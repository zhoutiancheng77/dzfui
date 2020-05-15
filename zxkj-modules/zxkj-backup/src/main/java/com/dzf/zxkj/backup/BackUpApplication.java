package com.dzf.zxkj.backup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class BackUpApplication {
    public static void main(String[] args) {
        SpringApplication.run(BackUpApplication.class, args);
    }
}
