package com.dzf.zxkj.jackson;

import com.dzf.zxkj.custom.type.DZFBoolean;
import com.dzf.zxkj.custom.type.DZFDate;
import com.dzf.zxkj.custom.type.DZFDateTime;
import com.dzf.zxkj.jackson.serializer.DZFBooleanSerializer;
import com.dzf.zxkj.jackson.serializer.DZFDateSerializer;
import com.dzf.zxkj.jackson.serializer.DZFDateTimeSerializer;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.text.SimpleDateFormat;

/**
 * @Auther: dandelion
 * @Date: 2019-09-05
 * @Description:
 */
@ConditionalOnBean(DataSource.class)
@Configuration
@ComponentScan
public class JacksonConverterAutoConfiguration {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer initJackson() {
        return builder -> {
            builder.serializerByType(DZFDate.class, new DZFDateSerializer());
            builder.serializerByType(DZFBoolean.class, new DZFBooleanSerializer());
            builder.serializerByType(DZFDateTime.class, new DZFDateTimeSerializer());
            builder.serializationInclusion(JsonInclude.Include.NON_NULL);//不包含为空的字段
            builder.serializationInclusion(JsonInclude.Include.NON_EMPTY);//不包含空字符串字段
            builder.indentOutput(true).dateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        };
    }

}
