package com.dzf.zxkj.mybatis.config;

import com.dzf.zxkj.mybatis.converter.DZFDateTimeConverter;
import com.dzf.zxkj.mybatis.lang.DZFDateTime;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;

/**
 * @Auther: dandelion
 * @Date: 2019-09-03
 * @Description:
 */
@Configuration
public class JackSonConfig {
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer initJackson() {
        return builder ->{
            builder.serializerByType(DZFDateTime.class,new DZFDateTimeConverter());
            builder.serializationInclusion(JsonInclude.Include.NON_NULL);//不包含为空的字段
            builder.serializationInclusion(JsonInclude.Include.NON_EMPTY);//不包含空字符串字段
            builder.indentOutput(true).dateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        };
    }
}
