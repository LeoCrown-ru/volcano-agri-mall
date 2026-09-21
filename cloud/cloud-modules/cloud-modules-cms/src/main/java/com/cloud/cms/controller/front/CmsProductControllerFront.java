package com.cloud.cms.controller.front;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloud.cms.domain.CmsProduct;
import com.cloud.cms.domain.CmsProductCategory;
import com.cloud.cms.domain.CmsProductPromotion;
import com.cloud.cms.enums.ProductStatus;
import com.cloud.cms.service.ICmsProductCategoryService;
import com.cloud.cms.service.ICmsProductPromotionService;
import com.cloud.cms.service.ICmsProductService;
import com.cloud.oms.domain.OmsOrder;
import com.cloud.oms.domain.OmsOrderItem;
import com.cloud.oms.enums.OrderStatus;
import com.cloud.oms.service.IOmsOrderItemService;
import com.cloud.oms.service.IOmsOrderService;
import com.cloud.ums.domain.UmsBrowseHistory;
import com.cloud.common.core.utils.StringUtils;
import com.cloud.common.core.web.controller.BaseController;
import com.cloud.common.security.utils.FrontSecurityUtils;
import com.cloud.ums.domain.AjaxResultResponse;
import com.cloud.ums.domain.UmsFavorite;
import com.cloud.ums.domain.UmsProductReview;
import com.cloud.ums.service.IUmsBrowseHistoryService;
import com.cloud.ums.service.IUmsFavoriteService;
import com.cloud.ums.service.IUmsProductReviewService;
import com.cloud.ums.service.IUmsUserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商品Controller
 * 
 * @author cloud
 * @date 2026-03-15
 */
@RestController
@RequestMapping("/front/product")
public class CmsProductControllerFront extends BaseController {
    @Resource
    private ICmsProductService cmsProductService;
    @Resource
    private ICmsProductCategoryService cmsProductCategoryService;
    @Resource
    private ICmsProductPromotionService productPromotionService;
    @Resource
    private IOmsOrderItemService omsOrderItemService;
    @Resource
    private IOmsOrderService omsOrderService;
    @Resource
    private IUmsBrowseHistoryService umsBrowseHistoryService;
    @Resource
    private IUmsProductReviewService umsProductReviewService;
    @Resource
    private IUmsUserService umsUserService;
    @Resource
    private IUmsFavoriteService umsFavoriteService;

    /**
     * 查询商品列表
     */
    @GetMapping("/list")
    public AjaxResultResponse<List<CmsProduct>> list(CmsProduct cmsProduct) {
        LambdaQueryWrapper<CmsProduct> queryWrapper = new LambdaQueryWrapper<>(cmsProduct);
        queryWrapper.orderByDesc(CmsProduct::getUpdateTime)
                .eq(CmsProduct::getProductStatus, ProductStatus.UNSOLD.getValue());
        List<CmsProduct> list = cmsProductService.list(queryWrapper);
        return AjaxResultResponse.success(list);
    }

    /**
     * 搜索商品
     */
    @GetMapping(value = "/searchProduct")
    public AjaxResultResponse<List<CmsProduct>> searchProduct(
            @RequestParam(value = "searchContent", required = false) String searchContent,
            @RequestParam(value = "categoryId", required = false) String categoryId,
            @RequestParam(value = "sort", required = false, defaultValue = "hot") String sort ) {
        LambdaQueryWrapper<CmsProduct> queryWrapper = new LambdaQueryWrapper<>();
        
        if ("price_asc".equals(sort)) {
            queryWrapper.orderByAsc(CmsProduct::getPrice);
        } else if ("price_desc".equals(sort)) {
            queryWrapper.orderByDesc(CmsProduct::getPrice);
        } else if ("hot".equals(sort)) {
            queryWrapper.orderByDesc(CmsProduct::getCreateTime);
        } else {
            queryWrapper.orderByDesc(CmsProduct::getUpdateTime);
        }
        
        if (StringUtils.isNotBlank(searchContent)){
            queryWrapper.and(wrapper -> wrapper.like(CmsProduct::getName, searchContent)
                    .or().like(CmsProduct::getDescription, searchContent));
        }
        if (StringUtils.isNotBlank(categoryId)) {
            List<String> categoryIds = new ArrayList<>();
            categoryIds.add(categoryId);
            collectChildCategoryIds(categoryId, categoryIds);
            queryWrapper.in(CmsProduct::getCategoryId, categoryIds);
        }
        List<CmsProduct> cmsProductList = cmsProductService.list(queryWrapper);
        return AjaxResultResponse.success(cmsProductList);
    }

