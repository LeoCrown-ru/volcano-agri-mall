package com.cloud.cms.controller.backed;

import com.cloud.cms.domain.CmdIndexContent;
import com.cloud.cms.service.ICmdIndexContentService;
import com.cloud.common.core.web.controller.BaseController;
import com.cloud.common.log.annotation.Log;
import com.cloud.common.log.enums.BusinessType;
import com.cloud.common.security.annotation.RequiresPermissions;
import com.cloud.ums.domain.AjaxResultResponse;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 首页内容Controller
 */
@RestController
@RequestMapping("/content")
public class CmdIndexContentController extends BaseController {
    @Resource
    private ICmdIndexContentService cmdIndexContentService;

    /**
     * 获取首页内容详细信息
     */
    @RequiresPermissions("@ss.hasPermi('cms:content:query')")
    @GetMapping
    public AjaxResultResponse<CmdIndexContent> getInfo() {
        return AjaxResultResponse.success(cmdIndexContentService.getById(1));
    }

    /**
     * 修改首页内容
     */
    @RequiresPermissions("@ss.hasPermi('cms:content:edit')")
    @Log(title = "首页内容", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResultResponse<Boolean> edit(@RequestBody CmdIndexContent cmdIndexContent) {
        cmdIndexContent.setUpdateTime(new Date());
        return AjaxResultResponse.success(cmdIndexContentService.updateById(cmdIndexContent));
    }
}
