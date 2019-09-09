package com.dzf.zxkj.custom.type;

import java.text.SimpleDateFormat;


public final class DZFTime implements java.io.Serializable, Comparable {
    static final long serialVersionUID = 7886265777567493523L;

    public DZFTime() {
        super();
    }

    public DZFTime(long m) {
        this(new java.util.Date(m));
    }

    public DZFTime(String strTime) {
        this(strTime, true);
    }

    public DZFTime(java.sql.Date date) {
        this((java.util.Date) date);
    }

    public DZFTime(java.util.Date date) {
        this((new SimpleDateFormat("HH:mm:ss")).format(date));
    }


    public String toString() {
        return value == null ? "" : value;
    }

    private String value = null;


    public boolean after(DZFTime when) {
        return value.compareTo(when.toString()) > 0;
    }


    public boolean before(DZFTime when) {
        return value.compareTo(when.toString()) < 0;
    }


    public Object clone() {
        return new DZFTime(value);
    }

    public int compareTo(DZFTime when) {
        return value.compareTo(when.toString());
    }


    public boolean equals(Object o) {
        if ((o != null) && (o instanceof DZFTime)) {
            return value.equals(o.toString());
        }
        return false;
    }


    public int getHour() {
        return Integer.valueOf(value.substring(0, 2)).intValue();
    }


    public int getMinute() {
        return Integer.valueOf(value.substring(3, 5)).intValue();
    }


    public int getSecond() {
        return Integer.valueOf(value.substring(6, 8)).intValue();
    }


    public static String getValidUFTimeString(String sTime) {
        if (sTime == null)
            return null;
        if (isAllowTime(sTime))
            return sTime;
        else {

            try {
                int hour = 0;
                int minute = 0;
                int second = 0;
                int index = sTime.indexOf(":");
                if (index < 1) {
                    if (sTime.trim().length() > 0)
                        hour = Integer.parseInt(sTime.trim());
                } else {
                    hour = Integer.parseInt(sTime.trim().substring(0, index));
                    String sTemp = sTime.trim().substring(index + 1);
                    if (sTemp.trim().length() > 0) {
                        index = sTemp.indexOf(":");
                        if (index < 1) {
                            minute = Integer.parseInt(sTemp.trim());
                        } else {
                            minute = Integer.parseInt(sTemp.trim().substring(0, index));
                            if (sTemp.trim().substring(index + 1).trim().length() > 0)
                                second = Integer.parseInt(sTemp.trim().substring(index + 1));
                        }
                    }
                }
                if (hour < 0 || hour > 24 || minute < 0 || minute > 59 || second < 0 || second > 59)
                    return null;
                String strHour = String.valueOf(hour);
                if (strHour.length() < 2)
                    strHour = "0" + strHour;
                String strMinute = String.valueOf(minute);
                if (strMinute.length() < 2)
                    strMinute = "0" + strMinute;
                String strSecond = String.valueOf(second);
                if (strSecond.length() < 2)
                    strSecond = "0" + strSecond;
                //
                return strHour + ":" + strMinute + ":" + strSecond;
            } catch (Exception e) {
                return null;
            }
        }
    }


    public static boolean isAllowTime(String strTime) {
        if (strTime == null || strTime.trim().length() == 0)
            return true;
        if (strTime.trim().length() != 8)
            return false;
        for (int i = 0; i < 8; i++) {
            char c = strTime.trim().charAt(i);
            if (i == 2 || i == 5) {
                if (c != ':')
                    return false;
            } else if (c < '0' || c > '9')
                return false;
        }
        int hour = Integer.parseInt(strTime.trim().substring(0, 2));
        int minute = Integer.parseInt(strTime.trim().substring(3, 5));
        int second = Integer.parseInt(strTime.trim().substring(6, 8));
        if (hour < 0 || hour > 24 || minute < 0 || minute > 59 || second < 0 || second > 59)
            return false;
        return true;
    }

    public DZFTime(long m, Object o) {
        if (m == 24 * 3600000) {
            value = "24:00:00";
            return;
        }
        long seconds = m / 1000;
        long hour = seconds / 3600;
        hour %= 24;
        long minute = seconds / 60;
        minute %= 60;
        long second = seconds % 60;
        value = "";
        if (hour < 10)
            value += "0" + hour;
        else
            value += hour;
        value += ":";
        if (minute < 10)
            value += "0" + minute;
        else
            value += minute;
        value += ":";
        if (second < 10)
            value += "0" + second;
        else
            value += second;
    }


    public DZFTime(String strTime, boolean isParse) {
        if (isParse)
            value = getValidUFTimeString(strTime);
        else
            value = strTime;
    }


    public long getMillis() {
        return ((getHour() * 60 + getMinute()) * 60 + getSecond()) * 1000;
    }

    public int compareTo(Object o) {
        if (o instanceof DZFTime)
            return value.compareTo(o.toString());
        throw new RuntimeException("Unsupported parameter type while comparing UFTime!");
    }

}