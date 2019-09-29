package com.dzf.zxkj.gateway.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@Data
public class GatewayConfig {

    @Value("${jwt.loginUrl:/auth/login}")
    private String loginUrl;

    private List<String> ignoreUrl;

    private byte[] userPubKey;
}
