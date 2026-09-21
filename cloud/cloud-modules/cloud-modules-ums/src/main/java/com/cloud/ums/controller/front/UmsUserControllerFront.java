package com.cloud.ums.controller.front;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloud.common.core.utils.StringUtils;
import com.cloud.common.security.utils.FrontSecurityUtils;
import com.cloud.common.security.utils.SecurityUtils;
import com.cloud.oms.domain.OmsOrder;
import com.cloud.oms.domain.OmsOrderItem;
import com.cloud.oms.service.IOmsOrderItemService;
import com.cloud.oms.service.IOmsOrderService;
import com.cloud.ums.domain.AjaxResultResponse;
import com.cloud.ums.domain.UmsCart;
import com.cloud.ums.domain.UmsFavorite;
import com.cloud.ums.domain.UmsUser;
import com.cloud.ums.domain.dto.UmsAppLoginDTO;
import com.cloud.ums.domain.dto.UmsAppRegisterDTO;
import com.cloud.ums.domain.vo.LoginVO;
import com.cloud.ums.service.IUmsCartService;
import com.cloud.ums.service.IUmsFavoriteService;
import com.cloud.ums.service.IUmsUserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
     * 前台-用户注册登录登出模块
     */
//@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS})
@RestController
public class UmsUserControllerFront {

    @Resource
    private IUmsUserService umsUserService;
    @Resource
    private IOmsOrderService omsOrderService;
    @Resource
    private IOmsOrderItemService omsOrderItemService;
    @Resource
    private IUmsCartService umsCartService;
    @Resource
    private IUmsFavoriteService umsFavoriteService;

    /**
     * 注册
     */
    @PostMapping("/register")
    public AjaxResultResponse<Boolean> register(@RequestBody UmsAppRegisterDTO umsAppRegisterDTO) {
        String username = umsAppRegisterDTO.getUsername();
        String password = umsAppRegisterDTO.getPassword();
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            return AjaxResultResponse.error("账号密码不可为空", null);
        }
        if (umsUserService.getUmsUserByUserName(username) != null) {
            return AjaxResultResponse.error("用户名已存在", null);
        }
        UmsUser umsUser = new UmsUser();
        umsUser.setUserId(IdUtil.getSnowflakeNextIdStr());
        umsUser.setUserName(username);
        umsUser.setUserBalance(new BigDecimal(0));
        String encryptPassword = SecurityUtils.encryptPassword(password);
        umsUser.setUserPassword(encryptPassword);
        return AjaxResultResponse.success(umsUserService.save(umsUser));
    }

    /**
     * 登录
     */
    @PostMapping("/login")
    public AjaxResultResponse<LoginVO> login(@RequestBody UmsAppLoginDTO umsAppLoginDTO) {
        String username = umsAppLoginDTO.getUsername();
        String password = umsAppLoginDTO.getPassword();
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            return AjaxResultResponse.error("账号密码不可为空", null);
        }
        umsAppLoginDTO.setUsername(username);
        umsAppLoginDTO.setPassword(password);
        return AjaxResultResponse.success(umsUserService.appLogin(umsAppLoginDTO));
    }

    /**
     * 登出
     */
    @PostMapping("/logout")
    public AjaxResultResponse<Boolean> logout(){
        return AjaxResultResponse.success(umsUserService.logout());
    }

    /**
     * 登录后查看个人信息
     */
    @GetMapping("/getById")
    public AjaxResultResponse<UmsUser> getById(){
        String userId = FrontSecurityUtils.getUserId();
        UmsUser user = umsUserService.getById(userId);
        
        if (user != null) {
            // 计算订单数量
            LambdaQueryWrapper<OmsOrder> orderQuery = new LambdaQueryWrapper<>();
            orderQuery.eq(OmsOrder::getUserId, userId);
            user.setOrderCount((int) omsOrderService.count(orderQuery));
            
            // 计算购物车数量
            LambdaQueryWrapper<UmsCart> cartQuery = new LambdaQueryWrapper<>();
            cartQuery.eq(UmsCart::getUserId, userId);
            user.setCartCount((int) umsCartService.count(cartQuery));
            
            // 计算收藏数量
            LambdaQueryWrapper<UmsFavorite> favoriteQuery = new LambdaQueryWrapper<>();
            favoriteQuery.eq(UmsFavorite::getUserId, userId);
            user.setFavoriteCount((int) umsFavoriteService.count(favoriteQuery));
            
            // 计算评价数量
            LambdaQueryWrapper<OmsOrderItem> reviewQuery = new LambdaQueryWrapper<>();
            reviewQuery.eq(OmsOrderItem::getIsReview, true);
            user.setReviewCount((int) omsOrderItemService.count(reviewQuery));
        }
        
        return AjaxResultResponse.success(user);
    }

    /**
     * 用户充值
     */
    @PostMapping("/recharge")
    public AjaxResultResponse<Boolean> recharge(@RequestBody java.util.Map<String, Double> request) {
        Double amount = request.get("amount");
        if (amount == null || amount <= 0) {
            return AjaxResultResponse.error("充值金额必须大于0", null);
        }
        String userId = FrontSecurityUtils.getUserId();
        UmsUser umsUser = umsUserService.getById(userId);
        if (umsUser == null) {
            return AjaxResultResponse.error("用户不存在", null);
        }
        umsUser.setUserBalance(umsUser.getUserBalance().add(BigDecimal.valueOf(amount)));
        return AjaxResultResponse.success(umsUserService.updateById(umsUser));
    }

    /**
     * 登录后更改个人信息
     */
    @PostMapping("/updatePersonalData")
    public AjaxResultResponse<Boolean> updatePersonalData(@RequestBody UmsUser umsUser) {
        String password = umsUser.getUserPassword();
        if (StringUtils.isNotBlank(password)&&password.length()<=16) {
            String encryptPassword = SecurityUtils.encryptPassword(password);
            umsUser.setUserPassword(encryptPassword);
        }
        umsUser.setUserId(FrontSecurityUtils.getUserId());
        return AjaxResultResponse.success(umsUserService.updateById(umsUser));
    }


}
