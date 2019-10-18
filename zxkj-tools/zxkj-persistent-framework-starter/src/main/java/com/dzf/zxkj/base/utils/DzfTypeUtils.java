package com.dzf.zxkj.base.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.parser.JSONScanner;
import com.alibaba.fastjson.util.Base64;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DzfTypeUtils {

	public static String castToString(Object value) {
		if (value == null) {
			return null;
		}

		return value.toString();
	}

	public static Byte castToByte(Object value) {
		if (value == null) {
			return null;
		}

		if (value instanceof Number) {
			return ((Number) value).byteValue();
		}

		if (value instanceof String) {
			String strVal = (String) value;
			if (strVal.length() == 0) {
				return null;
			}

			if ("null".equals(strVal) || "NULL".equals(strVal)) {
				return null;
			}

			return Byte.parseByte(strVal);
		}

		throw new JSONException("can not cast to byte, value : " + value);
	}

	public static Character castToChar(Object value) {
		if (value == null) {
			return null;
		}

		if (value instanceof Character) {
			return (Character) value;
		}

		if (value instanceof String) {
			String strVal = (String) value;

			if (strVal.length() == 0) {
				return null;
			}

			if (strVal.length() != 1) {
				throw new JSONException("can not cast to byte, value : " + value);
			}

			return strVal.charAt(0);
		}

		throw new JSONException("can not cast to byte, value : " + value);
	}

	public static Short castToShort(Object value) {
		if (value == null) {
			return null;
		}

		if (value instanceof Number) {
			return ((Number) value).shortValue();
		}

		if (value instanceof String) {
			String strVal = (String) value;

			if (strVal.length() == 0) {
				return null;
			}

			if ("null".equals(strVal) || "NULL".equals(strVal)) {
				return null;
			}

			return Short.parseShort(strVal);
		}

		throw new JSONException("can not cast to short, value : " + value);
	}

	public static BigDecimal castToBigDecimal(Object value) {
		if (value == null) {
			return null;
		}

		if (value instanceof BigDecimal) {
			return (BigDecimal) value;
		}

		if (value instanceof BigInteger) {
			return new BigDecimal((BigInteger) value);
		}

		String strVal = value.toString();
		if (strVal.length() == 0) {
			return null;
		}

		return new BigDecimal(strVal);
	}

	public static BigInteger castToBigInteger(Object value) {
		if (value == null) {
			return null;
		}

		if (value instanceof BigInteger) {
			return (BigInteger) value;
		}

		if (value instanceof Float || value instanceof Double) {
			return BigInteger.valueOf(((Number) value).longValue());
		}

		String strVal = value.toString();
		if (strVal.length() == 0) {
			return null;
		}

		return new BigInteger(strVal);
	}

	public static Float castToFloat(Object value) {
		if (value == null) {
			return null;
		}

		if (value instanceof Number) {
			return ((Number) value).floatValue();
		}

		if (value instanceof String) {
			String strVal = value.toString();
			if (strVal.length() == 0) {
				return null;
			}

			if ("null".equals(strVal) || "NULL".equals(strVal)) {
				return null;
			}

			return Float.parseFloat(strVal);
		}

		throw new JSONException("can not cast to float, value : " + value);
	}

	public static Double castToDouble(Object value) {
		if (value == null) {
			return null;
		}

		if (value instanceof Number) {
			return ((Number) value).doubleValue();
		}

		if (value instanceof String) {
			String strVal = value.toString();
			if (strVal.length() == 0) {
				return null;
			}

			if ("null".equals(strVal) || "NULL".equals(strVal)) {
				return null;
			}

			return Double.parseDouble(strVal);
		}

		throw new JSONException("can not cast to double, value : " + value);
	}

	public static Date castToDate(Object value) {
		if (value == null) {
			return null;
		}

		if (value instanceof Calendar) {
			return ((Calendar) value).getTime();
		}

		if (value instanceof Date) {
			return (Date) value;
		}

		long longValue = -1;

		if (value instanceof Number) {
			longValue = ((Number) value).longValue();
			return new Date(longValue);
		}

		if (value instanceof String) {
			String strVal = (String) value;

			if (strVal.indexOf('-') != -1) {
				String format;
				if (strVal.length() == JSON.DEFFAULT_DATE_FORMAT.length()) {
					format = JSON.DEFFAULT_DATE_FORMAT;
				} else if (strVal.length() == 10) {
					format = "yyyy-MM-dd";
				} else if (strVal.length() == "yyyy-MM-dd HH:mm:ss".length()) {
					format = "yyyy-MM-dd HH:mm:ss";
				} else {
					format = "yyyy-MM-dd HH:mm:ss.SSS";
				}

				SimpleDateFormat dateFormat = new SimpleDateFormat(format);
				try {
					return (Date) dateFormat.parse(strVal);
				} catch (ParseException e) {
					throw new JSONException("can not cast to Date, value : " + strVal);
				}
			}

			if (strVal.length() == 0) {
				return null;
			}

			longValue = Long.parseLong(strVal);
		}

		if (longValue < 0) {
			throw new JSONException("can not cast to Date, value : " + value);
		}

		return new Date(longValue);
	}

	public static java.sql.Date castToSqlDate(Object value) {
		if (value == null) {
			return null;
		}

		if (value instanceof Calendar) {
			return new java.sql.Date(((Calendar) value).getTimeInMillis());
		}

		if (value instanceof java.sql.Date) {
			return (java.sql.Date) value;
		}

		if (value instanceof java.util.Date) {
			return new java.sql.Date(((java.util.Date) value).getTime());
		}

		long longValue = 0;

		if (value instanceof Number) {
			longValue = ((Number) value).longValue();
		}

		if (value instanceof String) {
			String strVal = (String) value;
			if (strVal.length() == 0) {
				return null;
			}

			longValue = Long.parseLong(strVal);
		}

		if (longValue <= 0) {
			throw new JSONException("can not cast to Date, value : " + value);
		}

		return new java.sql.Date(longValue);
	}

	public static java.sql.Timestamp castToTimestamp(Object value) {
		if (value == null) {
			return null;
		}

		if (value instanceof Calendar) {
			return new java.sql.Timestamp(((Calendar) value).getTimeInMillis());
		}

		if (value instanceof java.sql.Timestamp) {
			return (java.sql.Timestamp) value;
		}

		if (value instanceof java.util.Date) {
			return new java.sql.Timestamp(((java.util.Date) value).getTime());
		}

		long longValue = 0;

		if (value instanceof Number) {
			longValue = ((Number) value).longValue();
		}

		if (value instanceof String) {
			String strVal = (String) value;
			if (strVal.length() == 0) {
				return null;
			}

			longValue = Long.parseLong(strVal);
		}

		if (longValue <= 0) {
			throw new JSONException("can not cast to Date, value : " + value);
		}

		return new java.sql.Timestamp(longValue);
	}

	public static Long castToLong(Object value) {
		if (value == null) {
			return null;
		}

		if (value instanceof Number) {
			return ((Number) value).longValue();
		}

		if (value instanceof String) {
			String strVal = (String) value;
			if (strVal.length() == 0) {
				return null;
			}

			if ("null".equals(strVal) || "NULL".equals(strVal)) {
				return null;
			}

			try {
				return Long.parseLong(strVal);
			} catch (NumberFormatException ex) {
				//
			}

			JSONScanner dateParser = new JSONScanner(strVal);
			Calendar calendar = null;
			if (dateParser.scanISO8601DateIfMatch(false)) {
				calendar = dateParser.getCalendar();
			}
			dateParser.close();

			if (calendar != null) {
				return calendar.getTimeInMillis();
			}
		}

		throw new JSONException("can not cast to long, value : " + value);
	}

	public static Integer castToInt(Object value) {
		if (value == null) {
			return null;
		}

		if (value instanceof Integer) {
			return (Integer) value;
		}

		if (value instanceof Number) {
			return ((Number) value).intValue();
		}

		if (value instanceof String) {
			String strVal = (String) value;

			if (strVal.length() == 0) {
				return null;
			}

			if ("null".equals(strVal)) {
				return null;
			}

			if ("null".equals(strVal) || "NULL".equals(strVal)) {
				return null;
			}

			return Integer.parseInt(strVal);
		}

		throw new JSONException("can not cast to int, value : " + value);
	}

	public static byte[] castToBytes(Object value) {
		if (value instanceof byte[]) {
			return (byte[]) value;
		}

		if (value instanceof String) {
			return Base64.decodeFast((String) value);
		}
		throw new JSONException("can not cast to int, value : " + value);
	}

	public static Boolean castToBoolean(Object value) {
		if (value == null) {
			return Boolean.FALSE;
		}

		if (value instanceof Boolean) {
			return (Boolean) value;
		}

		if (value instanceof Number) {
			return ((Number) value).intValue() == 1;
		}

		if (value instanceof String) {
			String strVal = (String) value;

			if (strVal.length() == 0) {
				return Boolean.FALSE;
			}

			if ("true".equalsIgnoreCase(strVal)) {
				return Boolean.TRUE;
			}
			if ("false".equalsIgnoreCase(strVal)) {
				return Boolean.FALSE;
			}

			if ("1".equals(strVal)) {
				return Boolean.TRUE;
			}

			if ("0".equals(strVal)) {
				return Boolean.FALSE;
			}

			if ("null".equals(strVal) || "NULL".equals(strVal)) {
				return Boolean.FALSE;
			}
		}

		throw new JSONException("can not cast to boolean, value : " + value);
	}
}


