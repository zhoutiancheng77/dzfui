package com.dzf.zxkj.platform.auth.util;

import com.dzf.zxkj.common.entity.CachedLoginUser;
import com.dzf.zxkj.common.entity.Platform;
import com.dzf.zxkj.platform.auth.entity.LoginUser;

import java.util.Set;
import java.util.stream.Collectors;

public class LoginUserUtil {

    public static CachedLoginUser transform(LoginUser loginUser) {
        CachedLoginUser cachedLoginUser = new CachedLoginUser();

        cachedLoginUser.setUsername(loginUser.getUsername());
        cachedLoginUser.setUserid(loginUser.getUserid());
        cachedLoginUser.setToken(loginUser.getToken());
        cachedLoginUser.setKey(loginUser.getKey());
        cachedLoginUser.setDzfAuthToken(loginUser.getDzfAuthToken());
        cachedLoginUser.setVerify(loginUser.getVerify());

        if (loginUser.getPlatformVOSet() != null) {
            Set<Platform> platformSet = loginUser.getPlatformVOSet().stream().map(v -> {
                Platform platform = new Platform();
                platform.setPlatformName(v.getPlatformName());
                platform.setPlatformTag(v.getPlatformTag());
                platform.setPlatformIndexPage(v.getPlatformIndexPage());
                platform.setPlatformDomain(v.getPlatformDomain());
                platform.setShow(v.isShow());
                return platform;
            }).collect(Collectors.toSet());
            cachedLoginUser.setPlatformVOSet(platformSet);
        }


        return new CachedLoginUser();
    }

}
