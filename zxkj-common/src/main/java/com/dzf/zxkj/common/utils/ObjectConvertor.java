package com.dzf.zxkj.common.utils;

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.Converter;


public class ObjectConvertor implements Converter {
    public ObjectConvertor() {
        this.defaultValue = null;
        this.useDefault = false;

    }


    public ObjectConvertor(Object defaultValue) {

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
            return value;
        } catch (Exception e) {
            if (useDefault) {
                return (defaultValue);
            } else {
                throw new ConversionException(e);
            }
        }

    }

}
