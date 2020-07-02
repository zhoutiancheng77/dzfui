package com.dzf.zxkj.report;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @Auther: dandelion
 * @Date: 2019-09-05
 * @Description:
 */
@EnableDiscoveryClient
@EnableScheduling
@SpringBootApplication(exclude = DruidDataSourceAutoConfigure.class)
public class ZxkjReportApplication {
    public static void main(String[] args) {
        SpringApplication.run(ZxkjReportApplication.class, args);
    }
}
