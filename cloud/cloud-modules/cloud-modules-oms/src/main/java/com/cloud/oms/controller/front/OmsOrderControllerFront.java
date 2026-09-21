package com.cloud.oms.controller.front;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.cloud.cms.domain.CmsProduct;
import com.cloud.cms.domain.CmsProductPromotion;
import com.cloud.cms.domain.CmsPromotion;
import com.cloud.cms.service.ICmsProductPromotionService;
import com.cloud.cms.service.ICmsProductService;
import com.cloud.cms.service.ICmsPromotionService;
import com.cloud.common.core.exception.ServiceException;
import com.cloud.common.core.utils.StringUtils;
import com.cloud.common.core.web.controller.BaseController;
import com.cloud.common.log.annotation.Log;
import com.cloud.common.log.enums.BusinessType;
import com.cloud.common.security.annotation.RequiresPermissions;
import com.cloud.common.security.utils.FrontSecurityUtils;
import com.cloud.oms.domain.OmsOrder;
import com.cloud.oms.domain.OmsOrderItem;
import com.cloud.oms.domain.dto.OmsPlaceOrder;
import com.cloud.oms.domain.dto.OmsPlaceOrderProductItem;
import com.cloud.oms.enums.OrderStatus;
import com.cloud.oms.enums.PromotionType;
import com.cloud.oms.service.IOmsOrderItemService;
import com.cloud.oms.service.IOmsOrderService;
import com.cloud.ums.domain.AjaxResultResponse;
import com.cloud.ums.domain.UmsCart;
import com.cloud.ums.domain.UmsUser;
import com.cloud.ums.service.IUmsCartService;
import com.cloud.ums.service.IUmsUserService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 订单Controller
 * 
 * @author cloud
 * @date 2026-03-15
 */
@RestController
@RequestMapping("/front/order")
public class OmsOrderControllerFront extends BaseController {
    @Resource
    private IOmsOrderService omsOrderService;
    @Resource
    private IUmsUserService userService;
    @Resource
    private IOmsOrderItemService orderItemService;
    @Resource
    private ICmsProductService productService;
    @Resource
    private IUmsCartService cartService;
    @Resource
    private ICmsProductPromotionService productPromotionService;
    @Resource
    private ICmsPromotionService promotionService;

    /**
     * 我的订单
     */
    @GetMapping("/list")
    public AjaxResultResponse<List<OmsOrder>> list(OmsOrder omsOrder) {
        LambdaQueryWrapper<OmsOrder> queryWrapper = new LambdaQueryWrapper<>(omsOrder);
        queryWrapper.orderByDesc(OmsOrder::getUpdateTime)
                .eq(OmsOrder::getUserId, FrontSecurityUtils.getUserId())
                .eq(OmsOrder::getDelFlag, 0);
        List<OmsOrder> list = omsOrderService.list(queryWrapper);
        list.forEach(order -> {
            String orderId = order.getId();
            OmsOrderControllerFront.getOrderItemByOrderId(orderId, order, orderItemService, productService, userService);
        });
        return AjaxResultResponse.success(list);
    }

