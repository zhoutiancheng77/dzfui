package com.dzf.zxkj.common.utils;


import com.dzf.zxkj.common.lang.DZFDouble;

import java.lang.reflect.Array;
import java.util.*;


/**
 * 数组相关的工具类
 * <p>
 * @author  zengj
 * @version 1.0 2010-11-11
 * @since   NC5.7
 */
public class ArrayUtil {

	public static final String NUMBER = "NUMBER";
	public static final String STRING = "STRING";

	/**
	 * 将一个数组添加到一个List中
	 * 
	 * @param <T>
	 * @param <S>
	 * @param lst
	 * @param arr
	 * @return
	 * @author wangxy
	 * @since NC6.0
	 */
	public static <T, S extends T> List<T> addArrayToList(List<T> lst, S[] arr) {
		if (lst != null && arr != null) {
			for (int i = 0; i < arr.length; i++) {
				lst.add(arr[i]);
			}
		}
		return lst;
	}

	/**
	 * 将一个数组添加到一个List中
	 * 
	 * @param <T>
	 * @param <S>
	 * @param lsta
	 * @param lstb
	 * @return
	 * @author wangxy
	 * @since NC6.0
	 */
	public static <T, S extends T> List<T> addListToList(List<T> lsta,
			List<T> lstb) {
		if (lsta != null && lstb != null) {
			for (int i = 0; i < lstb.size(); i++) {
				lsta.add(lstb.get(i));
			}
		}
		return lsta;
	}

	/**
	 * 将一个新元素加入数组的末尾。
	 * 
	 * @param <T>
	 * @param <S>
	 * @param oldData
	 * @param o
	 * @return
	 * @author wangxy
	 * @since NC6.0
	 */
	public static <T, S extends T> T[] arrayAdd(T[] arr, S obj) {
		T[] newData;
		newData = ArrayUtil.arrayCapacity(arr, 1);
		newData[arr.length] = obj;
		return newData;
	}

	/**
	 * 将新元素加入数组的指定位置。
	 * 
	 * @param <T>
	 * @param <S>
	 * @param arr
	 * @param obj
	 * @param index
	 * @return
	 * @author wangxy
	 * @since NC6.0
	 */
	public static <T, S extends T> T[] arrayAdd(T[] arr, S obj, int index) {
		int size = arr.length;
		if (index > size || index < 0)
			throw new IndexOutOfBoundsException("Index: "
					+ index
					+ ", Size: "
					+ size);

		T[] newData;
		newData = ArrayUtil.arrayCapacity(arr, 1);
		System.arraycopy(newData, index, newData, index + 1, size - index);
		newData[index] = obj;
		return newData;
	}

	/**
	 * 数组容量扩大。 创建日期：(2002-5-30 10:48:40)
	 * 
	 * @param <T>
	 * @param arr
	 * @param increase
	 * @return
	 * @since NC6.0
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] arrayCapacity(T[] arr, int increase) {
		int oldsize = arr.length;
		int size = oldsize + increase;
		T[] newArr = (T[]) Array.newInstance(arr.getClass().getComponentType(), size);

		System.arraycopy(arr, 0, newArr, 0, oldsize);
		return newArr;
	}

	/**
	 * <p>
	 * Searches the specified array for the specified object using the binary
	 * search algorithm.
	 * <p>
	 * 作者：hzguo <br>
	 * 日期：2006-7-13
	 * 
	 * @param objs
	 * @param obj
	 * @param style
	 * @return
	 */
	public static int binarySearch(Object[] objs, Object obj, String style) {
		int index = -1;
		Comparator<Object> strComparator = new Comparator<Object>() {
			public int compare(Object o1, Object o2) {
				return o1.toString().compareTo(o2.toString());
			}
		};
		Comparator<Object> ufdoubleComparator = new Comparator<Object>() {

			public int compare(Object o1, Object o2) {
				DZFDouble ufdouble1 = new DZFDouble(o1.toString());
				DZFDouble ufdouble2 = new DZFDouble(o2.toString());
				return ufdouble1.compareTo(ufdouble2);
			}
		};
		if (style.equals(ArrayUtil.STRING))
			index = Arrays.binarySearch(objs, obj, strComparator);
		else if (style.equals(ArrayUtil.NUMBER))
			index = Arrays.binarySearch(objs, obj, ufdoubleComparator);
		return index;
	}

	/**
	 * <p>
	 * 获取数组的长度，如果为空，则长度为0。
	 * 
	 * @param obj
	 * @return
	 * @since NC6.0
	 */
	public static int getArrayLength(Object[] obj) {
		return obj == null ? 0 : obj.length;
	}

	/**
	 * <p>
	 * 判断字符串数组内是否有空值。
	 * <p>
	 * 作者：qbh <br>
	 * 日期：2006-9-12
	 * 
	 * @param ss
	 * @return
	 */
	public static boolean hasNull(String[] ss) {
		if (ss == null || ss.length == 0) {
			return true;
		}

		for (int i = 0; i < ss.length; i++) {
			if (StringUtil.isEmpty(ss[i])) {
				return true;
			}
		}

		return false;
	}

	/**
	 * <p>
	 * 判断是否是空数组。
	 * <p>
	 * 作者：qbh <br>
	 * 日期：2006-1-5
	 * 
	 * @param ss
	 * @return
	 */
	public static boolean isNull(Object[] array) {
		if (array == null || array.length == 0) {
			return true;
		}

		for (int i = 0; i < array.length; i++) {
			if (array[i] != null) {
				return false;
			}
		}

		return true;
	}

