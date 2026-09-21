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
import com.cloud.ums.domain.UmsFavorite;
import com.cloud.ums.domain.UmsUser;
import com.cloud.ums.service.IUmsFavoriteService;
import com.cloud.ums.service.IUmsUserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 收藏Controller
 * 
 * @author cloud
 * @date 2026-03-15
 */
@RestController
@RequestMapping("/front/favorite")
public class UmsFavoriteControllerFront extends BaseController {
    @Resource
    private IUmsFavoriteService umsFavoriteService;
    @Resource
    private IUmsUserService umsUserService;
    @Resource
    private ICmsProductService cmsProductService;

    /**
     * 查询收藏列表
     */
    @GetMapping("/list")
    public TableDataInfo list() {
        LambdaQueryWrapper<UmsFavorite> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(UmsFavorite::getUpdateTime)
                .eq(UmsFavorite::getUserId, FrontSecurityUtils.getUserId());
        List<UmsFavorite> list = umsFavoriteService.list(queryWrapper);
        list.forEach(favorite -> {
            // 获取用户信息并设置用户名
            String userId = favorite.getUserId();
            if (StringUtils.isNotEmpty(userId)) {
                UmsUser user = umsUserService.getById(userId);
                if (user != null) {
                    favorite.setUserName(user.getUserName());
                }
            }
            // 获取商品信息并设置商品图片和名称
            String productId = favorite.getProductId();
            if (StringUtils.isNotEmpty(productId)) {
                CmsProduct product = cmsProductService.getById(productId);
                if (product != null) {
                    favorite.setProductImage(product.getImageUrl());
                    favorite.setProductName(product.getName());
                }
            }
        });
        return getDataTable(list);
    }

    /**
     * 添加收藏
     */
    @PostMapping(value = "addCollect")
    public AjaxResultResponse<Boolean> addCollect(@RequestBody String productId) {
        String userId = FrontSecurityUtils.getUserId();
        
        LambdaQueryWrapper<UmsFavorite> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UmsFavorite::getUserId, userId)
                .eq(UmsFavorite::getProductId, productId);
        UmsFavorite existingFavorite = umsFavoriteService.getOne(queryWrapper);
        
        if (existingFavorite != null) {
            umsFavoriteService.removeById(existingFavorite.getId());
            return AjaxResultResponse.success(false);
        }
        
        UmsFavorite umsFavorite = new UmsFavorite();
        umsFavorite.setUserId(userId);
        umsFavorite.setProductId(productId);
        Date date = new Date();
        umsFavorite.setCreateTime(date);
        umsFavorite.setUpdateTime(date);
        return AjaxResultResponse.success(umsFavoriteService.save(umsFavorite));
    }

    /**
     * 删除收藏
     */
    @Log(title = "收藏", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResultResponse<Boolean> remove(@PathVariable List<String> ids) {
        return AjaxResultResponse.success((umsFavoriteService.removeBatchByIds(ids)));
    }


}
