package com.dzf.zxkj.platform.auth.cache;

import com.alicp.jetcache.Cache;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.CreateCache;
import com.dzf.zxkj.platform.auth.entity.LoginUser;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class AuthCache {

    @CreateCache(name = "zxkj:platform:user", cacheType = CacheType.REMOTE, expire = 1, timeUnit = TimeUnit.HOURS)
    private Cache<String, LoginUser> platformUserCache;

    public LoginUser getLoginUser(String userid){
        return platformUserCache.get(userid);
    }

    public void putLoginUser(String userid, LoginUser loginUser){
        platformUserCache.put(userid, loginUser);
    }

}
