package com.dzf.zxkj.operate.log.annotation;

import com.dzf.zxkj.common.constant.ISysConstants;
import com.dzf.zxkj.common.enums.LogRecordEnum;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogRecord {
    String msg();
    LogRecordEnum type();
    int ident() default ISysConstants.SYS_2;
}
