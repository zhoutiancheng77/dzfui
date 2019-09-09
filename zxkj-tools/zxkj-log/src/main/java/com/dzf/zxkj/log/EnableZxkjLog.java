package com.dzf.zxkj.log;

import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.lang.annotation.*;

/**
 * @Auther: dandelion
 * @Date: 2019-09-09
 * @Description:
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(LogAutoConfiguration.class)
@EnableScheduling
@Documented
@Inherited
public @interface EnableZxkjLog {

}
