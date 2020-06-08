package com.dzf.zxkj.platform;

import com.alicp.jetcache.anno.config.EnableCreateCacheAnnotation;
import com.alicp.jetcache.anno.config.EnableMethodCache;
import com.dzf.cloud.redis.EnableDzfRedis;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Auther: dandelion
 * @Date: 2019-09-05
 * @Description:
 */
@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(basePackages= {"com.dzf.zxkj.platform"})
@EnableMethodCache(basePackages = "com.dzf.zxkj.platform")
@EnableCreateCacheAnnotation
@EnableDzfRedis
public class ZxkjPlatformApplication {
    public static void main(String[] args) {
        SpringApplication.run(ZxkjPlatformApplication.class, args);
    }
}
