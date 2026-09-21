package com.cloud.ums.controller.front;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloud.cms.domain.CmsProduct;
import com.cloud.cms.service.ICmsProductService;
import com.cloud.common.core.utils.StringUtils;
import com.cloud.common.core.web.controller.BaseController;
import com.cloud.common.core.web.page.TableDataInfo;
import com.cloud.common.log.annotation.Log;
import com.cloud.common.log.enums.BusinessType;
import com.cloud.common.security.utils.FrontSecurityUtils;
import com.cloud.ums.domain.AjaxResultResponse;
import com.cloud.ums.domain.UmsBrowseHistory;
import com.cloud.ums.domain.UmsUser;
import com.cloud.ums.service.IUmsBrowseHistoryService;
import com.cloud.ums.service.IUmsUserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 浏览记录Controller
 * 
 * @author cloud
 * @date 2026-03-15
 */
@RestController
@RequestMapping("/front/history")
public class UmsBrowseHistoryControllerFront extends BaseController {
    @Resource
    private IUmsBrowseHistoryService umsBrowseHistoryService;
    @Resource
    private IUmsUserService umsUserService;
    @Resource
    private ICmsProductService cmsProductService;

    /**
     * 查询浏览记录列表
     */
    @GetMapping("/list")
    public TableDataInfo list() {
        LambdaQueryWrapper<UmsBrowseHistory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(UmsBrowseHistory::getBrowseTime)
                .eq(UmsBrowseHistory::getUserId, FrontSecurityUtils.getUserId());
        List<UmsBrowseHistory> list = umsBrowseHistoryService.list(queryWrapper);
        list.forEach(browseHistory -> {
            // 获取用户信息并设置用户名
            String userId = browseHistory.getUserId();
            if (StringUtils.isNotEmpty(userId)) {
                UmsUser user = umsUserService.getById(userId);
                if (user != null) {
                    browseHistory.setUserName(user.getUserName());
                }
            }
            // 获取商品信息并设置商品图片和名称
            String productId = browseHistory.getProductId();
            if (StringUtils.isNotEmpty(productId)) {
                CmsProduct product = cmsProductService.getById(productId);
                if (product != null) {
                    browseHistory.setProductImage(product.getImageUrl());
                    browseHistory.setProductName(product.getName());
                }
            }
        });
        return getDataTable(list);
    }

    /**
     * 删除浏览记录
     */
    @Log(title = "浏览记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResultResponse<Boolean> remove(@PathVariable List<String> ids) {
        return AjaxResultResponse.success((umsBrowseHistoryService.removeBatchByIds(ids)));
    }

}
