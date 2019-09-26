package com.dzf.zxkj.common.utils;

import java.util.Collection;

/**
 * Collection类型工具类
 * 
 */
public class DZFCollectionUtil {
	
	
    /**
     * 判断集合是否为空
     * 
     * @param c
     * @return
     */
    public static boolean isEmpty(Collection<?> c) {
        return null == c || c.size() == 0;
    }

    /**
     * 判断集合是否为非空
     * 
     * @param c
     * @return
     */
    public static boolean isNotEmpty(Collection<?> c) {
        return null != c && c.size() > 0;
    }

    /**
     * 判断集合是否包含空元素
     * 
     * @param c
     * @return
     */
    public static boolean isContainNull(Collection<?> c) {
        if (DZFCollectionUtil.isEmpty(c)) {
            return true;
        }
        for (Object obj : c) {
            if (null == obj) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断集合是否不包含空元素
     * 
     * @param c
     * @return
     */
    public static boolean isNotContainNull(Collection<?> c) {
        if (DZFCollectionUtil.isEmpty(c)) {
            return false;
        }
        for (Object obj : c) {
            if (null == obj) {
                return false;
            }
        }
        return true;
    }

    /**
     * 将数组附加到集合
     * 
     * @param <T>
     * @param c
     * @param values
     */
    public static <T> void addArrayToCollection(Collection<T> c, T[] values) {
        if (c == null || DZFArrayUtil.isEmpty(values)) {
            return;
        }
        for (T value : values) {
            c.add(value);
        }
    }
}
