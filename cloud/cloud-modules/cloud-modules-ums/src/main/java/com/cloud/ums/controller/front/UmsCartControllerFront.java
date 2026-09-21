package com.cloud.ums.controller.front;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cloud.cms.domain.CmsProduct;
import com.cloud.cms.service.ICmsProductService;
import com.cloud.common.core.utils.StringUtils;
import com.cloud.common.core.web.controller.BaseController;
import com.cloud.common.core.web.page.TableDataInfo;
import com.cloud.common.security.utils.FrontSecurityUtils;
import com.cloud.ums.domain.AjaxResultResponse;
import com.cloud.ums.domain.UmsCart;
import com.cloud.ums.domain.UmsUser;
import com.cloud.ums.service.IUmsCartService;
import com.cloud.ums.service.IUmsUserService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;

/**
 * 购物车Controller
 * 
 * @author cloud
 * @date 2026-03-15
 */
@RestController
@RequestMapping("/front/cart")
public class UmsCartControllerFront extends BaseController {
    @Resource
    private IUmsCartService umsCartService;
    @Resource
    private IUmsUserService umsUserService;
    @Resource
    private ICmsProductService cmsProductService;

    /**
     * 查询用户购物车
     */
    @GetMapping("/list")
    public AjaxResultResponse<List<UmsCart>> list(UmsCart umsCart) {
        String userId = FrontSecurityUtils.getUserId();
        
        if (umsCart.getUserName() != null) {
            LambdaQueryWrapper<UmsUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.like(UmsUser::getUserName, umsCart.getUserName());
            UmsUser umsUser = umsUserService.getOne(lambdaQueryWrapper);
            if (umsUser != null) {
                userId = umsUser.getUserId();
            } else {
                return AjaxResultResponse.success(java.util.Collections.emptyList());
            }
        }
        
        LambdaQueryWrapper<UmsCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UmsCart::getUserId, userId)
                .orderByDesc(UmsCart::getUpdateTime);
        
        List<UmsCart> list = umsCartService.list(queryWrapper);
        list.forEach(cart -> {
            CmsProduct product = cmsProductService.getById(cart.getProductId());
            if (product != null) {
                cmsProductService.queryIsPromotion(product);
                product.setQuantity(String.valueOf(cart.getQuantity()));
                if (CollectionUtils.isEmpty(cart.getCmsProductList())) {
                    cart.setCmsProductList(new ArrayList<>());
                }
                cart.getCmsProductList().add(product);
            }
        });
        
        return AjaxResultResponse.success(list);
    }

    /**
     * 商品加入购物车
     */
    @PostMapping(value = "addProductInCart")
    public AjaxResultResponse<Boolean> addCart(@RequestBody UmsCart cart) {
        String productId = cart.getProductId();
        if (StringUtils.isBlank(productId)) {
            return AjaxResultResponse.error("id不可为空", null);
        }
        String userId = FrontSecurityUtils.getUserId();
        cart.setUserId(userId);
        UmsCart umsCart = quantityNum(userId, productId);
        //扣除数量,如果当前数量为1，那么删掉数据
        if (umsCart == null) {
            cart.setQuantity(1L);
            Date date = new Date();
            cart.setCreateTime(date);
            cart.setUpdateTime(date);
            return AjaxResultResponse.success(umsCartService.save(cart));
        } else if (umsCart.getQuantity() > 0L) {
            cart.setId(umsCart.getId());
            return AjaxResultResponse.success(umsCartService.updateById(cart));
        }
        return AjaxResultResponse.error("数据不规范", null);
    }

    /**
     * 商品从购物车中删除
     */
    @PostMapping(value = "removeProductInCart")
    public AjaxResultResponse<Boolean> removeProductInCart(@RequestBody UmsCart cart) {
        String productId = cart.getProductId();
        if (StringUtils.isBlank(productId)) {
            return AjaxResultResponse.error("id不可为空", null);
        }
        String userId = FrontSecurityUtils.getUserId();
        cart.setUserId(userId);
        UmsCart umsCart = quantityNum(userId, productId);
        Long currentQuantity = umsCart.getQuantity();
        cart.setId(umsCart.getId());
        //扣除数量,如果当前数量为1，那么删掉数据
        if (currentQuantity > 1L) {
            return AjaxResultResponse.success(umsCartService.updateById(cart));
        } else if (currentQuantity == 1L) {
            return AjaxResultResponse.success("删除成功", umsCartService.removeById(cart));
        }
        return AjaxResultResponse.error("数据不规范", null);
    }

    //查看当前购物车数量，如果数量为0那就是不存在
    private UmsCart quantityNum(String userId, String productId) {
        LambdaQueryWrapper<UmsCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UmsCart::getUserId, userId)
                .eq(UmsCart::getProductId, productId);
        return umsCartService.getOne(queryWrapper);
    }



}
