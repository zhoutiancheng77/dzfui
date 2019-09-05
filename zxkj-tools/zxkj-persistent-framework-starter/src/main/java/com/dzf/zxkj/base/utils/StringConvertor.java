package com.dzf.zxkj.base.utils;

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.Converter;

public class StringConvertor implements Converter {

    public Object convert(Class type, Object value) {
        if (value == null) {
            return null;
        }

        try {
            return trimString(String.valueOf(value), false).intern();
        } catch (Exception e) {
            throw new ConversionException(e);
        }

    }

    private String trimString(String value, boolean isLeft) {
        int len = value.length();
        int st = 0;
        int off = 0; /* avoid getfield opcode */
        char[] val = value.toCharArray(); /* avoid getfield opcode */

        if (isLeft) {
            while ((st < len) && (val[off + st] <= ' ')) {
                st++;
            }
        } else {
            while ((st < len) && (val[off + len - 1] <= ' ')) {
                len--;
            }
        }
        return ((st > 0) || (len < value.length())) ? value.substring(st, len) : value;
    }

}
