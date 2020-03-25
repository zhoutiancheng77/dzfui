package com.dzf.zxkj.base.utils;

import com.alibaba.fastjson.util.TypeUtils;
import com.dzf.zxkj.common.lang.*;
import com.dzf.zxkj.common.utils.JavaType;

import java.math.BigDecimal;

/**
 * 值转换工具类，将某个未知类型的object转换为特定类型的值。
 */
public class ValueUtils {
	private ValueUtils() {
		// 缺省构造方法
	}

	/**
	 * 根据元数据属性信息中定义的数据类型转换值的类型
	 * 
	 * @param value
	 *            要转换的值
	 * @param 'attribute'
	 *            元数据属性信息
	 * @return 元数据属性信息所定义数据类型的值
	 */
	public static Object convert(Object value,JavaType type) {
		Object ret = value;
		if (type == JavaType.DZFDouble) {
			ret = ValueUtils.getDZFDouble(value);
		} else if (type == JavaType.String) {
			ret = ValueUtils.getString(value);
		} else if (type == JavaType.Integer) {
			ret = ValueUtils.getInteger(value);
		} else if (type == JavaType.DZFBoolean) {
			ret = ValueUtils.getDZFBoolean(value);
		} else if (type == JavaType.DZFDate) {
			ret = ValueUtils.getDZFDate(value);
		} else if (type == JavaType.DZFDateTime) {
			ret = ValueUtils.getDZFDateTime(value);
		} else if (type == JavaType.DZFTime) {
			ret = ValueUtils.getDZFTime(value);
		} else if (type == JavaType.BigDecimal) {
			ret = ValueUtils.getDZFDouble(value);
		} else if (type == JavaType.Object) {
			ret = value;
		} else {
			String message = "不支持此种业务，请检查"; /*-=notranslate=-*/
			throw new IllegalArgumentException(message);
		}
		return ret;
	}

	/**
	 * 将值转换为BigDecimal类型
	 * 
	 * @param value
	 *            要转换的值
	 * @return 类型为BigDecimal的值
	 */
	public static BigDecimal getBigDecimal(Object value) {
		BigDecimal retValue = null;
		if (value == null) {
			return null;
		}
		if (value instanceof BigDecimal) {
			retValue = (BigDecimal) value;
		} else if (value instanceof DZFDouble) {
			retValue = ((DZFDouble) value).toBigDecimal();
		} else {
			String str = value.toString();
			try {
				retValue = new BigDecimal(str);
			} catch (NumberFormatException ex) {
				ValueUtils.throwIllegalArgumentException(value, ex);
			}
		}
		return retValue;
	}

	/**
	 * 将值转换为boolean类型
	 * 
	 * @param value
	 *            要转换的值
	 * @return 类型为boolean的值
	 */
	public static boolean getBoolean(Object value) {
		DZFBoolean temp = ValueUtils.getDZFBoolean(value);
		boolean flag = true;
		if (temp != null) {
			flag = temp.booleanValue();
		}
		return flag;
	}

	/**
	 * 值转换工具类的工厂方法。
	 * 
	 * @return 返回值转化工具类的实例
	 * @deprecated 用具体转换值的static方法替代
	 */
	@Deprecated
	public static ValueUtils getInstance() {
		return new ValueUtils();
	}

	/**
	 * 将值转换为int类型
	 * 
	 * @param value
	 *            要转换的值
	 * @return 类型为int的值
	 */
	public static int getInt(Object value) {
		return ValueUtils.getInt(value, 0);
	}

	/**
	 * 将值转换为int类型，如果传出的值为null，则返回默认值
	 * 
	 * @param value
	 *            要转换的值
	 * @param defaultValue
	 *            默认值
	 * @return 类型为int的值
	 */
	public static int getInt(Object value, int defaultValue) {
		Integer temp = ValueUtils.getInteger(value);
		int ret = defaultValue;
		if (temp != null) {
			ret = temp.intValue();
		}
		return ret;
	}

