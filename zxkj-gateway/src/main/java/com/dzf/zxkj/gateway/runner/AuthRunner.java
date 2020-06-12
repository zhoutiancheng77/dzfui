package com.dzf.zxkj.gateway.runner;

import com.alicp.jetcache.Cache;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.CreateCache;
import com.dzf.zxkj.gateway.config.GatewayConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class AuthRunner implements CommandLineRunner {

    @CreateCache(name = "zxkj:platform:rsaKey", cacheType = CacheType.REMOTE)
    private Cache<String, Map<String, byte[]>> rsaKeyCache;

    @Autowired
    private GatewayConfig gatewayConfig;

    @Override
    public void run(String... args) throws Exception {
        try {
            refreshPubKey();
        } catch (Exception e) {
            log.error("初始化加载pubKey失败,1分钟后自动重试!", e);
        }
    }

    @Scheduled(cron = "0 0/5 * * * ?")
    public void refreshPubKey() {
        Map<String, byte[]> keyMap = rsaKeyCache.get("rsaKey");
        byte[] pubKey = keyMap.get("pub");
        gatewayConfig.setUserPubKey(pubKey);
        log.info("刷新公钥。。。。。。");
    }
}
