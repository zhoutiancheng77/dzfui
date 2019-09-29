package com.dzf.zxkj.gateway.runner;

import com.dzf.zxkj.gateway.config.GatewayConfig;
import com.dzf.zxkj.platform.auth.service.IAuthService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AuthRunner implements CommandLineRunner {

    @Reference(version = "1.0.0")
    private IAuthService authService;

    @Autowired
    private GatewayConfig gatewayConfig;

    @Override
    public void run(String... args) throws Exception {
        try {
            refreshPubKey();
        } catch (Exception e) {
            log.error("初始化加载pubKey失败,1分钟后自动重试!",e);
        }
    }
    @Scheduled(cron = "0 0/1 * * * ?")
    public void refreshPubKey(){
        byte[] pubKey = authService.getPubKey();
        gatewayConfig.setUserPubKey(pubKey);
    }
}
