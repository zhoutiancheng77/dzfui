package com.dzf.zxkj.common.utils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Array类型工具类
 * 
 */
public class DZFArrayUtil {

	/**
	 * 判断数组是否为空
	 * 
	 * @param objs
	 * @return
	 */
	public static boolean isEmpty(Object[] objs) {
		return null == objs || objs.length == 0;
	}

	/**
	 * 判断数组是否为非空
	 * 
	 * @param objs
	 * @return
	 */
	public static boolean isNotEmpty(Object[] objs) {
		return null != objs && objs.length > 0;
	}

	/**
	 * 判断数组是否包含空元素
	 * 
	 * @param objs
	 * @return
	 */
	public static boolean isContainNull(Object[] objs) {
		if (DZFArrayUtil.isEmpty(objs)) {
			return true;
		}
		for (Object obj : objs) {
			if (null == obj) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断数组是否不包含空元素
	 * 
	 * @param objs
	 * @return
	 */
	public static boolean isNotContainNull(Object[] objs) {
		if (DZFArrayUtil.isEmpty(objs)) {
			return false;
		}
		for (Object obj : objs) {
			if (null == obj) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 判断两个数组长度是否相等
	 * 
	 * @param objs1
	 * @param objs2
	 * @return
	 */
	public static boolean isLengthEqual(Object[] objs1, Object[] objs2) {

		// 全空
		if (DZFArrayUtil.isEmpty(objs1) && DZFArrayUtil.isEmpty(objs2)) {
			return true;
		}
		// 其一为空，另外一个非空
		if (DZFArrayUtil.isEmpty(objs1) || DZFArrayUtil.isNotEmpty(objs2)) {
			return false;
		}
		if (DZFArrayUtil.isEmpty(objs2) || DZFArrayUtil.isNotEmpty(objs1)) {
			return false;
		}
		// 均非空
		return objs1.length == objs2.length;
	}

	/**
	 * 合并多个数组
	 * 
	 * @param <T>
	 * @param objs
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] combineArray(T[]... objs) {
		if (null == objs) {
			return null;
		}
		int length = 0;
		int count = 0;
		T[] result = null;
		for (T[] array : objs) {
			if (DZFArrayUtil.isEmpty(array)) {
				continue;
			}
			if (null == result) {
				result = array;
			}
			count++;
			length += array.length;
		}
		if (length == 0 || count == 1 || null == result) {
			return result;
		}
		result = (T[]) Array.newInstance(result[0].getClass(), length);
		int destPos = 0;
		for (Object[] array : objs) {
			if (DZFArrayUtil.isEmpty(array)) {
				continue;
			}
			System.arraycopy(array, 0, result, destPos, array.length);
			destPos += array.length;
		}
		return result;
	}

	/**
	 * 去掉数组中的空元素
	 * <p>
	 * <b>调用方请注意:该方法可能返回null</b>
	 * </p> 
	 * @param <T>
	 * @param objs
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] removeNull(T[] objs, Class<T> clazz) {
		if (DZFArrayUtil.isEmpty(objs)) {
			return null;
		}
		List<T> tempList = new ArrayList<T>();
		for (T temp : tempList) {
			if (null == temp) {
				continue;
			}
			tempList.add(temp);
		}
		T[] result = (T[]) Array.newInstance(clazz, tempList.size());
		tempList.toArray(result);
		return result;
	}

	/**
	 * Object转换成数组T[]
	 * 
	 * @param <T>
	 * @param obj
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] toArray(T obj) {
		T[] array = null;
		if (obj.getClass().isArray()) {
			Object[] objs = (Object[]) obj;
			array = (T[]) Array.newInstance(objs[0].getClass(), objs.length);
			for (int i = 0; i < objs.length; i++) {
				array[i] = (T) objs[i];
			}
		} else {
			array = (T[]) Array.newInstance(obj.getClass(), 1);
			array[0] = obj;
		}
		return array;
	}

	/**
	 * Object[]转换成数组T[]
	 * <p>
	 * <b>调用方请注意:该方法可能返回null</b>
	 * </p>
	 * 
	 * @param <T>
	 * @param objs
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] toArray(Object[] objs) {
		if (DZFArrayUtil.isEmpty(objs)) {
			return null;
		}
		T[] result = (T[]) Array.newInstance(objs[0].getClass(), objs.length);
		if (result.getClass().isAssignableFrom(objs.getClass())) {
			return (T[]) objs;
		}
		System.arraycopy(objs, 0, result, 0, objs.length);
		return result;
	}

	/**
	 * Collection转换成数组T[]
	 * <p>
	 * <b>调用方请注意:该方法可能返回null</b>
	 * </p>
	 * 
	 * @param <T>
	 * @param c
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] toArray(Collection<T> c) {
		if (DZFCollectionUtil.isEmpty(c)) {
			return null;
		}

		T t = c.iterator().next();
		T[] result = (T[]) Array.newInstance(t.getClass(), c.size());
		return c.toArray(result);
	}

	/**
	 * Collection转换成数组T[]
	 * 
	 * @param <T>
	 * @param c
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] toArray(Collection<T> c, Class<T> clazz) {
		if (DZFCollectionUtil.isEmpty(c)) {
			return null;
		}
		T[] result = (T[]) Array.newInstance(clazz, c.size());
		return c.toArray(result);
	}

	/**
	 * 方法功能描述：单对象转换为数组，需传入目标对象类型，如果本身不是数组则返回size=1的数组
	 * 
	 * @param <T>
	 *            泛型参数决定返回数组的类型
	 * @param dataClass
	 *            传入泛型T的类型参数，决定返回数组的类型
	 * @param obj
	 *            待转换为数组的Object型对象参数
	 * @return 转换后的泛型数组
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] toArray(Class<T> dataClass, Object obj) {
		T[] array = null;
		if (obj.getClass().isArray()) {
			Object[] objs = (Object[]) obj;
			array = (T[]) Array.newInstance(dataClass, objs.length);
			for (int i = 0; i < objs.length; i++) {
				array[i] = (T) objs[i];
			}
		} else {
			array = (T[]) Array.newInstance(obj.getClass(), 1);
			array[0] = (T) obj;
		}
		return array;
	}

	/**
	 * 取得一个空数组
	 * 
	 * @param <T>
	 * @param dataclass
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] getArrayInstance(Class<T> dataclass) {
		return (T[]) Array.newInstance(dataclass, 0);
	}

}
