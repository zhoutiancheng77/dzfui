package com.dzf.zxkj.platform.auth.runner;

import com.alicp.jetcache.Cache;
import com.alicp.jetcache.CacheGetResult;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.CreateCache;
import com.dzf.zxkj.auth.utils.RsaKeyUtil;
import com.dzf.zxkj.platform.auth.cache.PermissionCache;
import com.dzf.zxkj.platform.auth.config.RsaKeyConfig;
import com.dzf.zxkj.platform.auth.service.IAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
@Slf4j
public class RsaKeyRunner implements CommandLineRunner {

    @Autowired
    private RsaKeyConfig rsaKeyConfig;

    @CreateCache(name = "zxkj:platform:rsaKey", cacheType = CacheType.REMOTE)
    private Cache<String, Map<String, byte[]>> rsaKeyCache;

    @Autowired
    private IAuthService authService;

    @Autowired
    private PermissionCache permissionCache;

    @Override
    public void run(String... args) throws Exception {
        //启动获取秘钥
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
        log.info("成功获取秘钥");
        //获取所有权限
        Set<String> permissions =  authService.getAllPermission();
        permissionCache.putAllPermission(permissions);
        log.info("成功加载权限");
    }
}
