package com.dzf.zxkj.gateway.cache;

import com.alicp.jetcache.Cache;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.CreateCache;
import com.dzf.zxkj.common.constant.AuthConstant;
import com.dzf.zxkj.common.entity.CachedLoginUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@Slf4j
public class AuthCache {

    @CreateCache(name = AuthConstant.AUTH_PREFIX, cacheType = CacheType.REMOTE, expire = 8, timeUnit = TimeUnit.HOURS)
    private Cache<String, CachedLoginUser> platformUserCache;

    @CreateCache(name = AuthConstant.AUTH_ONLINE, cacheType = CacheType.REMOTE, expire = 8, timeUnit = TimeUnit.HOURS)
    private Cache<String, String> platformUserOnlineCache;

    public CachedLoginUser getLoginUser(String userid){
        return platformUserCache.get(userid);
    }

    public void putLoginUser(String userid, CachedLoginUser loginUser){
        platformUserCache.put(userid, loginUser);
    }

    public void putLoginUnique(String userid, String clientId){
        platformUserOnlineCache.put(userid, clientId);
    }

    public boolean checkIsMulti(String userid, String clientId){
        String client = platformUserOnlineCache.get(userid);
        return StringUtils.isNoneBlank(client) && !client.equals(clientId);
    }

    public boolean checkIsOnLine(String userid){
        return StringUtils.isNoneBlank(platformUserOnlineCache.get(userid));
    }

    public void logout(String userid, String client){

        String clt = platformUserOnlineCache.get(userid);
        if(clt != null && clt.equalsIgnoreCase(client)){
            platformUserOnlineCache.remove(userid);
            platformUserCache.remove(userid);
        }
    }

}
