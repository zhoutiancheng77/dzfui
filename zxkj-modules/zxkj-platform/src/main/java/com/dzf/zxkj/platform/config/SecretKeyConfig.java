package com.dzf.zxkj.platform.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@Slf4j
public class SecretKeyConfig {
    private String pubKey;
    private String preKey;
    private String defaultKey;
}
