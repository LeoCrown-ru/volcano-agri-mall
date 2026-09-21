package com.cloud.cms.controller.backed;

import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloud.cms.domain.CmsProduct;
import com.cloud.cms.service.ICmsProductService;
import com.cloud.common.core.utils.StringUtils;
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
import com.cloud.cms.domain.CmsProductTrace;
import com.cloud.cms.service.ICmsProductTraceService;

/**
 * 商品溯源Controller
 * 
 * @author cloud
 * @date 2026-08-29
 */
@RestController
@RequestMapping("/productTrace")
public class CmsProductTraceController extends BaseController {
    @Resource
    private ICmsProductTraceService cmsProductTraceService;
    @Resource
    private ICmsProductService cmsProductService;

    /**
     * 查询商品溯源列表
     */
    @RequiresPermissions("@ss.hasPermi('cms:productTrace:list')")
    @GetMapping("/list")
    public TableDataInfo list(CmsProductTrace cmsProductTrace) {
        startPage();
        LambdaQueryWrapper<CmsProductTrace> queryWrapper = new LambdaQueryWrapper<>(cmsProductTrace);
        queryWrapper.orderByDesc(CmsProductTrace::getUpdateTime);
        List<CmsProductTrace> list = cmsProductTraceService.list(queryWrapper);
        list.forEach(trace -> {
            String productId = trace.getProductId();
            if (StringUtils.isNotEmpty(productId)) {
                CmsProduct product = cmsProductService.getById(productId);
                if (product != null) {
                    trace.setProductName(product.getName());
                }
            }
        });
        return getDataTable(list);
    }

    /**
     * 导出商品溯源列表
     */
    @RequiresPermissions("@ss.hasPermi('cms:productTrace:export')")
    @Log(title = "商品溯源", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CmsProductTrace cmsProductTrace) {
        LambdaQueryWrapper<CmsProductTrace> queryWrapper = new LambdaQueryWrapper<>(cmsProductTrace);
        queryWrapper.orderByDesc(CmsProductTrace::getUpdateTime);
        List<CmsProductTrace> list = cmsProductTraceService.list(queryWrapper);
        ExcelUtil<CmsProductTrace> util = new ExcelUtil<CmsProductTrace>(CmsProductTrace.class);
        util.exportExcel(response, list, "商品溯源数据");
    }

    /**
     * 获取商品溯源详细信息
     */
    @RequiresPermissions("@ss.hasPermi('cms:productTrace:query')")
    @GetMapping(value = "/{id}")
    public AjaxResultResponse<CmsProductTrace> getInfo(@PathVariable("id") String id) {
        return AjaxResultResponse.success(cmsProductTraceService.getById(id));
    }

    /**
     * 新增商品溯源
     */
    @RequiresPermissions("@ss.hasPermi('cms:productTrace:add')")
    @Log(title = "商品溯源", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResultResponse<Boolean> add(@RequestBody CmsProductTrace cmsProductTrace) {
        Date date = new Date();
        cmsProductTrace.setCreateTime(date);
        cmsProductTrace.setUpdateTime(date);
        return AjaxResultResponse.success(cmsProductTraceService.save(cmsProductTrace));
    }

    /**
     * 修改商品溯源
     */
    @RequiresPermissions("@ss.hasPermi('cms:productTrace:edit')")
    @Log(title = "商品溯源", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResultResponse<Boolean> edit(@RequestBody CmsProductTrace cmsProductTrace) {
        cmsProductTrace.setUpdateTime(new Date());
        return AjaxResultResponse.success(cmsProductTraceService.updateById(cmsProductTrace));
    }

    /**
     * 删除商品溯源
     */
    @RequiresPermissions("@ss.hasPermi('cms:productTrace:remove')")
    @Log(title = "商品溯源", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResultResponse<Boolean> remove(@PathVariable List<String> ids) {
        return AjaxResultResponse.success(cmsProductTraceService.removeBatchByIds(ids));
    }
}
