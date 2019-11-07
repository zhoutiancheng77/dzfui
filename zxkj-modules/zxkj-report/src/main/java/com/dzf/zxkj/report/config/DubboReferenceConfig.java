package com.dzf.zxkj.report.config;

import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DubboReferenceConfig {
    @Reference(version = "2.0.0", timeout = Integer.MAX_VALUE)
    private IZxkjPlatformService zxkjPlatformService;

    @Bean("zxkjPlatformService")
    public IZxkjPlatformService zxkjPlatformService() {
        return zxkjPlatformService;
    }
}
