package com.cloud.ums.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.common.core.exception.ServiceException;
import com.cloud.common.redis.service.RedisService;
import com.cloud.common.security.utils.SecurityUtils;
import com.cloud.ums.domain.UmsUser;
import com.cloud.ums.domain.dto.UmsAppLoginDTO;
import com.cloud.ums.domain.dto.UmsAppRegisterDTO;
import com.cloud.ums.domain.vo.LoginVO;
import com.cloud.ums.mapper.UmsUserMapper;
import com.cloud.ums.service.IUmsUserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.UUID;

/**
 * 平台用户Service业务层处理
 *
 * @author haiziohhue
 * @date 2026-10-03
 */
@Service
public class UmsUserServiceImpl extends ServiceImpl<UmsUserMapper, UmsUser> implements IUmsUserService {

    @Resource
    private RedisService redisService;

    public UmsUser getUmsUserByUserName(String username){
        LambdaQueryWrapper<UmsUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UmsUser::getUserName, username);
        return getOne(queryWrapper);
    }

    @Override
    public Boolean register(UmsAppRegisterDTO umsAppRegisterDTO) {
        UmsUser umsUser = BeanUtil.copyProperties(umsAppRegisterDTO, UmsUser.class);
        String encryptPassword = SecurityUtils.encryptPassword(umsAppRegisterDTO.getPassword());
        umsUser.setUserPassword(encryptPassword);
        return save(umsUser);
    }

    @Override
    public LoginVO appLogin(UmsAppLoginDTO umsAppLoginDTO) {
        LoginVO loginVO = new LoginVO();
        String username = umsAppLoginDTO.getUsername();
        String password = umsAppLoginDTO.getPassword();
        UmsUser umsUser = getUmsUserByUserName(username);
        if (umsUser==null){
            throw new ServiceException("用户名不存在，请注册");
        }
        if (!SecurityUtils.matchesPassword(password, umsUser.getUserPassword())){
            throw new ServiceException("密码错误");
        }
        String token = UUID.randomUUID().toString().replace("-","");
        loginVO.setToken(token);
        loginVO.setUserAvatar(umsUser.getUserAvatar());
        loginVO.setUser(umsUser);
        loginDataSetRedis(token, umsUser);
        return loginVO;
    }

    public void loginDataSetRedis(String token, UmsUser user) {
        String json = JSONUtil.toJsonStr(user);
        redisService.set(token, json);
    }

    @Override
    public Boolean logout() {
        //删掉redis登录信息
        String token = SecurityUtils.getToken();
        redisService.remove(token);
        return true;
    }


}
