package com.dzf.zxkj.custom.type;


public final class DZFBoolean implements java.io.Serializable, Comparable {

    public static final DZFBoolean TRUE = new DZFBoolean(true);


    public static final DZFBoolean FALSE = new DZFBoolean(false);

    private static final long serialVersionUID = -2971431361057093474L;

    private boolean value = false;

    public DZFBoolean(char ch) {
        super();
        value = (ch == 'Y' || ch == 'y');
    }


    public DZFBoolean(String val) {
        if ("是".equals(val)) {
            value = true;
        } else if ("否".equals(val)) {
            value = false;
        } else if (val != null
                && val.length() > 0
                && (val.equalsIgnoreCase("true") || val.charAt(0) == 'Y' || val
                .charAt(0) == 'y')) {
            value = true;
        } else {
            value = false;
        }
    }


    public DZFBoolean(boolean b) {
        super();
        value = b;
    }


    public boolean booleanValue() {
        return value;
    }

    public static DZFBoolean valueOf(boolean b) {
        return (b ? TRUE : FALSE);
    }

    public static DZFBoolean valueOf(String val) {
        if ("是".equals(val)) {
            return TRUE;
        } else if ("否".equals(val)) {
            return FALSE;
        } else if (val != null
                && val.length() > 0
                && (val.equalsIgnoreCase("true") || val.charAt(0) == 'Y' || val
                .charAt(0) == 'y')) {
            return TRUE;
        } else {
            return FALSE;
        }
    }


    public boolean equals(Object obj) {
        if ((obj != null) && (obj instanceof DZFBoolean)) {
            return value == ((DZFBoolean) obj).booleanValue();
        }
        return false;
    }


    public int hashCode() {
        return value ? 1231 : 1237;
    }


    public String toString() {
        return value ? "Y" : "N";
    }

    public int compareTo(Object o) {
        if (o == null) return 1;
        return toString().compareTo(o.toString());
    }
}