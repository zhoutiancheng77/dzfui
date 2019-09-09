package com.dzf.zxkj.custom.utils;


import com.dzf.zxkj.custom.type.DZFDouble;

public class DZFNumberFormat {
    public static final int NUMBERSTYLE = 0;
    public static final int CURRENCYSTYLE = 1;
    public static final int PERCENTSTYLE = 2;
    public static final int SCIENTIFICSTYLE = 3;

    public DZFNumberFormat() {
        super();
    }

    public static String format(double value) {
        return format(value, NUMBERSTYLE);
    }

    public static String format(double value, int style) {
        return format(value, style, null);
    }

    public static String format(double value, int style, java.util.Locale locale) {
        java.text.NumberFormat form = null;
        if (locale == null) {
            switch (style) {
                case NUMBERSTYLE:
                    form = java.text.NumberFormat.getNumberInstance();
                    break;
                case CURRENCYSTYLE:
                    form = java.text.NumberFormat.getCurrencyInstance();
                    break;
                case PERCENTSTYLE:
                    form = java.text.NumberFormat.getPercentInstance();
                    break;
                case SCIENTIFICSTYLE:
                default:
                    form = java.text.NumberFormat.getInstance();
                    break;
            }
        } else {
            switch (style) {
                case NUMBERSTYLE:
                    form = java.text.NumberFormat.getNumberInstance(locale);
                    break;
                case CURRENCYSTYLE:
                    form = java.text.NumberFormat.getCurrencyInstance(locale);
                    break;
                case PERCENTSTYLE:
                    form = java.text.NumberFormat.getPercentInstance(locale);
                    break;
                case SCIENTIFICSTYLE:
                default:
                    form = java.text.NumberFormat.getInstance(locale);
                    break;
            }
        }
        form.setMaximumFractionDigits(9);
        return form.format(value);
    }

    public static String format(int value) {
        return format(value, NUMBERSTYLE);
    }

    public static String format(int value, int style) {
        return format(value, style, null);
    }

    public static String format(int value, int style, java.util.Locale locale) {
        return format((long) value, style, locale);
    }

    public static String format(long value) {
        return format(value, NUMBERSTYLE);
    }

    public static String format(long value, int style) {
        return format(value, style, null);
    }

    public static String format(long value, int style, java.util.Locale locale) {
        java.text.NumberFormat form = null;
        if (locale == null) {
            switch (style) {
                case NUMBERSTYLE:
                    form = java.text.NumberFormat.getNumberInstance();
                    break;
                case CURRENCYSTYLE:
                    form = java.text.NumberFormat.getCurrencyInstance();
                    break;
                case PERCENTSTYLE:
                    form = java.text.NumberFormat.getPercentInstance();
                    break;
                case SCIENTIFICSTYLE:
                default:
                    form = java.text.NumberFormat.getInstance();
                    break;
            }
        } else {
            switch (style) {
                case NUMBERSTYLE:
                    form = java.text.NumberFormat.getNumberInstance(locale);
                    break;
                case CURRENCYSTYLE:
                    form = java.text.NumberFormat.getCurrencyInstance(locale);
                    break;
                case PERCENTSTYLE:
                    form = java.text.NumberFormat.getPercentInstance(locale);
                    break;
                case SCIENTIFICSTYLE:
                default:
                    form = java.text.NumberFormat.getInstance(locale);
                    break;
            }
        }
        return form.format(value);
    }

    public static String format(Double value) {
        return format(value, NUMBERSTYLE);
    }

    public static String format(Double value, int style) {
        return format(value, style, null);
    }

    public static String format(Double value, int style, java.util.Locale locale) {
        if (value != null)
            return format(value.doubleValue(), style, locale);
        else
            return "";
    }

    public static String format(Integer value) {
        return format(value, NUMBERSTYLE);
    }

    public static String format(Integer value, int style) {
        return format(value, style, null);
    }

    public static String format(Integer value, int style, java.util.Locale locale) {
        if (value != null)
            return format(value.intValue(), style, locale);
        else
            return "";
    }

    public static String format(Long value) {
        return format(value, NUMBERSTYLE);
    }

    public static String format(Long value, int style) {
        return format(value, style, null);
    }

    public static String format(Long value, int style, java.util.Locale locale) {
        if (value != null)
            return format(value.longValue(), style, locale);
        else
            return "";
    }

    public static String format(DZFDouble value) {
        return format(value, NUMBERSTYLE);
    }

    public static String format(DZFDouble value, int style) {
        return format(value, style, null);
    }

    public static String format(DZFDouble value, int style, java.util.Locale locale) {
        if (value != null)
            return format(value.doubleValue(), style, locale);
        else
            return "";
    }

    public static void main(String[] args) {

    }
}
