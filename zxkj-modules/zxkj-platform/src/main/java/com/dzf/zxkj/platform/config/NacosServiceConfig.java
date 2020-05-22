package com.dzf.zxkj.platform.config;

import lombok.Data;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@Data
public class NacosServiceConfig {
    private List<String> serverList;
}
