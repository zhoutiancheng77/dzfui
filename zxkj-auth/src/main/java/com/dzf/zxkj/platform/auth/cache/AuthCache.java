package com.dzf.zxkj.platform.auth.cache;

import com.alicp.jetcache.Cache;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.CreateCache;
import com.dzf.zxkj.platform.auth.entity.LoginUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@Slf4j
public class AuthCache {

    @CreateCache(name = "zxkj:platform:user", cacheType = CacheType.REMOTE, expire = 8, timeUnit = TimeUnit.HOURS)
    private Cache<String, LoginUser> platformUserCache;

    @CreateCache(name = "zxkj:platform:online", cacheType = CacheType.LOCAL, expire = 8, timeUnit = TimeUnit.HOURS)
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
        String client = platformUserOnlineCache.get(userid);
        log.info("checkIsMulti--------begin--------------");
        log.info("client-------->"+client);
        log.info("userid-------->"+userid);
        log.info("checkIsMulti--------end--------------");
        return StringUtils.isNoneBlank(platformUserOnlineCache.get(userid)) && !platformUserOnlineCache.get(userid).equals(clientId);
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
