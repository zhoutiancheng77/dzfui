package com.dzf.zxkj.gateway;

import com.alicp.jetcache.anno.config.EnableCreateCacheAnnotation;
import com.alicp.jetcache.anno.config.EnableMethodCache;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @Auther: dandelion
 * @Date: 2019-09-02
 * @Description:
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableScheduling
@EnableMethodCache(basePackages = "com.dzf.zxkj.gateway")
@EnableCreateCacheAnnotation
public class ZxkjGateWayApplication {
    public static void main(String[] args) {
        SpringApplication.run(ZxkjGateWayApplication.class, args);
    }
}
