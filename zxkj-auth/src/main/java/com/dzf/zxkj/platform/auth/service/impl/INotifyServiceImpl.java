package com.dzf.zxkj.platform.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzf.auth.api.model.platform.PlatformVO;
import com.dzf.auth.api.model.user.UserVO;
import com.dzf.auth.api.service.INotifyService;
import com.dzf.zxkj.auth.model.sys.UserModel;
import com.dzf.zxkj.common.entity.CachedLoginUser;
import com.dzf.zxkj.common.entity.Platform;
import com.dzf.zxkj.platform.auth.cache.AuthCache;
import com.dzf.zxkj.platform.auth.config.ZxkjPlatformAuthConfig;
import com.dzf.zxkj.platform.auth.mapper.UserMapper;
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
    @Autowired
    private UserMapper userMapper;

    private UserModel queryUser(String unifiedid) {
        QueryWrapper<UserModel> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(UserModel::getUnifiedid, unifiedid).and(condition -> condition.eq(UserModel::getDr, "0").or().isNull(UserModel::getDr));
        return userMapper.selectOne(queryWrapper);
    }

    @Override
    public void notify(NotifyType type, String token, UserVO uservo) {
        log.info("接收统一登录通知------>", token);
        if(NotifyType.update.equals(type)){
            if(StringUtils.isBlank(uservo.getPlatformUserId())){
                UserModel userModel = queryUser(String.valueOf(uservo.getId()));
                uservo.setPlatformUserId(userModel.getCuserid());
            }
            CachedLoginUser loginUser = authCache.getLoginUser(uservo.getPlatformUserId());
            if(loginUser != null && !StringUtils.isBlank(loginUser.getDzfAuthToken()) && loginUser.getDzfAuthToken().equals(token)){
                Set<PlatformVO> list = uservo.getCanJumpPlatforms().stream().filter(k -> k.isShow() && !zxkjPlatformAuthConfig.getPlatformName().equals(k.getPlatformTag())).collect(Collectors.toSet());
                loginUser.setPlatformVOSet(list.stream().map(v -> {
                    Platform platform = new Platform();
                    platform.setPlatformName(v.getPlatformName());
                    platform.setPlatformTag(v.getPlatformTag());
                    platform.setPlatformIndexPage(v.getPlatformIndexPage());
                    platform.setPlatformDomain(v.getPlatformDomain());
                    platform.setShow(v.isShow());
                    return platform;
                }).collect(Collectors.toSet()));
                authCache.putLoginUser(loginUser.getUserid(), loginUser);
            }
        }
    }
}
