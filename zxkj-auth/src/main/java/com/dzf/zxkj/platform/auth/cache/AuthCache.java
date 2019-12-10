package com.dzf.zxkj.platform.auth.cache;

import com.alicp.jetcache.Cache;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.CreateCache;
import com.dzf.zxkj.platform.auth.entity.LoginUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class AuthCache {

    @CreateCache(name = "zxkj:platform:user", cacheType = CacheType.REMOTE, expire = 1, timeUnit = TimeUnit.HOURS)
    private Cache<String, LoginUser> platformUserCache;

    @CreateCache(name = "zxkj:platform:online", cacheType = CacheType.REMOTE, expire = 1, timeUnit = TimeUnit.HOURS)
    private Cache<String, String> platformUserOnlineCache;

    public LoginUser getLoginUser(String userid){
        return platformUserCache.get(userid);
    }

    public void putLoginUser(String userid, LoginUser loginUser){
        platformUserCache.put(userid, loginUser);
    }

    public void putLoginUnique(String userid, String clientId){
        platformUserOnlineCache.put(userid, clientId);
    }

    public boolean checkIsMulti(String userid, String clientId){
        return StringUtils.isNoneBlank(platformUserOnlineCache.get(userid)) && !platformUserOnlineCache.get(userid).equals(clientId);
    }

    public void logout(String userid){
        platformUserOnlineCache.remove(userid);
        platformUserCache.remove(userid);
    }

}
