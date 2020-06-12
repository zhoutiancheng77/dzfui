package com.dzf.zxkj.gateway.utils;


import com.dzf.zxkj.common.entity.CachedLoginUser;
import com.dzf.zxkj.gateway.cache.AuthCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AuthUtil {

    @Autowired
    private AuthCache authCache;

    public boolean validateMultipleLogin(String userid, String clientid) {
        boolean r = false;
        try {
            r = authCache.checkIsOnLine(userid) && authCache.checkIsMulti(userid, clientid) ;
        } catch (Exception e) {
            log.error(String.format("authCache方法：checkIsMulti异常: %s, 参数{userid：%s, clientid：%s}]", e.getMessage(), userid, clientid), e);
        }
        return r;
    }

    public boolean validateTokenEx(String userid, String clientId) {
        //过期返回true 结合redis实现
        CachedLoginUser loginUser = authCache.getLoginUser(userid);
        if (loginUser == null) {
            return true;
        }
        authCache.putLoginUser(userid, loginUser);
        authCache.putLoginUnique(userid, clientId);
        return false;
    }

}
