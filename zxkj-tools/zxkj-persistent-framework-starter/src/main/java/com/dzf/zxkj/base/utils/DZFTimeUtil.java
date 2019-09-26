package com.dzf.zxkj.base.utils;

import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Time类型工具类
 * 
 */
public class DZFTimeUtil {

    /**
     * 比较日期
     * 
     * @param d1
     * @param d2
     * @return
     */
    public static boolean before(DZFDate d1, DZFDate d2) {
        if (null == d1 || null == d2) {
            return false;
        }
        return d1.before(d2);
    }

    /**
     * 比较时间
     * 
     * @param t1
     * @param t2
     * @return
     */
    public static boolean before(DZFDateTime t1, DZFDateTime t2) {
        if (null == t1 || null == t2) {
            return false;
        }
        return t1.before(t2);
    }


    /**
     * 转换给定日期（开始时间格式）
     * 
     * @param billDate
     * @return
     */
    public static DZFDate getStartDate(DZFDate billDate) {
        return null == billDate ? null : new DZFDate(billDate.toString(), true);
    }


    /**
     * 转换给定日期（结束时间格式）
     * 
     * @param billDate
     * @return
     */
    public static DZFDate getEndDate(DZFDate billDate) {
        return null == billDate ? null : new DZFDate(billDate.toString(), false);
    }


    /**
     * 获取给定日期所在月份第一天
     * 
     * @param date
     * @return
     */
    public static Date getFirstMonthDay(Date date) {
        GregorianCalendar gc = DZFTimeUtil.getCalendar();
        DZFTimeUtil.initCalendar(date, gc);
        int dayOfMonth = gc.get(Calendar.DAY_OF_MONTH);
        gc.add(Calendar.DAY_OF_MONTH, 1 - dayOfMonth);
        return gc.getTime();
    }

    /**
     * 获取给定日期所在月份第一天
     * 
     * @param ufdate
     * @return
     */
    public static DZFDate getFirstMonthDay(DZFDate ufdate) {
        DZFTimeUtil.checkDate(ufdate);
        return new DZFDate(TimeUtils.getFirstMonthDay(ufdate.toDate()));
    }

    /**
     * 获取给定日期所在月份最后一天
     * 
     * @param date
     * @return
     */
    public static Date getLastMonthDay(Date date) {
        GregorianCalendar gc = DZFTimeUtil.getCalendar();
        DZFTimeUtil.initCalendar(date, gc);
        int dayOfMonth = gc.get(Calendar.DAY_OF_MONTH);
        int maxDaysOfMonth = gc.getActualMaximum(Calendar.DAY_OF_MONTH);
        gc.add(Calendar.DAY_OF_MONTH, maxDaysOfMonth - dayOfMonth);
        return gc.getTime();
    }

    /**
     * 获取给定日期所在月份最后一天
     * 
     * @param ufdate
     * @return
     */
    public static DZFDate getLastMonthDay(DZFDate ufdate) {
        DZFTimeUtil.checkDate(ufdate);
        return new DZFDate(TimeUtils.getLastMonthDay(ufdate.toDate()));
    }

    /**
     * 方法功能描述：DZFDate型对象空值检查，若为空则抛出异常
     * 
     * @param ufdate 待检查的DZFDate型日期参数
     * @throws IllegalArgumentException 变量异常，传入日期参数为空时抛出
     */
    private static void checkDate(DZFDate ufdate) {
        if (null == ufdate) {
            throw new IllegalArgumentException("argument date must be not null");
        }
    }

    /**
     * 方法功能描述：获得一个系统的GregorianCalendar（罗马历）型对象
     * 其中已设置日期计算方式为偏移值（不会因为天数超过月份最大值而抛出异常）；以及每周第一天为周一（默认为周日）。
     * 
     * @return GregorianCalendar型对象
     */
    private static GregorianCalendar getCalendar() {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setLenient(true);
        gc.setFirstDayOfWeek(Calendar.MONDAY);
        return gc;
    }

    /**
     * 方法功能描述：初始化一个GregorianCalendar（罗马历）型对象，使其日期设置为传入的日期
     * 若传入的日期为空，则抛出异常
     * 
     * @param date 传入的要设置的Date型日期参数
     * @param gc 传入的要设置日期的GregorianCalendar型对象
     * @throws IllegalArgumentException 若传入的日期为空，则抛出异常
     */
    private static void initCalendar(Date date, GregorianCalendar gc) {
        if (null == gc) {
            return;
        }
        if (null == date) {
            throw new IllegalArgumentException("argument date must be not null");
        }
        gc.clear();
        gc.setTime(date);
    }
}
