package com.dzf.zxkj.base.utils;

import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.DZFArrayUtil;

/**
 * 数值类型工具类
 * 
 */
public class DZFNumberUtil {

	/**
	 * 判断数值是否为空或者零
	 * 
	 * @param d
	 * @return
	 */
	public static boolean isNullOrZero(DZFDouble d) {
		return null == d || d.compareTo(DZFDouble.ZERO_DBL) == 0;
	}

	/**
	 * 判断数值是否为非空或者非零
	 * 
	 * @param d
	 * @return
	 */
	public static boolean isNotNullAndNotZero(DZFDouble d) {
		return null != d && d.compareTo(DZFDouble.ZERO_DBL) != 0;
	}

	/**
	 * 判断两个数值是否相等
	 * 
	 * @param d1
	 * @param d2
	 * @return
	 */
	public static boolean isEqual(DZFDouble d1, DZFDouble d2) {
		if (null == d1 && null == d2) {
			return true;
		}
		if (null != d1 && null != d2) {
			return d1.equals(d2);
		}
		return false;
	}

	/**
	 * 数值大于判断
	 * 
	 * @param d1
	 * @param d2
	 * @return
	 */
	public static boolean isGt(DZFDouble d1, DZFDouble d2) {
		DZFDouble d11 = null == d1 ? DZFDouble.ZERO_DBL : d1;
		DZFDouble d22 = null == d2 ? DZFDouble.ZERO_DBL : d2;
		return d11.compareTo(d22) > 0;
	}

	/**
	 * 数值大于等于判断
	 * 
	 * @param d1
	 * @param d2
	 * @return
	 */
	public static boolean isGtEqual(DZFDouble d1, DZFDouble d2) {
		DZFDouble d11 = null == d1 ? DZFDouble.ZERO_DBL : d1;
		DZFDouble d22 = null == d2 ? DZFDouble.ZERO_DBL : d2;
		return d11.compareTo(d22) >= 0;
	}

	/**
	 * 数值小于判断
	 * 
	 * @param d1
	 * @param d2
	 * @return
	 */
	public static boolean isLs(DZFDouble d1, DZFDouble d2) {
		DZFDouble d11 = null == d1 ? DZFDouble.ZERO_DBL : d1;
		DZFDouble d22 = null == d2 ? DZFDouble.ZERO_DBL : d2;
		return d11.compareTo(d22) < 0;
	}

	/**
	 * 数值小于等于判断
	 * 
	 * @param d1
	 * @param d2
	 * @return
	 */
	public static boolean isLsEqual(DZFDouble d1, DZFDouble d2) {
		DZFDouble d11 = null == d1 ? DZFDouble.ZERO_DBL : d1;
		DZFDouble d22 = null == d2 ? DZFDouble.ZERO_DBL : d2;
		return d11.compareTo(d22) <= 0;
	}

	/**
	 * 判断数值是否等于零
	 * 
	 * @param d
	 * @return
	 */
	public static boolean isEqualZero(DZFDouble d) {
		return null != d && d.compareTo(DZFDouble.ZERO_DBL) == 0;
	}

	/**
	 * 判断数值是否大于零
	 * 
	 * @param d
	 * @return
	 */
	public static boolean isGtZero(DZFDouble d) {
		return null != d && d.compareTo(DZFDouble.ZERO_DBL) > 0;
	}

	/**
	 * 判断数值是否大于等于零
	 * 
	 * @param d
	 * @return
	 */
	public static boolean isGtEqualZero(DZFDouble d) {
		return null != d && d.compareTo(DZFDouble.ZERO_DBL) >= 0;
	}

	/**
	 * 判断数值是否小于零
	 * 
	 * @param d
	 * @return
	 */
	public static boolean isLsZero(DZFDouble d) {
		return null != d && d.compareTo(DZFDouble.ZERO_DBL) < 0;
	}

	/**
	 * 判断数值是否小于等于零
	 * 
	 * @param d
	 * @return
	 */
	public static boolean isLsEqualZero(DZFDouble d) {
		return null != d && d.compareTo(DZFDouble.ZERO_DBL) <= 0;
	}

	/**
	 * 两个数值取小
	 * 
	 * @param d1
	 * @param d2
	 * @return
	 */
	public static DZFDouble min(DZFDouble d1, DZFDouble d2) {
		if (null == d1) {
			return d1;
		} else if (null == d2) {
			return d2;
		}
		return d1.compareTo(d2) > 0 ? d2 : d1;
	}

	/**
	 * 两个数值取大
	 * 
	 * @param d1
	 * @param d2
	 * @return
	 */
	public static DZFDouble max(DZFDouble d1, DZFDouble d2) {
		if (null == d1) {
			return d2;
		} else if (null == d2) {
			return d1;
		}
		return d1.compareTo(d2) > 0 ? d1 : d2;
	}

