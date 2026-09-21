package com.cloud.cms.controller.backed;

import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloud.common.core.utils.poi.ExcelUtil;
import com.cloud.common.core.web.controller.BaseController;
import com.cloud.common.core.web.page.TableDataInfo;
import com.cloud.common.log.annotation.Log;
import com.cloud.common.log.enums.BusinessType;
import com.cloud.common.security.annotation.RequiresPermissions;
import com.cloud.ums.domain.AjaxResultResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.cloud.cms.domain.CmsProductPromotion;
import com.cloud.cms.service.ICmsProductPromotionService;

/**
 * 商品促销关联Controller
 * 
 * @author cloud
 * @date 2026-03-15
 */
@RestController
@RequestMapping("/link")
public class CmsProductPromotionController extends BaseController {
    @Resource
    private ICmsProductPromotionService cmsProductPromotionService;

    /**
     * 查询商品促销关联列表
     */
    @RequiresPermissions("@ss.hasPermi('cms:promotion:list')")
    @GetMapping("/list")
    public TableDataInfo list(CmsProductPromotion cmsProductPromotion) {
        startPage();
        LambdaQueryWrapper<CmsProductPromotion> queryWrapper = new LambdaQueryWrapper<>(cmsProductPromotion);
        List<CmsProductPromotion> list = cmsProductPromotionService.list(queryWrapper);
        return getDataTable(list);
    }

    /**
     * 导出商品促销关联列表
     */
    @RequiresPermissions("@ss.hasPermi('cms:promotion:export')")
    @Log(title = "商品促销关联", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CmsProductPromotion cmsProductPromotion) {
        LambdaQueryWrapper<CmsProductPromotion> queryWrapper = new LambdaQueryWrapper<>(cmsProductPromotion);
        queryWrapper.orderByDesc(CmsProductPromotion::getUpdateTime);
        List<CmsProductPromotion> list = cmsProductPromotionService.list(queryWrapper);
        ExcelUtil<CmsProductPromotion> util = new ExcelUtil<CmsProductPromotion>(CmsProductPromotion.class);
        util.exportExcel(response, list, "商品促销关联数据");
    }

    /**
     * 获取商品促销关联详细信息
     */
    @RequiresPermissions("@ss.hasPermi('cms:promotion:query')")
    @GetMapping(value = "/{id}")
    public AjaxResultResponse<CmsProductPromotion> getInfo(@PathVariable("id") String id) {
        return AjaxResultResponse.success(cmsProductPromotionService.getById(id));
    }

    /**
     * 新增商品促销关联
     */
    @RequiresPermissions("@ss.hasPermi('cms:promotion:add')")
    @Log(title = "商品促销关联", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResultResponse<Boolean> add(@RequestBody CmsProductPromotion cmsProductPromotion) {
        Date date = new Date();
        cmsProductPromotion.setCreateTime(date);
        cmsProductPromotion.setUpdateTime(date);
        return AjaxResultResponse.success(cmsProductPromotionService.save(cmsProductPromotion));
    }

    /**
     * 修改商品促销关联
     */
    @RequiresPermissions("@ss.hasPermi('cms:promotion:edit')")
    @Log(title = "商品促销关联", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResultResponse<Boolean> edit(@RequestBody CmsProductPromotion cmsProductPromotion) {
        cmsProductPromotion.setUpdateTime(new Date());
        return AjaxResultResponse.success(cmsProductPromotionService.updateById(cmsProductPromotion));
    }

    /**
     * 删除商品促销关联
     */
    @RequiresPermissions("@ss.hasPermi('cms:promotion:remove')")
    @Log(title = "商品促销关联", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResultResponse<Boolean> remove(@PathVariable List<String> ids) {
        return AjaxResultResponse.success((cmsProductPromotionService.removeBatchByIds(ids)));
    }
}
