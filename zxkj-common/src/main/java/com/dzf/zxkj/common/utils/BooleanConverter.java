package com.dzf.zxkj.common.utils;

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.Converter;


public final class BooleanConverter implements Converter {

    public BooleanConverter() {

        this.defaultValue = null;
        this.useDefault = false;

    }

    public BooleanConverter(Object defaultValue) {

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

        if (value instanceof Boolean) {
            return (value);
        }

        try {
            String stringValue = value.toString();
            stringValue = stringValue.trim();
            if (stringValue.equalsIgnoreCase("yes") || stringValue.equalsIgnoreCase("y")
                    || stringValue.equalsIgnoreCase("true") || stringValue.equalsIgnoreCase("on")
                    || stringValue.equalsIgnoreCase("1")) {
                return (Boolean.TRUE);
            } else if (stringValue.equalsIgnoreCase("no") || stringValue.equalsIgnoreCase("n")
                    || stringValue.equalsIgnoreCase("false") || stringValue.equalsIgnoreCase("off")
                    || stringValue.equalsIgnoreCase("0")) {
                return (Boolean.FALSE);
            } else if (useDefault) {
                return (defaultValue);
            } else {
                throw new ConversionException(stringValue);
            }
        } catch (ClassCastException e) {
            if (useDefault) {
                return (defaultValue);
            } else {
                throw new ConversionException(e);
            }
        }

    }

}