	/**
	 * 数值相加
	 * 
	 * @param values
	 * @return
	 */
	public static DZFDouble add(DZFDouble... values) {
		if (DZFArrayUtil.isEmpty(values)) {
			return null;
		}
		DZFDouble result = DZFDouble.ZERO_DBL;
		for (DZFDouble value : values) {
			if (null != value) {
				result = result.add(value);
			}
		}
		return result;
	}

	/**
	 * 数值相减
	 * 
	 * @param lvalue
	 * @param rvalues
	 * @return
	 */
	public static DZFDouble sub(DZFDouble lvalue, DZFDouble... rvalues) {
		if (DZFArrayUtil.isEmpty(rvalues)) {
			return lvalue;
		}
		DZFDouble result = lvalue;
		if (null == lvalue) {
			result = DZFDouble.ZERO_DBL;
		}
		for (DZFDouble rvalue : rvalues) {
			if (null != rvalue) {
				result = result.sub(rvalue);
			}
		}
		return result;
	}

	/**
	 * 绝对值相减
	 * 
	 * @param d1
	 * @param d2
	 * @return
	 */
	public static DZFDouble absoluteSub(DZFDouble d1, DZFDouble d2) {
		if (null == d1 && null == d2) {
			return null;
		}
		DZFDouble d11 = null == d1 ? DZFDouble.ZERO_DBL : d1;
		DZFDouble d22 = null == d2 ? DZFDouble.ZERO_DBL : d2;
		DZFDouble temp = d11.sub(d22);
		return temp.compareTo(DZFDouble.ZERO_DBL) >= 0 ? temp : DZFDouble.ZERO_DBL.sub(temp);
	}

	/**
	 * 数值相乘
	 * 
	 * @param values
	 * @return
	 */
	public static DZFDouble multiply(DZFDouble... values) {
		if (DZFArrayUtil.isEmpty(values)) {
			return null;
		}
		DZFDouble result = null;
		for (DZFDouble value : values) {
			if (null == value) {
				return DZFDouble.ZERO_DBL;
			}
			if (null == result) {
				result = value;
				continue;
			}
			result = result.multiply(value);
		}
		return result;
	}

	/**
	 * 数值相除
	 * 
	 * @param lvalue
	 * @param rvalues
	 * @return
	 */
	public static DZFDouble div(DZFDouble lvalue, DZFDouble... rvalues) {
		if (null == lvalue || DZFArrayUtil.isEmpty(rvalues)) {
			return null;
		}
		DZFDouble result = lvalue;
		for (DZFDouble rvalue : rvalues) {
			if (null == rvalue || rvalue.compareTo(DZFDouble.ZERO_DBL) == 0) {
				ExceptionUtils.wrappBusinessException("除数不能为零");
			}
			result = result.div(rvalue);
		}
		return result;
	}

	/**
	 * 数值取绝对值
	 * 
	 * @param d
	 * @return
	 */
	public static DZFDouble toAbsValue(DZFDouble d) {
		if (null == d) {
			return null;
		}
		return d.abs();
	}

	/**
	 * 数值取精度
	 * 
	 * @param d
	 * @param digit
	 * @return
	 */
	public static DZFDouble toDigitValue(DZFDouble d, int digit) {
		if (null == d) {
			return null;
		}
		return d.setScale(0 - digit, DZFDouble.ROUND_HALF_UP);
	}

	/**
	 * 数值取相反数
	 * 
	 * @param d
	 * @return
	 */
	public static DZFDouble toNegValue(DZFDouble d) {
		if (null == d) {
			return null;
		}
		return DZFDouble.ZERO_DBL.sub(d);
	}

	/**
	 * 数值取非空值
	 * 
	 * @param d
	 * @return
	 */
	public static DZFDouble toNotNullValue(DZFDouble d) {
		return null == d ? DZFDouble.ZERO_DBL : d;
	}

	/**
	 * 数值向上取整
	 * 
	 * @param d
	 * @return
	 */
	public static DZFDouble toUpWardRoundNumber(DZFDouble d) {
		if (DZFNumberUtil.isNullOrZero(d)) {
			return d;
		}
		return new DZFDouble(Math.ceil(d.doubleValue()));
	}

	/**
	 * 数值向下取整
	 * 
	 * @param d
	 * @return
	 */
	public static DZFDouble toDownWardRoundNumber(DZFDouble d) {
		if (DZFNumberUtil.isNullOrZero(d)) {
			return d;
		}
		return new DZFDouble(Math.floor(d.doubleValue()));
	}

}
