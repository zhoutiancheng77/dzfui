package com.dzf.file;

import com.dzf.file.fastdfs.FastDfsConfig;
import com.dzf.file.fastdfs.FastDfsUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ZxkjFastDFSAutoConfiguration {

    @Bean("connectionPool")
    public FastDfsUtil fastDfsUtil(){
        return FastDfsUtil.getInstance();
    }

    @Bean
    public FastDfsConfig fastDfsConfig(){
        return new FastDfsConfig();
    }
}
