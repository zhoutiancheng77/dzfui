package com.dzf.zxkj.platform.auth.service.impl.fallback;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SecretKeyServiceBack {
    public static String getPubKey(Throwable throwable){
        log.error("[鉴权中心-获取加密公钥] - [熔断] :{}", throwable.getStackTrace());
        return null;
    }
    public static String getPreKey(Throwable throwable){
        log.error("[鉴权中心-获取加密秘钥] - [熔断] :{}", throwable.getStackTrace());
        return null;
    }
    public static String getDefaultKey(Throwable throwable){
        log.error("[鉴权中心-获取加密默认秘钥] - [熔断] :{}", throwable.getStackTrace());
        return null;
    }
}
