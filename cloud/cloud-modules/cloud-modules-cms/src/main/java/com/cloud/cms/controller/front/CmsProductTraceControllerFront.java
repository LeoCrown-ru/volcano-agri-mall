package com.cloud.cms.controller.front;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloud.cms.domain.CmsProductTrace;
import com.cloud.cms.service.ICmsProductTraceService;
import com.cloud.common.core.utils.StringUtils;
import com.cloud.common.core.web.controller.BaseController;
import com.cloud.ums.domain.AjaxResultResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 商品溯源Controller（前台）
 * 
 * @author cloud
 * @date 2026-08-29
 */
@RestController
@RequestMapping("/front/productTrace")
public class CmsProductTraceControllerFront extends BaseController {
    @Resource
    private ICmsProductTraceService cmsProductTraceService;

    /**
     * 根据商品ID查询溯源信息
     */
    @GetMapping("/getByProductId/{productId}")
    public AjaxResultResponse<CmsProductTrace> getByProductId(@PathVariable("productId") String productId) {
        if (StringUtils.isEmpty(productId)) {
            return AjaxResultResponse.error("商品ID不可为空", null);
        }
        LambdaQueryWrapper<CmsProductTrace> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CmsProductTrace::getProductId, productId);
        queryWrapper.orderByDesc(CmsProductTrace::getUpdateTime);
        CmsProductTrace trace = cmsProductTraceService.getOne(queryWrapper);
        return AjaxResultResponse.success(trace);
    }
}
