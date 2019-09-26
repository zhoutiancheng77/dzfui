package com.dzf.zxkj.base.model;


import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;

import java.io.Serializable;

public abstract class CircularlyAccessibleValueObject implements Cloneable, Serializable {
    private static final long serialVersionUID = -4360103926517671160L;
    private int status = 0;

    public abstract String[] getAttributeNames();

    public abstract Object getAttributeValue(String paramString);

    public int getStatus() {
        return this.status;
    }

    public abstract void setAttributeValue(String paramString, Object paramObject);

    public void setStatus(int status) {
        this.status = status;
    }

    public Integer getInteger(String attributeName) {
        Object obj = getAttributeValue(attributeName);
        Integer integer = null;
        if (obj != null) {
            if ((obj instanceof Integer))
                integer = (Integer) obj;
            else
                integer = new Integer(obj.toString());
        }
        return integer;
    }

    public String getString(String attributeName) {
        Object obj = getAttributeValue(attributeName);
        String str = null;
        if (obj != null) {
            if ((obj instanceof String))
                str = (String) obj;
            else
                str = obj.toString();
        }
        return str;
    }

    public DZFBoolean getUFBoolean(String attributeName) {
        Object obj = getAttributeValue(attributeName);
        DZFBoolean ufBoolean = DZFBoolean.FALSE;
        if (obj != null) {
            if ((obj instanceof DZFBoolean))
                ufBoolean = (DZFBoolean) obj;
            else
                ufBoolean = new DZFBoolean(obj.toString());
        }
        return ufBoolean;
    }

    public DZFDate getUFDate(String attributeName) {
        Object obj = getAttributeValue(attributeName);
        DZFDate date = null;
        if ((obj != null) && (obj.toString().trim().length() > 0)) {
            if ((obj instanceof DZFDate))
                date = (DZFDate) obj;
            else {
                date = new DZFDate(obj.toString());
            }
        }
        return date;
    }

    public DZFDouble getUFDouble(String attributeName) {
        Object obj = getAttributeValue(attributeName);
        DZFDouble ufDouble = null;
        if (obj != null) {
            if ((obj instanceof DZFDouble))
                ufDouble = (DZFDouble) obj;
            else if (obj.toString().length() > 0) {
                ufDouble = new DZFDouble(obj.toString());
            }
        }
        return ufDouble;
    }
}