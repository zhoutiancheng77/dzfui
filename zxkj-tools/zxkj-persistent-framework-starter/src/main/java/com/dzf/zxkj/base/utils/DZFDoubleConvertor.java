package com.dzf.zxkj.base.utils;


import com.dzf.zxkj.common.lang.DZFDouble;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.Converter;

@Slf4j
public class DZFDoubleConvertor implements Converter {

    public DZFDoubleConvertor() {

        this.defaultValue = null;
        this.useDefault = true;

    }


    public DZFDoubleConvertor(Object defaultValue) {

        this.defaultValue = defaultValue;
        this.useDefault = true;

    }


    private Object defaultValue = null;


    private boolean useDefault = true;


    public Object convert(Class type, Object value) {

        if (value == null) {
            if (useDefault) {
                return (defaultValue);
            } else {
                throw new ConversionException("No value specified");
            }
        }

        try {

            if (value instanceof Number) {
                Number tmpNum = (Number) value;
                if (tmpNum.doubleValue() == 0)
                    return DZFDouble.ZERO_DBL;
                else if (tmpNum.doubleValue() == 1) {
                    return DZFDouble.ONE_DBL;
                } else
                    return new DZFDouble(tmpNum.toString());
            }
            return new DZFDouble(value.toString());
        } catch (Exception e) {
            log.error("错误的值：" + value + "。", e);
            if (useDefault) {
                return (defaultValue);
            } else {
                throw new ConversionException(e);
            }
        }
    }
}