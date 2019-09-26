/*
 * 创建日期 2005-9-16
 *
 * TODO 要更改此生成的文件的模板，请转至
 * 窗口 － 首选项 － Java － 代码样式 － 代码模板
 */
package com.dzf.zxkj.common.utils;

import com.dzf.zxkj.common.lang.DZFDateTime;
import oracle.sql.TIMESTAMP;
import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.Converter;

import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DZFDateTimeConvertor implements Converter {

    private Object defaultValue = null;

    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//定义格式，不显示毫秒  /////"yyyy-MM-dd HH:mm:ss.S"

    private boolean useDefault = true;

    /**
     * Create a {@link Converter} that will throw a {@link ConversionException}
     * if a conversion error occurs.
     */
    public DZFDateTimeConvertor() {

        this.defaultValue = null;
        this.useDefault = true;

    }

    public DZFDateTimeConvertor(Object defaultValue) {

        this.defaultValue = defaultValue;
        this.useDefault = true;

    }

    public Object convert(Class type, Object value) {

        if (value == null) {
            if (useDefault) {
                return (defaultValue);
            } else {
                throw new ConversionException("No value specified");
            }
        }

        try {
            if (value instanceof java.util.Date) {
                return new DZFDateTime((java.util.Date) value);
            } else if (value instanceof Calendar) {
                return new DZFDateTime(((Calendar) value).getTimeInMillis());
            } else if (value instanceof TIMESTAMP) {//oracle ----TIMESTAMP
                String zz = getDate(value);
                if (zz == null || "".equals(zz)) {
                    return null;
                } else {
                    return new DZFDateTime(zz);
                }
            }
            return (new DZFDateTime(value.toString().intern(), false));
        } catch (Exception e) {
            if (useDefault) {
                return (defaultValue);
            } else {
                throw new ConversionException(e);
            }
        }

    }


    private String getDate(Object value) {
        Timestamp timestamp = null;
        try {
            timestamp = (Timestamp) value;
        } catch (Exception e) {
            timestamp = getOracleTimestamp(value);
        }
        if (timestamp != null)
            return df.format(timestamp);
        else
            return null;
    }

    private Timestamp getOracleTimestamp(Object value) {
        try {
            Class clz = value.getClass();
            Method m = clz.getMethod("timestampValue", null);
            //m = clz.getMethod("timeValue", null); 时间类型
            //m = clz.getMethod("dateValue", null); 日期类型
            return (Timestamp) m.invoke(value, null);
        } catch (Exception e) {
            return null;
        }
    }

}
