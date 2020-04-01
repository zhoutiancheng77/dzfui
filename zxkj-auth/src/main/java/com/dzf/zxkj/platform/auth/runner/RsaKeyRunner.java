package com.dzf.zxkj.platform.auth.runner;

import com.alicp.jetcache.Cache;
import com.alicp.jetcache.CacheGetResult;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.CreateCache;
import com.dzf.zxkj.platform.auth.config.RsaKeyConfig;
import com.dzf.zxkj.platform.auth.utils.RsaKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class RsaKeyRunner implements CommandLineRunner {

    @Autowired
    private RsaKeyConfig rsaKeyConfig;

    @CreateCache(name = "zxkj:platform:rsaKey", cacheType = CacheType.REMOTE, expire = 30, timeUnit = TimeUnit.DAYS)
    private Cache<String, Map<String, byte[]>> rsaKeyCache;


    @Override
    public void run(String... args) throws Exception {

        Map<String, byte[]> keyMap;

        CacheGetResult<Map<String, byte[]>> result = rsaKeyCache.GET("rsaKey");

        if(result.isSuccess()){
            keyMap = rsaKeyCache.get("rsaKey");
        }else{
            keyMap = RsaKeyUtil.getInstance().generateKey(rsaKeyConfig.getUserSecret());
            rsaKeyCache.put("rsaKey", keyMap);
        }

        rsaKeyConfig.setUserPriKey(keyMap.get("pri"));
        rsaKeyConfig.setUserPubKey(keyMap.get("pub"));
    }
}
