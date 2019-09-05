package com.dzf.zxkj.common.lang;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;

public final class DZFDate implements java.io.Serializable, Comparable {
    static final long serialVersionUID = -1037968151602108293L;

    private String value = null;

    private static final long millisPerDay = 24 * 60 * 60 * 1000;

//	private static final int LRUSIZE = 2000;

//	private static class LRUMap<K, V> extends LinkedHashMap<K, V> {
//
//		private static final long serialVersionUID = 1L;
//
//		public LRUMap(int initSize) {//初始容量/////还是用默认加载因子0.75///// 2048 * 0.75 = 1536 ，基本5年的日期存储
//			super(initSize, 0.75f, true);
//		}
//
//		protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
//			if (size() > LRUSIZE)
//				return true;
//			else
//				return false;
//		}
//	}

    private final static Map<Object, DZFDate> allUsedDate = new ConcurrentHashMap<Object, DZFDate>();

    static {
        //365 * 4 ,预制4年数据进去。
        //从启动时刻，往前3年，往后1年。
        Date nowdate = new Date();//当前时间
        Calendar calendar = Calendar.getInstance();//日历对象
        calendar.setTime(nowdate);//设置当前日期
        calendar.add(Calendar.YEAR, -3);
        Date startdate = calendar.getTime();
        calendar.add(Calendar.YEAR, 4);
        Date enddate = calendar.getTime();
        calendar.setTime(startdate);
        while (startdate.compareTo(enddate) <= 0) {
            DZFDate date = new DZFDate(startdate);
            allUsedDate.put(date.toString(), date);
            calendar.add(Calendar.DATE, 1);
            startdate = calendar.getTime();
        }
    }


//	private final static Map<Object, DZFDate> allUsedDate = new LRUMap<Object, DZFDate>(2048);

//	private static ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();

    private transient Long currentLong = null;


    public DZFDate() {
        this(new Date());
    }


    public DZFDate(long m) {
        GregorianCalendar cal = new GregorianCalendar(TimeZone
                .getTimeZone("Asia/Shanghai"));
        cal.setTimeInMillis(m);
        value = toDateString(cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));
    }

    public DZFDate(String strDate) {
        this(strDate, true);
    }


    public DZFDate(String strDate, boolean isParse) {
        if (isParse) {
            value = internalParse(strDate);
        } else {
            if (strDate == null || strDate.trim().length() != 10) {
                throw new IllegalArgumentException("invalid DZFDate:" + strDate);
            }
            value = strDate.trim();
        }
    }

    public DZFDate(java.sql.Date date) {
        this((Date) date);
    }


    public DZFDate(Date date) {
        value = toDateString(date);
    }


    public boolean after(DZFDate when) {
        return this.compareTo(when) > 0;
    }


    public boolean before(DZFDate when) {
        return this.compareTo(when) < 0;
    }


    public Object clone() {
        return new DZFDate(value);
    }


    public int compareTo(DZFDate when) {
        return compareTo(when.getMillis());
    }


    private int compareTo(Long whenLong) {
        long retl = this.getMillis() - whenLong;
        if (retl == 0)
            return 0;
        else
            return retl > 0 ? 1 : -1;
    }

    public boolean equals(Object o) {
        if ((o != null) && (o instanceof DZFDate)) {
            return this.getMillis() == ((DZFDate) o).getMillis();
        }
        return false;
    }

    public static DZFDate getDate(long d) {

        return getDate(d, false);
    }

    public static DZFDate getDate(String strDate) {
        if (strDate == null || strDate.trim().length() == 0)
            return null;
        return getDate(strDate, true);
    }

    public static DZFDate getDate(Date date) {
        String strDate = toDateString(date);
        return getDate(strDate, false);
    }

    public static DZFDate getDate(String date, boolean check) {
        return getDate((Object) date, check);
    }

    public static DZFDate getDate(Long date) {
        return getDate((Object) date, false);
    }

    private static DZFDate getDate(Object date, boolean check) {
        if (date instanceof Long || date instanceof String) {
            DZFDate redate = allUsedDate.get(date);
            if (redate != null) {
                return redate;
            } else {
                return toUFDate(date, check);
            }
//			if (rwl.readLock().tryLock()) {
//				try {
//					DZFDate o = (DZFDate) allUsedDate.get(date);
//					if (o == null) {
//						rwl.readLock().unlock();
//						rwl.writeLock().lock();
//						try{
//							o = (DZFDate) allUsedDate.get(date);//再次取值
//							if(o == null){
//								o = toUFDate(date, check);
//								allUsedDate.put(date, o);
//							}
//							rwl.readLock().lock();//降级读锁
//						}finally{
//							rwl.writeLock().unlock();
//						}
//						return o;
//					}else{
//						return o;
//					}
//				}finally{
//					rwl.readLock().unlock();
//				}
//			} else {
//				return toUFDate(date, check);
//			}
        } else {
            throw new IllegalArgumentException(
                    "expect long or string parameter as the first parameter");
        }
    }
