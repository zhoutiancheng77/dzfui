package com.dzf.zxkj.platform.auth.service.impl.fallback;

import com.dzf.zxkj.platform.auth.model.sys.CorpModel;
import com.dzf.zxkj.platform.auth.model.sys.UserModel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SysServiceFallBack {
    public UserModel queryByUserId(Throwable throwable) {
        log.error("[鉴权中心-查询用户信息] - [熔断] :{}", throwable.getStackTrace());
        return null;
    }
    public CorpModel queryCorpByPk(Throwable throwable) {
        log.error("[鉴权中心-查询公司信息] - [熔断] :{}", throwable.getStackTrace());
        return null;
    }
}
