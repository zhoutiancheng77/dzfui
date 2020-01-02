package com.dzf.zxkj.platform.auth.service.impl;

import com.dzf.auth.api.model.platform.PlatformVO;
import com.dzf.auth.api.model.user.UserVO;
import com.dzf.auth.api.service.INotifyService;
import com.dzf.zxkj.platform.auth.cache.AuthCache;
import com.dzf.zxkj.platform.auth.config.ZxkjPlatformAuthConfig;
import com.dzf.zxkj.platform.auth.entity.LoginUser;
import com.dzf.zxkj.platform.auth.model.sys.UserModel;
import com.dzf.zxkj.platform.auth.service.ISysService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;
import java.util.stream.Collectors;

@Service(version = "1.0.1", protocol = {"dubbo"},group="zxkj")
@Slf4j
public class INotifyServiceImpl implements INotifyService {

    @Autowired
    private AuthCache authCache;
    @Autowired
    private ISysService sysService;
    @Autowired
    private ZxkjPlatformAuthConfig zxkjPlatformAuthConfig;

    @Override
    public void notify(NotifyType type, String token, UserVO uservo) {
        log.info("接收统一登录通知------>", token);
        if(NotifyType.update.equals(type)){
            UserModel userModel = sysService.queryByUserName(uservo.getLoginName());
            LoginUser loginUser = authCache.getLoginUser(userModel.getCuserid());
            if(loginUser != null && !StringUtils.isBlank(loginUser.getDzfAuthToken()) && loginUser.getDzfAuthToken().equals(token)){
                Set<PlatformVO> list = uservo.getCanJumpPlatforms().stream().filter(k -> k.isShow() && !zxkjPlatformAuthConfig.getPlatformName().equals(k.getPlatformTag())).collect(Collectors.toSet());
                loginUser.setPlatformVOSet(list);
                authCache.putLoginUser(loginUser.getUserid(), loginUser);
            }
        }
    }
}
