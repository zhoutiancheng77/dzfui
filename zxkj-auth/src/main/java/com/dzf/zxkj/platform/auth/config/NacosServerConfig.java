package com.dzf.zxkj.platform.auth.config;

import com.alibaba.cloud.nacos.NacosConfigProperties;
import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class NacosServerConfig {
    @Autowired
    private NacosConfigProperties nacosConfigProperties;

    @Autowired
    private NacosDiscoveryProperties nacosDiscoveryProperties;
}