	/**
	 * 合并两个数组，将数组2合并到数组1的后面
	 * 
	 * @param <T>
	 * @param <S>
	 * @param array1
	 * @param array2
	 * @return
	 * @author wangxy
	 * @since NC6.0
	 */
	public static <T, S extends T> T[] mergeArray(T[] array1, S[] array2) {
		if (array1 == null && array2 == null) {
			return null;
		}
		if (isNull(array1)) {
			return array2;
		}
		if (isNull(array2)) {
			return array1;
		}

		// int length = array1.length + array2.length;
		// T[] array = (T[])
		// Array.newInstance(array1.getClass().getComponentType(), length);
		//
		// System.arraycopy(array1, 0, array, 0, array1.length);

		T[] array = ArrayUtil.arrayCapacity(array1, array2.length);
		System.arraycopy(array2, 0, array, array1.length, array2.length);

		return array;
	}

	/**
	 * 将一个数组压入堆栈。
	 * 
	 * @param stk
	 * @param arr
	 * @param isOrder
	 *            <li>true 表示顺序 -- 按数组下标，最小的在栈顶，最大的在栈底 <li>false 表示倒序 --
	 *            最大载栈顶，最小在栈底
	 * @return
	 * @since NC3.5
	 * @see （关联类）
	 */
	public static <T, S extends T> Stack<T> pushArrrayToStack(Stack<T> stk,
			S[] arr, boolean isOrder) {
		if (isOrder) {
			for (int i = arr.length - 1; i >= 0; i--) {
				stk.push(arr[i]);
			}
		} else {
			for (int i = 0; i < arr.length; i++) {
				stk.push(arr[i]);
			}
		}

		return stk;
	}

	/**
	 * <p>
	 * 从超数组中除去子数组，子数组必须位于超数组的前头。
	 * <p>
	 * 作者：qbh <br>
	 * 日期：2006-2-27
	 * 
	 * @param superArray
	 * @param subArray
	 * @return
	 */
	// 请使用nc.vo.tmpub.util.ArrayUtil.shrinkArray(T[] arr, int n)方法替换

	// public static <T, S extends T> T[] removeSubArray(T[] superArray,
	// S[] subArray) {
	// if (isNull(superArray) || isNull(subArray)) {
	// return superArray;
	// }
	// Class superType = superArray.getClass().getComponentType();
	// Class subType = subArray.getClass().getComponentType();
	// if (superType != subType) {
	// return null;
	// }
	//
	// int length = superArray.length - subArray.length;
	// T[] array = (T[]) Array.newInstance(superType, length);
	//
	// System.arraycopy(superArray, subArray.length, array, 0, length);
	//
	// return array;
	// }

	/**
	 * @param obj
	 * @param style
	 */
	public static void sort(Object[] obj, String style) {
		Comparator<Object> strComparator = new Comparator<Object>() {
			public int compare(Object o1, Object o2) {
				return o1.toString().compareTo(o2.toString());
			}
		};
		Comparator<Object> ufdoubleComparator = new Comparator<Object>() {

			public int compare(Object o1, Object o2) {
				DZFDouble ufdouble1 = new DZFDouble(o1.toString());
				DZFDouble ufdouble2 = new DZFDouble(o2.toString());
				return ufdouble1.compareTo(ufdouble2);
			}
		};
		if (style.equals(ArrayUtil.STRING))
			Arrays.sort(obj, strComparator);
		else if (style.equals(ArrayUtil.NUMBER))
			Arrays.sort(obj, ufdoubleComparator);
	}

	/**
	 * 向数组中加入数组。 创建日期：(2002-5-30 10:44:11)
	 * 
	 * @return java.lang.Object[]
	 * @param oldData
	 *            java.lang.Object[]
	 * @param newObj
	 *            java.lang.Object
	 */
	// 该方法已删除，请使用nc.vo.tmpub.util.ArrayUtil.mergeArray(T[] array1, S[]
	// array2)方法替换

	// public static <T, S extends T> T[] arrayAdd(T[] oldData, T[] addData) {
	// T[] newData;
	// if (isNull(oldData)) {
	// newData = addData;
	// return newData;
	// }
	// newData = arrayCapacity(oldData, addData.length);
	// for (int i = 0; i < addData.length; i++) {
	// newData[oldData.length + i] = addData[i];
	// }
	// return newData;
	// }

	/**
	 * 数组收缩，取出空值元素
	 * 
	 * @param <T>
	 * @param arr
	 * @return
	 * @author wangxy
	 * @since NC6.0
	 */
	public static <T> T[] shrinkArray(T[] arr) {
		if (arr == null) {
			return null;
		}
		ArrayList<T> lst = new ArrayList<T>();
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] != null) {
				lst.add(arr[i]);
			}
		}

		return lst.toArray(arr);
	}

	/**
	 * 数组收缩，去除数组的前n个元素
	 * 
	 * @param <T>
	 * @param arr
	 * @param n
	 * @return
	 * @author wangxy
	 * @since NC6.0
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] shrinkArray(T[] arr, int n) {
		if (arr == null) {
			return null;
		}
		if (n > arr.length) {
			throw new ArrayIndexOutOfBoundsException(" n > arr.length");
		}
		T[] array = (T[]) Array.newInstance(arr.getClass().getComponentType(), arr.length
				- n);
		System.arraycopy(arr, n, array, 0, arr.length - n);
		return array;
	}

	/**
	 * ArrayUtil的构造方法
	 */
	private ArrayUtil() {

	}

}
