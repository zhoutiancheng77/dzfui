package com.dzf.zxkj.jbsz;

import com.dzf.zxkj.mybatis.EnableMyBatisPlusConverter;
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
@EnableMyBatisPlusConverter
public class ZxkjJbszApplication {
    public static void main(String[] args) {
        SpringApplication.run(ZxkjJbszApplication.class, args);
    }
}
