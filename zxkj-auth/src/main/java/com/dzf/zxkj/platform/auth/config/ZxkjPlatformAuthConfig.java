package com.dzf.zxkj.platform.auth.config;


import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class ZxkjPlatformAuthConfig {
    @Value("${platform.name:'zxkj'}")
    private String platformName;
    @Value("${platform.external.loginUrl}")
    private String url;
}
