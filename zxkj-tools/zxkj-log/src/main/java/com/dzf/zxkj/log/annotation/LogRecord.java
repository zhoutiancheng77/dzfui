package com.dzf.zxkj.log.annotation;

import com.dzf.zxkj.log.enums.LogRecordEnum;

import java.lang.annotation.*;

/**
 * @Auther: dandelion
 * @Date: 2019-09-09
 * @Description:
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface LogRecord {
    LogRecordEnum logRecordEnum();
    String msg();
    String ident();
}
