package com.dzf.zxkj.mybatis;

import com.dzf.zxkj.mybatis.configuration.AutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.lang.annotation.*;

/**
 * @Auther: dandelion
 * @Date: 2019-09-04
 * @Description:
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(AutoConfiguration.class)
@EnableScheduling
@Documented
@Inherited
public @interface EnableMyBatisPlus {

}
