package com.dzf.zxkj.platform.auth.service.impl.fallback;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public class AuthServiceFallBack {
    public List<String> getPkCorpByUserId(Throwable throwable) {
        log.error("[鉴权中心-获取用户下公司列表] - [熔断] :{}", throwable.getStackTrace());
        return new ArrayList();
    }

    public Set<String> getAllPermission(Throwable throwable) {
        log.error("[鉴权中心-获取权限列表] - [熔断] :{}", throwable.getStackTrace());
        return new HashSet();
    }

    public Set<String> getPermisssionByUserid(Throwable throwable){
        log.error("[鉴权中心-获取用户权限列表] - [熔断] :{}", throwable.getStackTrace());
        return new HashSet();
    }
}
