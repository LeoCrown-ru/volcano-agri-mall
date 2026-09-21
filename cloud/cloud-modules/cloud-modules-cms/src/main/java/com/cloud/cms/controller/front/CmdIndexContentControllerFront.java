package com.cloud.cms.controller.front;

import com.cloud.cms.domain.CmdIndexContent;
import com.cloud.cms.service.ICmdIndexContentService;
import com.cloud.common.core.web.controller.BaseController;
import com.cloud.ums.domain.AjaxResultResponse;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 首页内容Controller
 */
@RestController
@RequestMapping("/front/content")
public class CmdIndexContentControllerFront extends BaseController {
    @Resource
    private ICmdIndexContentService cmdIndexContentService;

    /**
     * 获取首页内容详细信息
     */
    @GetMapping
    public AjaxResultResponse<CmdIndexContent> getInfo() {
        return AjaxResultResponse.success(cmdIndexContentService.getById(1));
    }


}
