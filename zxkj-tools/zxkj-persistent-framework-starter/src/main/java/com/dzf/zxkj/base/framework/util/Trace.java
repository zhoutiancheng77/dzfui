package com.dzf.zxkj.base.framework.util;

import lombok.extern.slf4j.Slf4j;

import java.sql.Date;
import java.util.Calendar;

/**
 * @nopublish User: 贺扬 Date: 2005-5-13 Time: 16:27:07 Trace类的说明
 */

@Slf4j
public class Trace {

    private static void traceSub(String id, String param, long time) {

        String caller = null;
        // getCaller();
        if (param != null) {
            param = "(" + param + ")";
        } else {
            param = "()";
        }
        // if (caller == null)
        caller = id + param;
        // else
        // caller = id + "." + caller + param;
        println(caller, time);
    }

    public static void trace(String id) {
        traceSub(id, null, 0);
    }

    public static void trace(String id, long beforeTime) {
        traceSub(id, null, beforeTime);
    }

    public static void traceQuote(String id, String param) {
        traceSub(id, param, 0);
    }

    public static void traceQuote(String id, String param, long beforeTime) {
        traceSub(id, param, beforeTime);
    }

    public static void trace(String id, int param) {
        traceSub(id, String.valueOf(param), 0);
    }

    public static void trace(String id, String param) {
        traceSub(id, param, 0);
    }

    public static void traceTime(long time) {
        // TODO:NEED HGY AUDIT.
        println("|costtime=" + (System.currentTimeMillis() - time) + "ms|");
    }

    public static void traceResult(String result) {
        println(" " + result);
    }

    public static void traceSQL(String result) {
        println(result);
    }

    public static void traceResultQuote(String result) {
        println("return " + quote(result));
    }

    public static void traceException(String sql, Exception e) {
//		if (logger.isErrorEnabled()) {
        StringBuffer sb = new StringBuffer();
        if (sql != null)
            sb.append('<').append(sql).append('>');
        sb.append("throws ").append(e.getMessage());
        log.error(sb.toString(), e);
//		}
    }

    public static String quote(String[] s) {
        if (s == null) {
            return "null";
        }
        StringBuffer buff = new StringBuffer("{");
        for (int i = 0; i < s.length; i++) {
            if (i > 0) {
                buff.append(',');
            }
            buff.append(quote(s[i]));
        }
        buff.append('}');
        return buff.toString();
    }

    public static String quote(String s) {
        if (s == null) {
            return "null";
        }
        StringBuffer buff = new StringBuffer("\"");
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '\b': // 0x8
                    buff.append("\\b");
                    break;
                case '\t': // 0x9
                    buff.append("\\t");
                    break;
                case '\n': // 0xa
                    buff.append("\\n");
                    break;
                case '\f': // 0xc
                    buff.append("\\f");
                    break;
                case '\r': // 0xd
                    buff.append("\\r");
                    break;
                case '\\': // 0x5c
                    buff.append("\\\\");
                    break;
                case '"': // 0x22
                    buff.append("\\\"");
                    break;
                default:
                    buff.append(c);
            }
        }
        buff.append("\"");
        return buff.toString();
    }

    public static String quote(int[] data) {
        if (data == null) {
            return "null";
        }
        StringBuffer buff = new StringBuffer("{");
        for (int i = 0; i < data.length; i++) {
            if (i > 0) {
                buff.append(',');
            }
            buff.append(data[i]);
        }
        buff.append('}');
        return buff.toString();
    }

    public static String quoteObject(Object o) {
        if (o == null) {
            return "null";
        }
        return "(" + o.getClass().getName() + ")[o_"
                + System.identityHashCode(o) + "]{" + o + "}";
    }

    static String getTimeStampString(long l) {
        Date d = new Date(l);
        Calendar cl = Calendar.getInstance();
        cl.setTime(d);
        int ia[] = new int[5];
        int year = cl.get(Calendar.YEAR);
        ia[0] = cl.get(Calendar.MONTH) + 1;
        ia[1] = cl.get(Calendar.DAY_OF_MONTH);
        ia[2] = cl.get(Calendar.HOUR_OF_DAY);
        ia[3] = cl.get(Calendar.MINUTE);
        ia[4] = cl.get(Calendar.SECOND);
        byte ba[] = new byte[19];
        ba[4] = ba[7] = (byte) '-';
        ba[10] = (byte) ' ';
        ba[13] = ba[16] = (byte) ':';
        ba[0] = (byte) (year / 1000 + '0');
        ba[1] = (byte) ((year / 100) % 10 + '0');
        ba[2] = (byte) ((year / 10) % 10 + '0');
        ba[3] = (byte) (year % 10 + '0');
        for (int i = 0; i < 5; i++) {
            ba[i * 3 + 5] = (byte) (ia[i] / 10 + '0');
            ba[i * 3 + 6] = (byte) (ia[i] % 10 + '0');
        }
        return new String(ba);
    }

    static public void println(String msg) {
        log.debug(msg);
    }

    static public void println(String msg, long time) {
        // TODO:NEED HGY AUDIT.
        StringBuffer result = new StringBuffer();
        if (time == 0)
            result.append("|costtime=0ms|").append(msg);
        else
            result.append("|costtime=")
                    .append(System.currentTimeMillis() - time).append("ms|")
                    .append(msg);
        log.debug(result.toString());
    }

    static public void traceDetail(String msg) {
        log.info(msg);
    }

    public static void main(String[] args) {
        Trace.println("asdfasdfas", System.currentTimeMillis());
    }
}