/*	private static DZFDate getDate(Object date, boolean check) {
		if (date instanceof Long || date instanceof String) {
			//for performance
			if (rwl.readLock().tryLock()) {
				boolean isReadLocked = true;
				try {
					DZFDate o = (DZFDate) allUsedDate.get(date);
					if (o == null) {
						DZFDate n = toUFDate(date, check);
						rwl.readLock().unlock();
						if (rwl.writeLock().tryLock())
						{
							try {
								o = allUsedDate.get(date);
								if (o == null) {
									o = n;
									allUsedDate.put(date, o);
								}
							} finally {
								rwl.readLock().lock();
								rwl.writeLock().unlock();
							}
						}
						else
						{
							//获取写锁失败，但读锁已释放
							isReadLocked = false;
							return n;
						}
					}
					return o;
				} finally {
					if (isReadLocked) {
						rwl.readLock().unlock();
					}
				}
			} else {
				return toUFDate(date, check);
			}
		} else {
			throw new IllegalArgumentException(
					"expect long or string parameter as the first parameter");
		}
	}*/

    private static DZFDate toUFDate(Object date, boolean check) {
        if (date instanceof String)
            return new DZFDate((String) date, check);
        else
            return new DZFDate((Long) date);
    }


    public DZFDate getDateAfter(int days) {
        long l = getMillis() + millisPerDay * days;
        Date date = new Date(l);
        return getDate(date);

    }


    public DZFDate getDateBefore(int days) {
        return getDateAfter(-days);
    }

    public int getDay() {
        return Integer.parseInt(value.substring(8, 10));
    }


    public int getDaysAfter(DZFDate when) {
        int days = 0;
        if (when != null) {
            days = (int) ((this.getMillis() - when.getMillis()) / millisPerDay);
        }
        return days;
    }


    public static int getDaysBetween(DZFDate begin, DZFDate end) {
        int days = 0;
        if (begin != null && end != null) {
            days = (int) ((end.getMillis() - begin.getMillis()) / millisPerDay);
        }
        return days;
    }

    public int getDaysMonth() {
        return getDaysMonth(getYear(), getMonth());
    }

    public static int getDaysMonth(int year, int month) {
        switch (month) {
            case 1:
                return 31;
            case 2:
                if (isLeapYear(year))
                    return 29;
                else
                    return 28;
            case 3:
                return 31;
            case 4:
                return 30;
            case 5:
                return 31;
            case 6:
                return 30;
            case 7:
                return 31;
            case 8:
                return 31;
            case 9:
                return 30;
            case 10:
                return 31;
            case 11:
                return 30;
            case 12:
                return 31;
            default:
                return 30;
        }
    }

    public String getEnMonth() {
        switch (getMonth()) {
            case 1:
                return "Jan";
            case 2:
                return "Feb";
            case 3:
                return "Mar";
            case 4:
                return "Apr";
            case 5:
                return "May";
            case 6:
                return "Jun";
            case 7:
                return "Jul";
            case 8:
                return "Aug";
            case 9:
                return "Sep";
            case 10:
                return "Oct";
            case 11:
                return "Nov";
            case 12:
                return "Dec";
        }
        return null;
    }


    public String getEnWeek() {
        switch (getWeek()) {
            case 0:
                return "Sun";
            case 1:
                return "Mon";
            case 2:
                return "Tue";
            case 3:
                return "Wed";
            case 4:
                return "Thu";
            case 5:
                return "Fri";
            case 6:
                return "Sat";
        }
        return null;
    }


    public int getMonth() {
        return Integer.parseInt(value.substring(5, 7));
    }

    public String getStrDay() {
        return value.substring(8, 10);
    }

    public String getStrMonth() {
        return value.substring(5, 7);
    }


    public int getWeek() {
        int days = getDaysAfter(new DZFDate("1980-01-06"));
        int week = days % 7;
        if (week < 0)
            week += 7;
        return week;
    }

    public int getYear() {
        return Integer.parseInt(value.substring(0, 4));
    }


    public boolean isLeapYear() {
        return isLeapYear(getYear());
    }


    public static boolean isLeapYear(int year) {
        if ((year % 4 == 0) && (year % 100 != 0 || year % 400 == 0))
            return true;
        else
            return false;
    }

    public String toString() {
        return value == null ? "" : value;
    }

    public int compareTo(Object o) {
        if (o instanceof DZFDate)
            return compareTo((DZFDate) o);
        else if (o instanceof DZFDateTime)
            return compareTo(((DZFDateTime) o).getMillis());
        else
            throw new IllegalArgumentException();
    }


    public long getMillis() {
        if (currentLong == null) {
            GregorianCalendar cal = new GregorianCalendar(TimeZone
                    .getTimeZone("Asia/Shanghai"));
            cal.set(Calendar.YEAR, getYear());
            cal.set(Calendar.MONTH, getMonth() - 1);
            cal.set(Calendar.DAY_OF_MONTH, getDay());
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            currentLong = cal.getTimeInMillis();

        }
        return currentLong;

    }


    public int getWeekOfYear() {
        GregorianCalendar calendar = new GregorianCalendar(getYear(),
                getMonth(), getDay());
        return calendar.get(Calendar.WEEK_OF_YEAR);
    }


    public Date toDate() {
        return new Date(getMillis());
    }


    @Override
    public int hashCode() {
        return value == null ? 17 : value.hashCode();
    }

    private static String internalParse(String sDate) {

        if (sDate == null)
            throw new IllegalArgumentException("invalid DZFDate: " + sDate);

        sDate = sDate.trim();
        String[] tokens = new String[3];

        StringTokenizer st = new StringTokenizer(sDate, "-/.");

        if (st.countTokens() == 1 && sDate.length() == 8) {
            sDate = sDate.substring(0, 4) + "-" + sDate.substring(4, 6) + "-" + sDate.substring(6, 8);
            st = new StringTokenizer(sDate, "-/.");
        }
        if (st.countTokens() != 3) {
            throw new IllegalArgumentException("invalid DZFDate: " + sDate);
        }

        int i = 0;
        while (st.hasMoreTokens()) {
            tokens[i++] = st.nextToken().trim();
        }

        try {
            int year = Integer.parseInt(tokens[0]);
            int month = Integer.parseInt(tokens[1]);
            if (month < 1 || month > 12)
                throw new IllegalArgumentException("invalid DZFDate: " + sDate);
            int day = Integer.parseInt(tokens[2]);

            int MONTH_LENGTH[] = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30,
                    31};
            int LEAP_MONTH_LENGTH[] = {31, 29, 31, 30, 31, 30, 31, 31, 30, 31,
                    30, 31};
            int daymax = isLeapYear(year) ? LEAP_MONTH_LENGTH[month - 1]
                    : MONTH_LENGTH[month - 1];

            if (day < 1 || day > daymax)
                throw new IllegalArgumentException("invalid DZFDate: " + sDate);

            String strYear = tokens[0];
            for (int j = strYear.length(); j < 4; j++) {
                if (j == 3) {
                    strYear = "2" + strYear;
                } else {
                    strYear = "0" + strYear;
                }
            }

            String strMonth = String.valueOf(month);
            if (strMonth.length() < 2)
                strMonth = "0" + strMonth;
            String strDay = String.valueOf(day);
            if (strDay.length() < 2)
                strDay = "0" + strDay;
            return strYear + "-" + strMonth + "-" + strDay;
        } catch (Throwable thr) {
            if (thr instanceof IllegalArgumentException) {
                throw (IllegalArgumentException) thr;
            } else {
                throw new IllegalArgumentException("invalid DZFDate: " + sDate);
            }
        }

    }

    private static String toDateString(int year, int month, int day) {
        String strYear = String.valueOf(year);
        for (int j = strYear.length(); j < 4; j++)
            strYear = "0" + strYear;
        String strMonth = String.valueOf(month);
        if (strMonth.length() < 2)
            strMonth = "0" + strMonth;
        String strDay = String.valueOf(day);
        if (strDay.length() < 2)
            strDay = "0" + strDay;
        return strYear + "-" + strMonth + "-" + strDay;

    }

    private static String toDateString(Date dt) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Shanghai"));
        cal.setTime(dt);
        return toDateString(cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));
    }


    public static String getValidUFDateString(String sDate) {
        return internalParse(sDate);
    }

}