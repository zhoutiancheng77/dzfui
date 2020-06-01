package com.dzf.zxkj.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableDiscoveryClient
@EnableScheduling
@ComponentScan(basePackages= {"com.dzf.zxkj.app"})
public class ZxkjApp {
    public static void main(String[] args) {
        SpringApplication.run(ZxkjApp.class, args);
    }
}
