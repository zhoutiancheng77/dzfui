package com.dzf.zxkj.common.lang;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;


public final class DZFDateTime implements java.io.Serializable, Comparable {
    static final long serialVersionUID = -7539595826392466408L;

    private String value = null;

    private static final long millisPerDay = 24 * 60 * 60 * 1000;

    private static final int millisPerHour = 60 * 60 * 1000;

    private static final int millisPerMinute = 60 * 1000;

    private static final int millisPerSecond = 1000;


    public DZFDateTime() {
        this(new Date());
        //this("1970-01-01 00:00:00");
    }


    public DZFDateTime(long m) {
        Calendar cal = Calendar.getInstance(TimeZone
                .getTimeZone("Asia/Shanghai"));
        cal.setTimeInMillis(m);

        value = toDateTimeString(cal.get(Calendar.YEAR), cal
                .get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH), cal
                .get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal
                .get(Calendar.SECOND));
    }

    private String toDateTimeString(int year, int month, int day, int hour,
                                    int minute, int second) {
        String strYear = String.valueOf(year);
        for (int j = strYear.length(); j < 4; j++)
            strYear = "0" + strYear;
        StringBuffer sb = new StringBuffer(strYear);
        append(sb.append('-'), month);
        append(sb.append('-'), day);
        append(sb.append(' '), hour);
        append(sb.append(':'), minute);
        append(sb.append(':'), second);
        return sb.toString();
    }

    private StringBuffer append(StringBuffer sb, int value) {
        if (value > 9) {
            sb.append(value);
        } else {
            sb.append('0').append(value);
        }

        return sb;
    }


    public DZFDateTime(String strDateTime) {
        this(strDateTime, true);
    }

    public DZFDateTime(java.sql.Date date) {
        this((Date) date);
    }


    public DZFDateTime(Date date) {
        this(sd.format(date));
    }


    public boolean after(DZFDate when) {
        return getDate().toString().compareTo(when.toString()) > 0;
    }


    public boolean after(DZFDateTime dateTime) {
        return value.compareTo(dateTime.toString()) > 0;
    }


    public boolean before(DZFDate when) {
        return getDate().toString().compareTo(when.toString()) < 0;
    }


    public boolean before(DZFDateTime dateTime) {
        return value.compareTo(dateTime.toString()) < 0;
    }


    public Object clone() {
        return new DZFDateTime(value);
    }


    public int compareTo(DZFDate when) {
        return getDate().toString().compareTo(when.toString());
    }


    public int compareTo(DZFDateTime dateTime) {
        return value.compareTo(dateTime.toString());
    }


    public boolean equals(DZFDate when) {
        return getDate().toString().equals(when.toString());
    }


    public DZFDate getDate() {
        return new DZFDate(value.substring(0, 10));
    }


    public DZFDateTime getDateAfter(int days) {
        GregorianCalendar mdate = new GregorianCalendar(
                getYear(), getMonth() - 1, getDay());
        return new DZFDateTime(mdate.getTime().getTime() + millisPerDay * days);
    }


    public int getDay() {
        return Integer.valueOf(value.substring(8, 10)).intValue();
    }

    public int getDaysAfter(DZFDate when) {
        int days = 0;
        if (when != null) {
            GregorianCalendar mdatewhen = new GregorianCalendar(
                    when.getYear(), when.getMonth() - 1, when.getDay());
            GregorianCalendar mdateEnd = new GregorianCalendar(
                    getYear(), getMonth() - 1, getDay());
            days = (int) ((mdateEnd.getTime().getTime() - mdatewhen.getTime()
                    .getTime()) / millisPerDay);
        }
        return days;
    }

    public int getDaysAfter(DZFDateTime begin) {
        return getDaysAfter(begin.getDate());
    }


    public static int getDaysBetween(DZFDate begin, DZFDate end) {
        if (begin != null && end != null) {
            GregorianCalendar mdateBegin = new GregorianCalendar(
                    begin.getYear(), begin.getMonth() - 1, begin.getDay());
            GregorianCalendar mdateEnd = new GregorianCalendar(
                    end.getYear(), end.getMonth() - 1, end.getDay());
            long sbtw = (mdateEnd.getTime().getTime() - mdateBegin.getTime()
                    .getTime());
            return (int) (sbtw / millisPerDay);
        }
        return 0;
    }

    public static int getHoursBetween(DZFDateTime begin, DZFDateTime end) {
        return (int) (getMiliSBetweenTwoDate(begin, end) / millisPerHour);
    }


    public static int getMinutesBetween(DZFDateTime begin, DZFDateTime end) {
        return (int) (getMiliSBetweenTwoDate(begin, end) / millisPerMinute);
    }


    public static int getSecondsBetween(DZFDateTime begin, DZFDateTime end) {
        return (int) (getMiliSBetweenTwoDate(begin, end) / millisPerSecond);
    }


    private static long getMiliSBetweenTwoDate(DZFDateTime begin, DZFDateTime end) {
        if (begin != null && end != null) {
            GregorianCalendar mdateBegin = new GregorianCalendar(
                    begin.getYear(), begin.getMonth() - 1, begin.getDay(),
                    begin.getHour(), begin.getMinute(), begin.getSecond());
            GregorianCalendar mdateEnd = new GregorianCalendar(
                    end.getYear(), end.getMonth() - 1, end.getDay(), end
                    .getHour(), end.getMinute(), end.getSecond());
            long sbtw = (mdateEnd.getTime().getTime() - mdateBegin.getTime()
                    .getTime());
            return sbtw;
        }
        return 0;
    }


    public static int getDaysBetween(DZFDate begin, DZFDateTime end) {
        return getDaysBetween(begin, end.getDate());
    }


    public static int getDaysBetween(DZFDateTime begin, DZFDate end) {
        return getDaysBetween(begin.getDate(), end);
    }


    public static int getDaysBetween(DZFDateTime begin, DZFDateTime end) {
        return getDaysBetween(begin.getDate(), end.getDate());
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


    public int getHour() {
        return Integer.valueOf(value.substring(11, 13)).intValue();
    }


    public long getMillis() {
        GregorianCalendar cal = new GregorianCalendar(TimeZone
                .getTimeZone("Asia/Shanghai"));
        cal.set(Calendar.YEAR, getYear());
        cal.set(Calendar.MONTH, getMonth() - 1);
        cal.set(Calendar.DAY_OF_MONTH, getDay());
        cal.set(Calendar.HOUR_OF_DAY, getHour());
        cal.set(Calendar.MINUTE, getMinute());
        cal.set(Calendar.SECOND, getSecond());
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    public long getMillisAfter(DZFDateTime dateTime) {
        long millis = 0;
        if (dateTime != null) {
            GregorianCalendar mdateBegin = new GregorianCalendar(
                    dateTime.getYear(), dateTime.getMonth() - 1, dateTime
                    .getDay(), dateTime.getHour(),
                    dateTime.getMinute(), dateTime.getSecond());
            GregorianCalendar mdateEnd = new GregorianCalendar(
                    getYear(), getMonth() - 1, getDay(), getHour(),
                    getMinute(), getSecond());
            millis = mdateEnd.getTime().getTime()
                    - mdateBegin.getTime().getTime();
        }
        return millis;
    }


    public int getMinute() {
        return Integer.valueOf(value.substring(14, 16)).intValue();
    }


    public int getMonth() {
        return Integer.valueOf(value.substring(5, 7)).intValue();
    }


    public int getSecond() {
        return Integer.valueOf(value.substring(17, 19)).intValue();
    }

    public String getStrDay() {
        if (getDay() > 0 && getDay() < 10)
            return "0" + Integer.toString(getDay());
        else if (getDay() >= 10 && getDay() < 32)
            return Integer.toString(getDay());
        else
            return null;
    }

    public String getStrMonth() {
        if (getMonth() > 0 && getMonth() < 10)
            return "0" + Integer.toString(getMonth());
        else if (getMonth() >= 10 && getMonth() < 13)
            return Integer.toString(getMonth());
        else
            return null;
    }


    public String getTime() {

        return value.substring(11, 19);
    }


    public DZFTime getUFTime() {
        return new DZFTime(getTime());
    }


    public int getWeek() {
        int days = getDaysAfter(new DZFDate("1980-01-06"));
        int week = days % 7;
        if (week < 0)
            week += 7;
        return week;
    }


    public int getYear() {
        return Integer.valueOf(value.substring(0, 4)).intValue();
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

    public boolean equals(Object o) {
        if ((o != null) && (o instanceof DZFDateTime)) {
            return value.equals(o.toString());
        }
        return false;
    }


    public DZFDateTime getDateBefore(int days) {
        return getDateAfter(-days);
    }

    public static String getValidUFDateTimeString(String sDateTime) {
        if (sDateTime == null)
            return null;
        int index = sDateTime.indexOf("/");
        if (index >= 0)
            sDateTime = sDateTime.replace('/', '-');
        index = sDateTime.indexOf(".");
        if (index >= 0)
            sDateTime = sDateTime.replace('.', '-');
        if (isAllowDateTime(sDateTime))
            return sDateTime;
        else {
            try {

                index = sDateTime.indexOf("-");
                if (index < 1)
                    return null;
                int year = Integer.parseInt(sDateTime.trim()
                        .substring(0, index));
                //
                String sTemp = sDateTime.trim().substring(index + 1);
                index = sTemp.indexOf("-");
                if (index < 1)
                    return null;
                //
                int month = Integer.parseInt(sTemp.trim().substring(0, index));
                if (month < 1 || month > 12)
                    return null;
                sTemp = sTemp.trim().substring(index + 1);
                index = sTemp.indexOf(" ");
                int day = 1;
                if (index > 0) {
                    day = Integer.parseInt(sTemp.trim().substring(0, index));
                    sTemp = sTemp.trim().substring(index + 1);
                } else {
                    day = Integer.parseInt(sTemp.trim().substring(0));
                    sTemp = "";
                }
                int MONTH_LENGTH[] = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31,
                        30, 31};
                int LEAP_MONTH_LENGTH[] = {31, 29, 31, 30, 31, 30, 31, 31, 30,
                        31, 30, 31};
                int daymax = isLeapYear(year) ? LEAP_MONTH_LENGTH[month - 1]
                        : MONTH_LENGTH[month - 1];
                if (day < 1 || day > daymax)
                    return null;

                int hour = 0;
                int minute = 0;
                int second = 0;
                index = sTemp.indexOf(":");
                if (index < 1) {
                    if (sTemp.trim().length() > 0)
                        hour = Integer.parseInt(sTemp.trim());
                } else {
                    hour = Integer.parseInt(sTemp.trim().substring(0, index));
                    sTemp = sTemp.trim().substring(index + 1);
                    if (sTemp.trim().length() > 0) {
                        index = sTemp.indexOf(":");
                        if (index < 1) {
                            minute = Integer.parseInt(sTemp.trim());
                        } else {
                            minute = Integer.parseInt(sTemp.trim().substring(0,
                                    index));
                            if (sTemp.trim().substring(index + 1).trim()
                                    .length() > 0)
                                second = Integer.parseInt(sTemp.trim()
                                        .substring(index + 1));
                        }
                    }
                }
                if (hour < 0 || hour > 24 || minute < 0 || minute > 59
                        || second < 0 || second > 59)
                    return null;
                //
                String strYear = String.valueOf(year);
                for (int i = strYear.length(); i < 4; i++)
                    strYear = "0" + strYear;
                String strMonth = String.valueOf(month);
                if (strMonth.length() < 2)
                    strMonth = "0" + strMonth;
                String strDay = String.valueOf(day);
                if (strDay.length() < 2)
                    strDay = "0" + strDay;
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
                return strYear + "-" + strMonth + "-" + strDay + " " + strHour
                        + ":" + strMinute + ":" + strSecond;
            } catch (Exception e) {
                return null;
            }
        }
    }

    public static boolean isAllowDate(String strDateTime) {
        if (strDateTime == null || strDateTime.trim().length() == 0)
            return true;
        if (strDateTime.trim().length() > 9)
            return isAllowDate0(strDateTime.substring(0, 10));
        else
            return false;
    }


    public static boolean isAllowDate0(String strDate) {
        if (strDate == null || strDate.trim().length() == 0)
            return true;
        if (strDate.trim().length() != 10)
            return false;
        for (int i = 0; i < 10; i++) {
            char c = strDate.trim().charAt(i);
            if (i == 4 || i == 7) {
                if (c != '-')
                    return false;
            } else if (c < '0' || c > '9')
                return false;
        }
        int year = Integer.parseInt(strDate.trim().substring(0, 4));
        int month = Integer.parseInt(strDate.trim().substring(5, 7));
        if (month < 1 || month > 12)
            return false;
        int day = Integer.parseInt(strDate.trim().substring(8, 10));
        int MONTH_LENGTH[] = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        int LEAP_MONTH_LENGTH[] = {31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30,
                31};
        int daymax = isLeapYear(year) ? LEAP_MONTH_LENGTH[month - 1]
                : MONTH_LENGTH[month - 1];
        if (day < 1 || day > daymax)
            return false;
        return true;
    }

    public static boolean isAllowDateTime(String strDateTime) {
        if (strDateTime == null || strDateTime.trim().length() == 0)
            return true;
        if (strDateTime.trim().length() != 19)
            return false;
        char c = strDateTime.trim().charAt(10);
        if (c != ' ')
            return false;
        if (!isAllowDate(strDateTime) || !isAllowTime(strDateTime))
            return false;
        return true;
    }


    public static boolean isAllowTime(String strDateTime) {
        if (strDateTime == null || strDateTime.trim().length() == 0)
            return true;
        if (strDateTime.trim().length() != 19)
            return false;
        for (int i = 11; i < 19; i++) {
            char c = strDateTime.trim().charAt(i);
            if (i == 13 || i == 16) {
                if (c != ':')
                    return false;
            } else if (c < '0' || c > '9')
                return false;
        }
        int hour = Integer.parseInt(strDateTime.trim().substring(11, 13));
        int minute = Integer.parseInt(strDateTime.trim().substring(14, 16));
        int second = Integer.parseInt(strDateTime.trim().substring(17, 19));
        if (hour < 0 || hour > 24 || minute < 0 || minute > 59 || second < 0
                || second > 59)
            return false;
        return true;
    }

    private static SimpleDateFormat sd = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss");

    static {
        sd.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
    }


    public DZFDateTime(String strDateTime, boolean isParse) {
        if (strDateTime == null) {
            throw new IllegalArgumentException("date time can't be null");
        }
        if (isParse) {
            value = getValidUFDateTimeString(strDateTime);
            if (value.length() != 19) {
                throw new IllegalArgumentException("invalid date time:"
                        + strDateTime);
            }
        } else {
            strDateTime = strDateTime.trim();
            if (strDateTime.length() == 10) {
                value = strDateTime + " 00:00:00";
            } else if (strDateTime.length() == 19) {
                value = strDateTime;
            } else {
                throw new IllegalArgumentException("invalid date time:"
                        + strDateTime);
            }
        }

    }


    public DZFDateTime(DZFDate date, DZFTime time) {
        if (date == null || date.toString().trim().length() == 0)
            value = new DZFDate(new Date()).toString();
        else
            value = date.toString();
        if (time == null || time.toString().trim().length() == 0)
            value += " " + new SimpleDateFormat("HH:mm:ss").format(new Date());// "
            // 00:00:00";
        else
            value += " " + time.toString();
    }

    public int compareTo(Object o) {
        if (o instanceof DZFDate)
            return getDate().toString().compareTo(o.toString());
        else if (o instanceof DZFDateTime)
            return value.compareTo(o.toString());
        throw new RuntimeException(
                "Unsupported parameter type while comparing the UFDateTime!");
    }


//	@Override
//	public int hashCode() {
//		return value == null ? 17 : value.hashCode();
//	}
//
//
//	@Override
//	public Object assemble(Serializable arg0, Object arg1)
//			throws HibernateException {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//
//	@Override
//	public Object deepCopy(Object arg0) throws HibernateException {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//
//	@Override
//	public Serializable disassemble(Object arg0) throws HibernateException {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//
//	@Override
//	public boolean equals(Object arg0, Object arg1) throws HibernateException {
//		// TODO Auto-generated method stub
//		return arg0.equals(arg1);
//	}
//
//
//	@Override
//	public int hashCode(Object arg0) throws HibernateException {
//		// TODO Auto-generated method stub
//		return arg0 == null ? 17 : arg0.hashCode();
//	}
//
//
//	@Override
//	public boolean isMutable() {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//
//	@Override
//	public Object nullSafeGet(ResultSet arg0, String[] arg1, Object arg2)
//			throws HibernateException, SQLException {
//		Timestamp ts=(Timestamp) Hibernate.TIMESTAMP.nullSafeGet(arg0,arg1[0]);
//		  if( ts != null){
//		   return new DZFDateTime(ts.getTime());
//		  }else{
//		   return null;
//		  }
//	}
//
//
//	@Override
//	public void nullSafeSet(PreparedStatement arg0, Object value, int arg2)
//			throws HibernateException, SQLException {
//		
//		  if(value != null){
//			  Timestamp ts=  Timestamp.valueOf(((DZFDateTime)value).getTime());
//
//			   Hibernate.TIMESTAMP.nullSafeSet(arg0,ts,arg2);
//			  }else{
//			   Hibernate.TIMESTAMP.nullSafeSet(arg0,value,arg2);
//			  }
//		
//	}
//
//
//	@Override
//	public Object replace(Object arg0, Object arg1, Object arg2)
//			throws HibernateException {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//
//	@Override
//	public Class returnedClass() {
//		// TODO Auto-generated method stub
//		return this.getClass();
//	}
//
//
//	@Override
//	public int[] sqlTypes() {
//		// TODO Auto-generated method stub
//		return new int[]{Types.TIMESTAMP};
//	}
}