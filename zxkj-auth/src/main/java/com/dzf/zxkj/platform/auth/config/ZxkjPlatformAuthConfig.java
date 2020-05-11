package com.dzf.zxkj.platform.auth.config;


import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@RefreshScope
public class ZxkjPlatformAuthConfig {
    @Value("${platform.name:'zxkj'}")
    private String platformName;
    @Value("${platform.external.loginUrl:'http://127.0.0.1:8521/loginByToke'}")
    private String url;
    private String platformAdminName = "xwwy";
    private String bankAcountArea = "23";
}