	/**
	 * 将值转换为Integer类型
	 * 
	 * @param value
	 *            要转换的值
	 * @return 类型为Integer的值
	 */
	public static Integer getInteger(Object value) {
		Integer retValue = null;
		if (value == null) {
			return null;
		}
		if (value instanceof Integer) {
			retValue = (Integer) value;
		} else {
			String str = value.toString();
			try {
				retValue = Integer.valueOf(str);
			} catch (NumberFormatException ex) {
				ValueUtils.throwIllegalArgumentException(value, ex);
			}
		}
		return retValue;
	}

	/**
	 * 将值转换为String类型
	 * 
	 * @param value
	 *            要转换的值
	 * @return 类型为String的值
	 */
	public static String getString(Object value) {
		String retValue = null;
		if (value == null) {
			return null;
		}

		retValue = value.toString().trim();
		return retValue;
	}

	/**
	 * 将值转换为DZFBoolean类型
	 * 
	 * @param value
	 *            要转换的值
	 * @return 类型为DZFBoolean的值
	 */
	public static DZFBoolean getDZFBoolean(Object value) {
		DZFBoolean retValue = null;
		if (value == null) {
			return DZFBoolean.FALSE;
		}
		if (value instanceof DZFBoolean) {
			retValue = (DZFBoolean) value;
			retValue = retValue.booleanValue() ? DZFBoolean.TRUE : DZFBoolean.FALSE;
		} else {
			retValue = DZFBoolean.valueOf(value.toString().trim());
			retValue = DZFBoolean.TRUE.equals(retValue) ? DZFBoolean.TRUE : DZFBoolean.FALSE;
		}
		return retValue;
	}

	/**
	 * 将值转换为DZFDate类型
	 * 
	 * @param value
	 *            要转换的值
	 * @return 类型为DZFDate的值
	 */
	public static DZFDate getDZFDate(Object value) {
		DZFDate retValue = null;
		if (value == null) {
			return null;
		}

		if (value instanceof DZFDate) {
			retValue = (DZFDate) value;
		} else {
			retValue = new DZFDate(TypeUtils.castToDate(value));
		}
		return retValue;
	}

	/**
	 * 将值转换为DZFDateTime类型
	 * 
	 * @param value
	 *            要转换的值
	 * @return 类型为DZFDateTime的值
	 */
	public static DZFDateTime getDZFDateTime(Object value) {
		DZFDateTime retValue = null;
		if (value == null) {
			return null;
		}
		if (value instanceof DZFDateTime) {
			retValue = (DZFDateTime) value;
		} else {
			retValue = new DZFDateTime(value.toString());
		}
		return retValue;
	}

	/**
	 * 将值转换为DZFDouble类型
	 * 
	 * @param value
	 *            要转换的值
	 * @return 类型为DZFDouble的值
	 */
	public static DZFDouble getDZFDouble(Object value) {
		DZFDouble ret = null;
		if (value == null) {
			return null;
		}

		if (value instanceof DZFDouble) {
			ret = (DZFDouble) value;
		} else if (value instanceof BigDecimal) {
			BigDecimal temp = (BigDecimal) value;
			ret = new DZFDouble(temp);
		} else if (value instanceof Number) {
			Number number = (Number) value;
			double temp = number.doubleValue();
			ret = new DZFDouble(temp);
		} else {
			String str = value.toString();
			try {
				ret = new DZFDouble(str);
			} catch (Exception ex) {
				ValueUtils.throwIllegalArgumentException(value, ex);
			}
		}
		return ret;
	}

	/**
	 * 将值转换为DZFTime类型
	 * 
	 * @param value
	 *            要转换的值
	 * @return 类型为DZFTime的值
	 */
	public static DZFTime getDZFTime(Object value) {
		DZFTime retValue = null;
		if (value == null) {
			return null;
		}
		if (value instanceof DZFTime) {
			retValue = (DZFTime) value;
		} else {
			retValue = new DZFTime(value.toString());
		}
		return retValue;
	}

	private static void throwIllegalArgumentException(Object value, Exception ex) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("the value is:");
		buffer.append(value);
		buffer.append(" the error message is :");
		buffer.append(ex.getMessage());
		throw new IllegalArgumentException(buffer.toString());
	}

}