    /**
     * 获取订单详细信息
     */
    @GetMapping(value = "/{id}")
    public AjaxResultResponse<OmsOrder> getInfo(@PathVariable("id") String orderId) {
        if (StringUtils.isEmpty(orderId)){
            return AjaxResultResponse.error("id不可为空", null);
        }
        LambdaQueryWrapper<OmsOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OmsOrder::getId, orderId).eq(OmsOrder::getDelFlag, 0);
        OmsOrder order = omsOrderService.getOne(queryWrapper);
        if (order == null) {
            return AjaxResultResponse.error("订单不存在", null);
        }
        getOrderItemByOrderId(orderId, order, orderItemService, productService, userService);
        return AjaxResultResponse.success(order);
    }

    /**
     * 删除订单（软删除：仅限本人，打删除标记，后台仍可查看）
     */
    @DeleteMapping("/{ids}")
    public AjaxResultResponse<Boolean> remove(@PathVariable("ids") List<String> ids) {
        if (CollUtil.isEmpty(ids)){
            return AjaxResultResponse.error("id不可为空", null);
        }
        String userId = FrontSecurityUtils.getUserId();
        for (String orderId : ids) {
            OmsOrder order = omsOrderService.getById(orderId);
            if (order == null) {
                continue;
            }
            if (!Objects.equals(order.getUserId(), userId)) {
                return AjaxResultResponse.error("只能删除自己的订单", null);
            }
            // 软删除：打删除标记，数据保留供后台查看
            LambdaUpdateWrapper<OmsOrder> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(OmsOrder::getId, orderId)
                    .set(OmsOrder::getDelFlag, 1);
            omsOrderService.update(updateWrapper);
        }
        return AjaxResultResponse.success(true);
    }

    public static void getOrderItemByOrderId(String orderId, OmsOrder order, IOmsOrderItemService orderItemService, ICmsProductService productService, IUmsUserService userService) {
        LambdaQueryWrapper<OmsOrderItem> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(OmsOrderItem::getOrderId, orderId);
        List<OmsOrderItem> omsOrderItemList = orderItemService.list(lambdaQueryWrapper);
        omsOrderItemList.forEach(orderItem -> {
            String productId = orderItem.getProductId();
            orderItem.setCmsProduct(productService.getById(productId));
        });
        order.setOmsOrderItemList(omsOrderItemList);
        UmsUser umsUser = userService.getById(order.getUserId());
        if (umsUser!=null){
            order.setUserName(umsUser.getUserName());
        }
    }

    /**
     * 初始化创建订单
     */
    @PostMapping("initOrder")
    @Transactional
    public AjaxResultResponse<OmsOrder> initOrder(@RequestBody OmsPlaceOrder omsPlaceOrder) {
        verify(omsPlaceOrder);
        String userId = FrontSecurityUtils.getUserId();

        // 促销价格重算（首购享活动价，复购原价）
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OmsPlaceOrderProductItem item : omsPlaceOrder.getOmsPlaceOrderProductItemList()) {
            BigDecimal realPrice = getPromotionPrice(userId, item.getProductId(), item.getPrice());
            item.setPrice(realPrice);
            totalAmount = totalAmount.add(realPrice.multiply(BigDecimal.valueOf(item.getQuantity())));
        }
        totalAmount = totalAmount.setScale(2, RoundingMode.HALF_UP);

        // 订单生成
        OmsOrder order = new OmsOrder();
        String orderId = IdUtil.getSnowflakeNextIdStr();
        order.setId(orderId);
        order.setUserId(userId);
        order.setUserAddress(omsPlaceOrder.getUserAddress());
        order.setTotalAmount(totalAmount);
        order.setPaymentMethod(omsPlaceOrder.getPaymentMethod());
        order.setOrderStatus(OrderStatus.UNPAYMENT.getValue()); // 初始状态为未支付
        Date date = new Date();
        order.setCreateTime(date);
        order.setUpdateTime(date);
        omsOrderService.save(order);

        // 创建订单商品项
        List<OmsOrderItem> omsOrderItemList = new ArrayList<>();
        for (OmsPlaceOrderProductItem item : omsPlaceOrder.getOmsPlaceOrderProductItemList()) {
            OmsOrderItem omsOrderItem = new OmsOrderItem();
            omsOrderItem.setId(IdUtil.getSnowflakeNextIdStr());
            omsOrderItem.setOrderId(orderId);
            omsOrderItem.setProductId(item.getProductId());
            omsOrderItem.setQuantity(item.getQuantity());
            omsOrderItem.setPrice(item.getPrice());
            omsOrderItem.setIsReview(false);
            omsOrderItem.setCreateTime(date);
            omsOrderItem.setUpdateTime(date);
            omsOrderItemList.add(omsOrderItem);
        }
        orderItemService.saveBatch(omsOrderItemList);

        return AjaxResultResponse.success(order);
    }

    /**
     * 计算商品促销后的实际单价（折扣：单价×折扣率；优惠：单价-优惠金额）
     * 规则：活动有效期内，首次购买享活动价，复购按原价；活动期外一律原价
     */
    private BigDecimal getPromotionPrice(String userId, String productId, BigDecimal originalPrice) {
        LambdaQueryWrapper<CmsProductPromotion> ppWrapper = new LambdaQueryWrapper<>();
        ppWrapper.eq(CmsProductPromotion::getProductId, productId);
        CmsProductPromotion productPromotion = productPromotionService.getOne(ppWrapper);
        if (productPromotion == null) {
            return originalPrice;
        }
        CmsPromotion promotion = promotionService.getById(productPromotion.getPromotionId());
        if (promotion == null || !isActivityActive(promotion)) {
            return originalPrice;
        }
        // 已购买过该活动商品的用户，复购按原价
        if (hasBoughtActivityProduct(userId, productPromotion.getPromotionId())) {
            return originalPrice;
        }
        BigDecimal price = originalPrice;
        Long type = promotion.getType();
        if (Objects.equals(type, PromotionType.ZK.getValue())) {
            price = originalPrice.multiply(promotion.getDiscount());
        } else if (Objects.equals(type, PromotionType.YH.getValue())) {
            price = originalPrice.subtract(promotion.getCouponValue());
            if (price.compareTo(BigDecimal.ZERO) < 0) {
                price = BigDecimal.ZERO;
            }
        }
        return price.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 该用户是否已购买过某活动下的商品（已取消订单不计）
     */
    private boolean hasBoughtActivityProduct(String userId, String promotionId) {
        LambdaQueryWrapper<CmsProductPromotion> allWrapper = new LambdaQueryWrapper<>();
        allWrapper.eq(CmsProductPromotion::getPromotionId, promotionId);
        List<CmsProductPromotion> activityList = productPromotionService.list(allWrapper);
        List<String> activityProductIds = activityList.stream()
                .map(CmsProductPromotion::getProductId).collect(Collectors.toList());
        LambdaQueryWrapper<OmsOrder> orderWrapper = new LambdaQueryWrapper<>();
        orderWrapper.eq(OmsOrder::getUserId, userId)
                .ne(OmsOrder::getOrderStatus, OrderStatus.CANCEL.getValue())
                .eq(OmsOrder::getDelFlag, 0);
        List<OmsOrder> userOrders = omsOrderService.list(orderWrapper);
        if (CollUtil.isEmpty(userOrders)) {
            return false;
        }
        List<String> userOrderIds = userOrders.stream().map(OmsOrder::getId).collect(Collectors.toList());
        LambdaQueryWrapper<OmsOrderItem> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.in(OmsOrderItem::getOrderId, userOrderIds)
                .in(OmsOrderItem::getProductId, activityProductIds);
        return orderItemService.count(itemWrapper) > 0;
    }

    /**
     * 活动是否处于有效期内
     */
    private boolean isActivityActive(CmsPromotion promotion) {
        Date now = new Date();
        if (promotion.getStartTime() != null && now.before(promotion.getStartTime())) {
            return false;
        }
        return promotion.getEndTime() == null || !now.after(promotion.getEndTime());
    }

    /**
     * 商品下单
     */
    @PostMapping("placeOrder")
    @Transactional
    public AjaxResultResponse<Boolean> placeOrder(@RequestBody OmsPlaceOrder omsPlaceOrder) {
        verify(omsPlaceOrder);
        // 执行真正的下单逻辑
        return AjaxResultResponse.success(createOrder(omsPlaceOrder));
    }


    public void verify(OmsPlaceOrder omsPlaceOrder) {
        // 校验商品清单
        List<OmsPlaceOrderProductItem> omsPlaceOrderProductItemList = omsPlaceOrder.getOmsPlaceOrderProductItemList();
        if (CollUtil.isEmpty(omsPlaceOrderProductItemList)) {
            throw new ServiceException("下单时商品清单不可为空", 500);
        }

        // 校验地址和支付方式
        String paymentMethod = omsPlaceOrder.getPaymentMethod();
        if (StringUtils.isEmpty(paymentMethod)) {
            throw new ServiceException("支付方式不可为空", 500);
        }

        // 校验金额
        BigDecimal totalAmount = omsPlaceOrder.getTotalAmount();
        if (totalAmount == null) {
            throw new ServiceException("金额不可为空", 500);
        }
    }

    /**
     * 真正的下单逻辑
     */
    private boolean createOrder(OmsPlaceOrder omsPlaceOrder) {
        // 获取订单
        String orderId = omsPlaceOrder.getOrderId();
        OmsOrder order = omsOrderService.getById(orderId);
        if (order == null) {
            throw new ServiceException("订单不存在", 500);
        }

        // 余额校验（以订单表重算后的金额为准）
        BigDecimal totalAmount = order.getTotalAmount();
        UmsUser umsUser = userService.getById(FrontSecurityUtils.getUserId());
        BigDecimal userBalance = umsUser.getUserBalance();
        if (totalAmount.compareTo(userBalance) > 0) {
            throw new ServiceException("用户余额不足", 500);
        }

        // 库存校验和扣除
        for (OmsPlaceOrderProductItem item : omsPlaceOrder.getOmsPlaceOrderProductItemList()) {
            String productId = item.getProductId();
            Long quantity = item.getQuantity();

            // 库存校验
            CmsProduct cmsProduct = productService.getById(productId);
            Long stock = cmsProduct.getStock();
            if (quantity.compareTo(stock) > 0) {
                throw new ServiceException(cmsProduct.getName() + "库存不足，请去除该商品重新下单", 500);
            }

            // 扣减库存
            cmsProduct.setStock(stock - quantity);
            productService.updateById(cmsProduct);

            //是购物车，下单完毕要把购物车的给删了
            LambdaQueryWrapper<UmsCart> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(UmsCart::getUserId, FrontSecurityUtils.getUserId())
                    .eq(UmsCart::getProductId, productId);
            cartService.remove(queryWrapper);
        }

        // 余额扣除
        umsUser.setUserBalance(userBalance.subtract(totalAmount));
        userService.updateById(umsUser);

        // 更新订单状态为已支付
        order.setOrderStatus(OrderStatus.PAID.getValue());
        order.setUserAddress(omsPlaceOrder.getUserAddress());
        omsOrderService.updateById(order);

        return true;
    }


    /**
     * 取消订单
     */
    @PutMapping
    public AjaxResultResponse<Boolean> cancelOrder(@RequestBody OmsOrder omsOrder) {
        omsOrder.setUpdateTime(new Date());
        return AjaxResultResponse.success(omsOrderService.updateById(omsOrder));
    }

}
