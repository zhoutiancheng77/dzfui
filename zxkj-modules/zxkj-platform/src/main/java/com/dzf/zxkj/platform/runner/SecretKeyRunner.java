package com.dzf.zxkj.platform.runner;

import com.dzf.zxkj.platform.auth.secret.service.ISecretKeyService;
import com.dzf.zxkj.platform.config.SecretKeyConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SecretKeyRunner implements CommandLineRunner {

    @Reference(version = "1.0.0")
    private ISecretKeyService secretKeyService;

    @Autowired
    private SecretKeyConfig secretKeyConfig;

    @Override
    public void run(String... args) throws Exception {
        try {
            refreshSecretKey();
        } catch (Exception e) {
            log.error("初始化加载secretKey失败,10分钟后自动重试!",e);
        }
    }
    @Scheduled(cron = "0 0/10 * * * ?")
    private void refreshSecretKey(){
        if(StringUtils.isAnyBlank(secretKeyConfig.getDefaultKey(), secretKeyConfig.getPreKey(), secretKeyConfig.getPubKey())){
            log.info("初始化加载secretKey...............");
            secretKeyConfig.setDefaultKey(secretKeyService.getDefaultKey());
            secretKeyConfig.setPreKey(secretKeyService.getPreKey());
            secretKeyConfig.setPubKey(secretKeyService.getPubKey());
        }
    }
}
