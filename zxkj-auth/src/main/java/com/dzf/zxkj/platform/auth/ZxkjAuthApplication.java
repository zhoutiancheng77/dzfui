package com.dzf.zxkj.platform.auth;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.dzf.zxkj.platform.auth.mapper")
@ComponentScan(basePackages = {"com.dzf.zxkj.platform.auth","com.dzf.zxkj.mybatis.handler"})
public class ZxkjAuthApplication {
    public static void main(String[] args) {
        SpringApplication.run(ZxkjAuthApplication.class, args);
    }
}
