package com.dzf.zxkj.backup;

import com.dzf.cloud.redis.EnableDzfRedis;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@EnableDzfRedis
@ComponentScan(basePackages = {"com.dzf.zxkj.backup", "com.dzf.file"})
public class BackUpApplication {
    public static void main(String[] args) {
        SpringApplication.run(BackUpApplication.class, args);
    }
}
