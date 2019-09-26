package com.dzf.zxkj.common.utils;

import com.dzf.zxkj.common.lang.DZFTime;
import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.Converter;

import java.util.Calendar;

public class DZFTimeConvertor implements Converter {

    private Object defaultValue = null;

    private boolean useDefault = true;

    public DZFTimeConvertor() {

        this.defaultValue = null;
        this.useDefault = true;

    }

    public DZFTimeConvertor(Object defaultValue) {

        this.defaultValue = defaultValue;
        this.useDefault = true;

    }

    @SuppressWarnings("unchecked")
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
                return new DZFTime((java.util.Date) value);
            } else if (value instanceof Calendar) {
                return new DZFTime(((Calendar) value).getTimeInMillis());
            }

            return (new DZFTime(value.toString().intern()));
        } catch (Exception e) {
            if (useDefault) {
                return (defaultValue);
            } else {
                throw new ConversionException(e);
            }
        }

    }

}
