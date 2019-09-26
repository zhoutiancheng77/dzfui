package com.dzf.zxkj.base.utils;

import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 */
public class TimeUtils {

	/**
	 * 比较日期先后，对象日期在参数日期之前为true，如果有一个为null，返回false
	 * 
	 * @param d1
	 *            日期
	 * @param d2
	 *            日期
	 * @return
	 */
	public static boolean before(DZFDate d1, DZFDate d2) {
		if (null == d1 || null == d2) {
			return false;
		}
		return d1.before(d2);
	}

	/**
	 * 比较日期时间先后，true为之前，如果有一个为null，返回false
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
	 * 获得日期加结束时间YYYY-MM-DD 23:59:59
	 * 
	 * @param billDate
	 * @return
	 */
	public static DZFDate getEndDate(DZFDate billDate) {
		if (billDate == null) {
			return null;
		}
		DZFDate endDate = new DZFDate(billDate.toString(), false);
		return endDate;
	}

	/**
	 * 取得日期所在月的第一天
	 * 
	 * @param date
	 * @return
	 */
	public static Date getFirstMonthDay(Date date) {
		GregorianCalendar gc = TimeUtils.getCalendar();
		TimeUtils.initCalendar(date, gc);
		int dayOfMonth = gc.get(Calendar.DAY_OF_MONTH);
		gc.add(Calendar.DAY_OF_MONTH, 1 - dayOfMonth);
		return gc.getTime();
	}

	/**
	 * 取得日期所在月的第一天
	 * 
	 * @param ufdate
	 * @return
	 */
	public static DZFDate getFirstMonthDay(DZFDate ufdate) {
		TimeUtils.checkDate(ufdate);
		return new DZFDate(TimeUtils.getFirstMonthDay(ufdate.toDate()));
	}

	/**
	 * 取得日期所在月的最后一天
	 * 
	 * @param date
	 * @return
	 */
	public static Date getLastMonthDay(Date date) {
		GregorianCalendar gc = TimeUtils.getCalendar();
		TimeUtils.initCalendar(date, gc);
		int dayOfMonth = gc.get(Calendar.DAY_OF_MONTH);
		int maxDaysOfMonth = gc.getActualMaximum(Calendar.DAY_OF_MONTH);
		gc.add(Calendar.DAY_OF_MONTH, maxDaysOfMonth - dayOfMonth);
		return gc.getTime();
	}

	/**
	 * 取得日期所在月的最后一天
	 * 
	 * @param ufdate
	 * @return
	 */
	public static DZFDate getLastMonthDay(DZFDate ufdate) {
		TimeUtils.checkDate(ufdate);
		return new DZFDate(TimeUtils.getLastMonthDay(ufdate.toDate()));
	}

	/**
	 * 操作总计耗时转化为时分秒显示
	 * 
	 * @param t
	 * @return
	 */
	public static String getOperUsedTime(long t) {
		int iHour = (int) (t / 3600000L);
		int iMinutes = (int) ((t - iHour * 3600000L) / 60000);
		int iSecond = (int) ((t - iHour * 3600000L - iMinutes * 60000L) / 1000);
		int iOther = (int) (t - iHour * 3600000L - iMinutes * 60000L - iSecond * 1000L);

		String msg = String.format("共耗时: {%s}小时{%s}分钟{%s}秒{%s}毫秒", String.valueOf(iHour), String.valueOf(iMinutes),
				String.valueOf(iSecond), String.valueOf(iOther));

		return msg;
	}

	/**
	 * 方法功能描述: 获取操作耗用时间提示信息.
	 * <p>
	 * <b>参数说明</b>
	 * 
	 * @param t2
	 *            操作结束点 通过System.currentTimeMillis()得到
	 * @param t1
	 *            操作开始点 通过System.currentTimeMillis()得到
	 * @return
	 *         <p>
	 * @since 6.0
	 * @author 皮之兵
	 * @time 下午06:21:23
	 */
	public static String getOperUsedTime(long t2, long t1) {
		long t = t2 - t1;
		int iHour = (int) (t / 3600000L);
		int iMinutes = (int) ((t - iHour * 3600000L) / 60000);
		int iSecond = (int) ((t - iHour * 3600000L - iMinutes * 60000L) / 1000);
		int iOther = (int) (t - iHour * 3600000L - iMinutes * 60000L - iSecond * 1000L);

		String msg = String.format("共耗时: {%s}小时{%s}分钟{%s}秒{%s}毫秒", String.valueOf(iHour), String.valueOf(iMinutes),
				String.valueOf(iSecond), String.valueOf(iOther));

		return msg;
	}

	/**
	 * 获得日期加开始时间YYYY-MM-DD 00:00:00
	 * 
	 * @param billDate
	 * @return
	 */
	public static DZFDate getStartDate(DZFDate billDate) {
		if (billDate == null) {
			return null;
		}
		DZFDate startDate = new DZFDate(billDate.toString(), true);
		return startDate;
	}

	private static void checkDate(DZFDate ufdate) {
		if (ufdate == null) {
			throw new IllegalArgumentException("argument date must be not null");
		}
	}

	private static GregorianCalendar getCalendar() {
		GregorianCalendar gc = new GregorianCalendar();
		gc.setLenient(true);
		gc.setFirstDayOfWeek(Calendar.MONDAY);
		return gc;
	}

	private static void initCalendar(Date date, GregorianCalendar gc) {
		if (date == null) {
			throw new IllegalArgumentException("argument date must be not null");
		}

		gc.clear();
		gc.setTime(date);
	}
}
