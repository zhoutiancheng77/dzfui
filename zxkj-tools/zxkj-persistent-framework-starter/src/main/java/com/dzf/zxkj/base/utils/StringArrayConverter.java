package com.dzf.zxkj.base.utils;

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.converters.AbstractArrayConverter;

import java.util.List;

public final class StringArrayConverter extends AbstractArrayConverter {

    private static String model[] = new String[0];

    private static int ints[] = new int[0];

    public StringArrayConverter() {

        this.defaultValue = null;
        this.useDefault = false;

    }

    public StringArrayConverter(Object defaultValue) {
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

        if (model.getClass() == value.getClass()) {
            return (value);
        }

        if (ints.getClass().equals(value.getClass())) {
            int[] values = (int[]) value;
            String[] results = new String[values.length];
            for (int i = 0; i < values.length; i++) {
                results[i] = Integer.toString(values[i]).intern();
            }

            return (results);
        }

        try {
            List list = parseElements(value.toString());
            String results[] = new String[list.size()];
            for (int i = 0; i < results.length; i++) {
                results[i] = ((String) list.get(i)).intern();
            }
            return (results);
        } catch (Exception e) {
            if (useDefault) {
                return (defaultValue);
            } else {
                throw new ConversionException(value.toString(), e);
            }
        }

    }

}
