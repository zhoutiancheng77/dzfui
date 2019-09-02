package com.dzf.zxkj.jbsz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @Auther: dandelion
 * @Date: 2019-09-02
 * @Description:
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ZxkjJbszApplication {
    public static void main(String[] args) {
        SpringApplication.run(ZxkjJbszApplication.class, args);
    }
}
