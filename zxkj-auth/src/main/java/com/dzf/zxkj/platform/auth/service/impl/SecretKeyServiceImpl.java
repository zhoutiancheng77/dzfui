package com.dzf.zxkj.platform.auth.service.impl;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.dzf.zxkj.platform.auth.config.SecretKeyConfig;
import com.dzf.zxkj.platform.auth.secret.service.ISecretKeyService;
import com.dzf.zxkj.platform.auth.service.impl.fallback.SecretKeyServiceBack;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service(version = "1.0.0", timeout = 2 * 60 * 1000)
public class SecretKeyServiceImpl implements ISecretKeyService {

    @Autowired
    private SecretKeyConfig secretKeyConfig;

    @Override
    @SentinelResource(value = "auth-resource", fallbackClass = SecretKeyServiceBack.class, fallback = "getPubKey")
    public String getPubKey() {
        return secretKeyConfig.getPubKey();
    }

    @Override
    @SentinelResource(value = "auth-resource", fallbackClass = SecretKeyServiceBack.class, fallback = "getPreKey")
    public String getPreKey() {
        return secretKeyConfig.getPreKey();
    }

    @Override
    @SentinelResource(value = "auth-resource", fallbackClass = SecretKeyServiceBack.class, fallback = "getDefaultKey")
    public String getDefaultKey() {
        return secretKeyConfig.getDefaultKey();
    }
}