    /**
     * 递归收集某分类下的所有子分类ID（用于按父分类搜索商品）
     */
    private void collectChildCategoryIds(String parentId, List<String> ids) {
        LambdaQueryWrapper<CmsProductCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CmsProductCategory::getParentId, parentId);
        List<CmsProductCategory> children = cmsProductCategoryService.list(wrapper);
        for (CmsProductCategory child : children) {
            ids.add(child.getId());
            collectChildCategoryIds(child.getId(), ids);
        }
    }

    /**
     * 获取商品详细信息-需登录
     */
    @GetMapping(value = "/getById/{id}")
    public AjaxResultResponse<CmsProduct> getInfo(@PathVariable("id") String productId) {
        if (StringUtils.isEmpty(productId)){
            return AjaxResultResponse.error("id不可为空", null);
        }
        CmsProduct cmsProduct = cmsProductService.getById(productId);
        LambdaQueryWrapper<UmsProductReview> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UmsProductReview::getProductId, productId);
        queryWrapper.orderByDesc(UmsProductReview::getUpdateTime);
        List<UmsProductReview> list = umsProductReviewService.list(queryWrapper);
        list.forEach(productReview -> {
            productReview.setUserName(umsUserService.getById(productReview.getUserId()).getUserName());
        });
        cmsProduct.setProductReviewList(list);
        //查询是否收藏
        LambdaQueryWrapper<UmsFavorite> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UmsFavorite::getProductId, productId)
                .eq(UmsFavorite::getUserId, FrontSecurityUtils.getUserId());
        UmsFavorite umsFavorite = umsFavoriteService.getOne(lambdaQueryWrapper);
        cmsProduct.setIsCollect(umsFavorite != null);
        //查询是否是促销产品
        cmsProductService.queryIsPromotion(cmsProduct);
        // 首购优惠：已购买过该活动商品的用户，详情页按原价展示
        if (cmsProduct.getIsDiscount() != null) {
            String userId = FrontSecurityUtils.getUserId();
            if (StringUtils.isNotEmpty(userId) && !"0".equals(userId)) {
                LambdaQueryWrapper<CmsProductPromotion> ppWrapper = new LambdaQueryWrapper<>();
                ppWrapper.eq(CmsProductPromotion::getProductId, productId);
                CmsProductPromotion productPromotion = productPromotionService.getOne(ppWrapper);
                if (productPromotion != null && hasBoughtActivityProduct(userId, productPromotion.getPromotionId())) {
                    cmsProduct.setIsDiscount(null);
                    cmsProduct.setDiscountNum(null);
                }
            }
        }
        //异步记录用户浏览记录
        String userId = FrontSecurityUtils.getUserId();
        if (StringUtils.isNotEmpty(userId)&&!"0".equals(userId) ){
            umsBrowseHistoryService.asyncRecordBrowseHistory(userId,productId);
        }
        // 统计：浏览量、已售数量、评价数量
        LambdaQueryWrapper<UmsBrowseHistory> browseWrapper = new LambdaQueryWrapper<>();
        browseWrapper.eq(UmsBrowseHistory::getProductId, productId);
        cmsProduct.setViewCount(umsBrowseHistoryService.count(browseWrapper));
        LambdaQueryWrapper<OmsOrderItem> saleWrapper = new LambdaQueryWrapper<>();
        saleWrapper.eq(OmsOrderItem::getProductId, productId);
        cmsProduct.setSaleCount(omsOrderItemService.count(saleWrapper));
        cmsProduct.setReviewCount((long) list.size());
        return AjaxResultResponse.success(cmsProduct);
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
        if (userOrders == null || userOrders.isEmpty()) {
            return false;
        }
        List<String> userOrderIds = userOrders.stream().map(OmsOrder::getId).collect(Collectors.toList());
        LambdaQueryWrapper<OmsOrderItem> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.in(OmsOrderItem::getOrderId, userOrderIds)
                .in(OmsOrderItem::getProductId, activityProductIds);
        return omsOrderItemService.count(itemWrapper) > 0;
    }




}
