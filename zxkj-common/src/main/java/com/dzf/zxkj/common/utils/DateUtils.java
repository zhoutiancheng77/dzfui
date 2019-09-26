/*
 * @(#)DateUtils.java 2006-9-23
 * Copyright 2006 UFIDA Software CO.LTD. All rights reserved.
 */
package com.dzf.zxkj.common.utils;

import com.dzf.zxkj.common.lang.DZFDate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class DateUtils {
	private static Calendar CALENDAR = Calendar.getInstance();
	
	/** 存放不同的日期模板格式的sdf的Map */
	private static ThreadLocal<Map<String, SimpleDateFormat>> sdfMap = new ThreadLocal<Map<String, SimpleDateFormat>>() {
		protected Map<String, SimpleDateFormat> initialValue() {
			return new HashMap<String, SimpleDateFormat>();
		}
	};

	/**
	 * 返回一个SimpleDateFormat,每个线程只会new一次pattern对应的sdf
	 * 
	 * @param pattern
	 * @return
	 */
	private static SimpleDateFormat getSdf(final String pattern) {
		Map<String, SimpleDateFormat> tl = sdfMap.get();
		SimpleDateFormat sdf = tl.get(pattern);
		if (sdf == null) {
			sdf = new SimpleDateFormat(pattern);
			tl.put(pattern, sdf);
		}
		return sdf;
	}

	/**
	 * 这样每个线程只会有一个SimpleDateFormat
	 * 
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static String format(Date date, String pattern) {
		return getSdf(pattern).format(date);
	}

	public static Date parse(String dateStr, String pattern) throws ParseException {
		return getSdf(pattern).parse(dateStr);
	}

	public static Date endOfDay(Date date) {
		Calendar calendar = CALENDAR;
		synchronized (calendar) {
			calendar.setTime(date);
			calendar.set(Calendar.HOUR_OF_DAY, 23);
			calendar.set(Calendar.MILLISECOND, 999);
			calendar.set(Calendar.SECOND, 59);
			calendar.set(Calendar.MINUTE, 59);
			return calendar.getTime();
		}
	}


	public static Date startOfDay(Date date) {
		Calendar calendar = CALENDAR;
		synchronized (calendar) {
			calendar.setTime(date);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MINUTE, 0);
			return calendar.getTime();
		}
	}


	public static long startOfDayInMillis(long date) {
		Calendar calendar = CALENDAR;
		synchronized (calendar) {
			calendar.setTimeInMillis(date);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MINUTE, 0);
			return calendar.getTimeInMillis();
		}
	}


	public static long endOfDayInMillis(long date) {
		Calendar calendar = CALENDAR;
		synchronized (calendar) {
			calendar.setTimeInMillis(date);
			calendar.set(Calendar.HOUR_OF_DAY, 23);
			calendar.set(Calendar.MILLISECOND, 999);
			calendar.set(Calendar.SECOND, 59);
			calendar.set(Calendar.MINUTE, 59);
			return calendar.getTimeInMillis();
		}
	}


	public static Date nextDay(Date date) {
		return new Date(addDays(date.getTime(), 1));
	}

	public static long addDays(long time, int amount) {
		Calendar calendar = CALENDAR;
		synchronized (calendar) {
			calendar.setTimeInMillis(time);
			calendar.add(Calendar.DAY_OF_MONTH, amount);
			return calendar.getTimeInMillis();
		}
	}


	public static long nextDay(long date) {
		return addDays(date, 1);
	}


	public static long nextWeek(long date) {
		return addDays(date, 7);
	}


	public static int getDaysDiff(long t1, long t2, boolean checkOverflow) {
		if (t1 > t2) {
			long tmp = t1;
			t1 = t2;
			t2 = tmp;
		}
		Calendar calendar = CALENDAR;
		synchronized (calendar) {
			calendar.setTimeInMillis(t1);
			int delta = 0;
			while (calendar.getTimeInMillis() < t2) {
				calendar.add(Calendar.DAY_OF_MONTH, 1);
				delta++;
			}
			if (checkOverflow && (calendar.getTimeInMillis() > t2)) {
				delta--;
			}
			return delta;
		}
	}

	public static int getDaysDiff(long t1, long t2) {
		return getDaysDiff(t1, t2, true);
	}

	
	public static boolean isFirstOfYear(long date) {
		boolean ret = false;
		Calendar calendar = CALENDAR;
		synchronized (calendar) {
			calendar.setTimeInMillis(date);
			int currentYear = calendar.get(Calendar.YEAR);
			// Check yesterday
			calendar.add(Calendar.DATE, -1);
			int yesterdayYear = calendar.get(Calendar.YEAR);
			ret = (currentYear != yesterdayYear);
		}
		return ret;
	}


	public static boolean isFirstOfMonth(long date) {
		boolean ret = false;
		Calendar calendar = CALENDAR;
		synchronized (calendar) {
			calendar.setTimeInMillis(date);
			int currentMonth = calendar.get(Calendar.MONTH);
			// Check yesterday
			calendar.add(Calendar.DATE, -1);
			int yesterdayMonth = calendar.get(Calendar.MONTH);
			ret = (currentMonth != yesterdayMonth);
		}
		return ret;
	}


	public static long previousDay(long date) {
		return addDays(date, -1);
	}


	public static long previousWeek(long date) {
		return addDays(date, -7);
	}


	public static long getPreviousDay(long date, int startOfWeek) {
		return getDay(date, startOfWeek, -1);
	}

	public static long getNextDay(long date, int startOfWeek) {
		return getDay(date, startOfWeek, 1);
	}

	private static long getDay(long date, int startOfWeek, int increment) {
		Calendar calendar = CALENDAR;
		synchronized (calendar) {
			calendar.setTimeInMillis(date);
			int day = calendar.get(Calendar.DAY_OF_WEEK);
			// Normalize the view starting date to a week starting day
			while (day != startOfWeek) {
				calendar.add(Calendar.DATE, increment);
				day = calendar.get(Calendar.DAY_OF_WEEK);
			}
			return startOfDayInMillis(calendar.getTimeInMillis());
		}
	}

	public static long getPreviousMonth(long date) {
		return incrementMonth(date, -1);
	}
	
	//取前一年的日期，即参数：2017-03，结果为2016-03
	public static String getPreviousYearPeriod(String period){
		DZFDate date=new DZFDate(period+"-01");
		long l=getPreviousYear(date.toDate().getTime());
		date=new DZFDate(l);
		String str=date.toString();
		return str.substring(0, 7);
	}
	
	public static String getNextPeriod(String period) {
		DZFDate date=new DZFDate(period+"-01");
		long l=getNextMonth(date.toDate().getTime());
		date=new DZFDate(l);
		String str=date.toString();
		return str.substring(0, 7);
	}
	
	public static String getPreviousPeriod(String period) {
		DZFDate date=new DZFDate(period+"-01");
		long l=getPreviousMonth(date.toDate().getTime());
		date=new DZFDate(l);
		String str=date.toString();
		return str.substring(0, 7);
	}
	public static DZFDate getPeriodEndDate(String period) {
		DZFDate date=new DZFDate(period+"-01");
		long l=getEndOfMonth(date.toDate().getTime());
		date=new DZFDate(l);

		return date;
	}
	public static DZFDate getPeriodStartDate(String period) {
		DZFDate date=new DZFDate(period+"-01");

		return date;
	}
	public static String getPeriod(DZFDate enddate) {
		String period=enddate.getYear()+"-"+(enddate.getMonth()<10?"0"+enddate.getMonth():enddate.getMonth());
		return period;
	}

	public static long getPreviousYear(long date) {
		return incrementYear(date, -1);
	}

	public static long getNextYear(long date) {
		return incrementYear(date, 1);
	}


	public static long getNextMonth(long date) {
		return incrementMonth(date, 1);
	}
	
	/**
     * 取指定日期往后推N个月
     * @param ddate
     * @param n
     * @return
     */
    public static String getNextNMonth(DZFDate ddate,int n){
        long millis = incrementMonth(ddate.getMillis(), n);
        return new DZFDate(millis).toString();
    }

	private static long incrementYear(long date, int increment) {
		Calendar calendar = CALENDAR;
		synchronized (calendar) {
			calendar.setTimeInMillis(date);
			calendar.add(Calendar.YEAR, increment);
			return calendar.getTimeInMillis();
		}
	}

	private static long incrementMonth(long date, int increment) {
		Calendar calendar = CALENDAR;
		synchronized (calendar) {
			calendar.setTimeInMillis(date);
			calendar.add(Calendar.MONTH, increment);
			return calendar.getTimeInMillis();
		}
	}


	public static long getStartOfMonth(long date) {
		return getMonth(date, -1);
	}


	public static long getEndOfMonth(long date) {
		return getMonth(date, 1);
	}

	private static long getMonth(long date, int increment) {
		Calendar calendar = CALENDAR;
		synchronized (calendar) {
			calendar.setTimeInMillis(date);
			if (increment == -1) {
				calendar.set(Calendar.DAY_OF_MONTH, 1);
				return startOfDayInMillis(calendar.getTimeInMillis());
			} else {
				calendar.add(Calendar.MONTH, 1);
				calendar.set(Calendar.DAY_OF_MONTH, 1);
				calendar.set(Calendar.HOUR_OF_DAY, 0);
				calendar.set(Calendar.MILLISECOND, 0);
				calendar.set(Calendar.SECOND, 0);
				calendar.set(Calendar.MINUTE, 0);
				calendar.add(Calendar.MILLISECOND, -1);
				return calendar.getTimeInMillis();
			}
		}
	}


	public static int getDayOfWeek(long date) {
		Calendar calendar = CALENDAR;
		synchronized (calendar) {
			calendar.setTimeInMillis(date);
			return (calendar.get(Calendar.DAY_OF_WEEK));
		}
	}


	public static String getDate(Date d) {
		if (d == null)
			return "";
		SimpleDateFormat dataFormate = new SimpleDateFormat("yyyy-MM-dd");
		return dataFormate.format(d);
	}
}
