package com.dzf.zxkj.report.config;

import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DubboReferenceConfig {
    @Reference
    private IZxkjPlatformService zxkjPlatformService;

    @Bean("zxkjPlatformService")
    public IZxkjPlatformService zxkjPlatformService() {
        return zxkjPlatformService;
    }
}
