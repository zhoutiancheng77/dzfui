package com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 转换值src=>dest
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface TaxExcelValueCast {
    /**
     * 原始值
     *
     * @return
     */
    String[] src() default {"是", "否"};

    /**
     * 转换后的值
     *
     * @return
     */
    String[] dest() default {"Y", "N"};
}
