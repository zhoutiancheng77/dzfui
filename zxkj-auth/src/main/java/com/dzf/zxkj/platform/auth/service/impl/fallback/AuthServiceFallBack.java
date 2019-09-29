package com.dzf.zxkj.platform.auth.service.impl.fallback;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class AuthServiceFallBack {
    public List<String> getPkCorpByUserId(Throwable throwable) {
        log.error("[鉴权中心-获取用户下公司列表] - [熔断] :{}", throwable.getStackTrace());
        return new ArrayList();
    }
}
