package com.dzf.file;

import com.dzf.file.fastdfs.FastDfsConfig;
import com.dzf.file.fastdfs.FastDfsUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ZxkjFastDFSAutoConfiguration {

    @Bean
    public FastDfsConfig fastDfsConfig(){
        return new FastDfsConfig();
    }

    @Bean("connectionPool")
    @ConditionalOnBean(FastDfsConfig.class)
    public FastDfsUtil fastDfsUtil(){
        return FastDfsUtil.getInstance();
    }


}
