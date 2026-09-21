package com.cloud.cms.controller.front;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloud.cms.domain.CmsProduct;
import com.cloud.cms.domain.CmsProductPromotion;
import com.cloud.cms.domain.CmsPromotion;
import com.cloud.cms.enums.ProductStatus;
import com.cloud.cms.service.ICmsProductPromotionService;
import com.cloud.cms.service.ICmsProductService;
import com.cloud.cms.service.ICmsPromotionService;
import com.cloud.common.core.utils.StringUtils;
import com.cloud.common.core.web.controller.BaseController;
import com.cloud.ums.domain.AjaxResultResponse;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 促销活动Controller
 * 
 * @author cloud
 * @date 2026-03-15
 */
@RestController
@RequestMapping("/front/promotion")
public class CmsPromotionControllerFront extends BaseController {
    @Resource
    private ICmsPromotionService cmsPromotionService;
    @Resource
    private ICmsProductPromotionService cmsProductPromotionService;
    @Resource
    private ICmsProductService cmsProductService;

    /**
     * 查询促销活动列表
     */
    @GetMapping("/list")
    public AjaxResultResponse<List<CmsPromotion>> list(CmsPromotion cmsPromotion) {
        LambdaQueryWrapper<CmsPromotion> queryWrapper = new LambdaQueryWrapper<>(cmsPromotion);
        queryWrapper.orderByDesc(CmsPromotion::getUpdateTime);
        List<CmsPromotion> list = cmsPromotionService.list(queryWrapper);
        return AjaxResultResponse.success(list);
    }

    /**
     * 获取促销活动详细信息
     */
    @GetMapping(value = "/{id}")
    public AjaxResultResponse<CmsPromotion> getInfo(@PathVariable("id") String promotionId) {
        if (StringUtils.isEmpty(promotionId)){
            return AjaxResultResponse.error("id不可为空", null);
        }
        CmsPromotion promotion = cmsPromotionService.getById(promotionId);
        //先拿到中间表再去拿商品
        LambdaQueryWrapper<CmsProductPromotion> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(CmsProductPromotion::getPromotionId, promotionId);
        List<CmsProductPromotion> productPromotionList = cmsProductPromotionService.list(lambdaQueryWrapper);
        if (CollUtil.isNotEmpty(productPromotionList)) {
            //中间表不为空，拿商品
            List<String> productIdList = productPromotionList.stream().map(CmsProductPromotion::getProductId).collect(Collectors.toList());
            if (CollUtil.isNotEmpty(productIdList)) {
                //拿商品
                LambdaQueryWrapper<CmsProduct> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(CmsProduct::getProductStatus, ProductStatus.UNSOLD.getValue())
                        .in(CmsProduct::getId, productIdList);
                promotion.setProductList(cmsProductService.list(queryWrapper));
            }
        }
        return AjaxResultResponse.success(promotion);
    }


}
