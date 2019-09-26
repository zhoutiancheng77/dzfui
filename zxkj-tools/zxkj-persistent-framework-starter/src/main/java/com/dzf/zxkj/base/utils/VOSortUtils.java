package com.dzf.zxkj.base.utils;

import com.dzf.zxkj.common.model.CircularlyAccessibleValueObject;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 提供对VO集合的排序功能
 * <ul>
 * <li>支持设置排序的属性数组
 * <li>支持按排序属性分别设置升序还是降序
 * <li>进行比较的属性建议使用可比较的类型（Comparable），否则会转换成String类型进行比较
 * </ul>
 */
public class VOSortUtils {

  /**
   * VO的比较运算类
   * 
   * @since 6.0
   * @version 2010-11-5 上午10:11:33
   * @author 苏建文
   */
  public static class VOComparator<T extends CircularlyAccessibleValueObject>
      implements Comparator<T>, Serializable {
    public static final int ASC = 1;

    public static final int DESC = -1;

    public static final int EQUAL = 0;

    public static final int GREATER = 1;

    public static final int LESS = -1;

    private static final long serialVersionUID = -4413788521875328592L;

    // 升序、降序
    private int[] ascFlags;

    // 比较的字段
    private String[] comparefields;

    public VOComparator(String[] comparefields) {
      super();
      this.comparefields = comparefields;
      this.ascFlags = new int[this.comparefields.length];
      Arrays.fill(this.ascFlags, VOSortUtils.VOComparator.ASC);
    }

    public VOComparator(String[] comparefields, int[] ascFlags) {
      super();
      this.comparefields = comparefields;
      if (ascFlags == null || ascFlags.length == 0) {
        this.ascFlags = new int[this.comparefields.length];
        Arrays.fill(this.ascFlags, VOSortUtils.VOComparator.ASC);
      }
      else {
        this.ascFlags = ascFlags;
      }
    }

    @SuppressWarnings({
      "unchecked", "rawtypes"
    })
    @Override
    public int compare(T vo1, T vo2) {
      int result = VOSortUtils.VOComparator.EQUAL;
      for (int i = 0; i < this.comparefields.length; i++) {
        Object v1 = vo1.getAttributeValue(this.comparefields[i]);
        Object v2 = vo2.getAttributeValue(this.comparefields[i]);
        if (v1 == null && v2 == null) {
          continue;
        }
        if (v1 == null && v2 != null) {
          result = this.ascFlags[i] * VOSortUtils.VOComparator.LESS;
          break;
        }
        if (v1 != null && v2 == null) {
          result = this.ascFlags[i] * VOSortUtils.VOComparator.GREATER;
          break;
        }
        if (v1 instanceof Comparable && v2 instanceof Comparable) {

          int compareresult = ((Comparable) v1).compareTo(v2);
          if (compareresult == 0) {
            continue;
          }
          result = compareresult * this.ascFlags[i];
          break;
        }
        if (v1 == null || v2 == null) {
          continue;
        }
        int compareresult = v1.toString().compareTo(v2.toString());
        if (compareresult == 0) {
          continue;
        }
        result = compareresult * this.ascFlags[i];
        break;
      }
      return result;
    }

  }

  /**
   * 工具类，不需要实例化
   */
  private VOSortUtils() {
    super();
  }

  /**
   * VO排序，可以同时指定排序字段数组及升降序数组，这两个数组应该等长
   *
   * @param <T>
   *          T extends CircularlyAccessibleValueObject
   * @param vos
   *          排序VO数组
   * @param fields
   *          排序属性数组
   * @param ascFlags
   *          升降序数组
   */
  public static <T extends CircularlyAccessibleValueObject> void ascSort(
      List<T> volist, String[] fields) {
    if (volist == null || volist.size() == 0) {
      return;
    }
    if (fields == null || fields.length == 0) {
      return;
    }
    int[] ascFlags = new int[fields.length];
    Arrays.fill(ascFlags, VOSortUtils.VOComparator.ASC);
    VOSortUtils.sort(volist, fields, ascFlags);
  }

  /**
   * VO按升序排序
   *
   * @param <T>
   *          T extends CircularlyAccessibleValueObject
   * @param vos
   *          排序VO数组
   * @param fields
   *          排序属性数组
   */
  public static <T extends CircularlyAccessibleValueObject> void ascSort(
      T[] vos, String[] fields) {
    if (vos == null) {
      return;
    }
    if (fields == null || fields.length == 0) {
      return;
    }
    Comparator<T> c = new VOComparator<T>(fields);
    Arrays.sort(vos, c);
  }

  /**
   * VO按降序排序
   *
   * @param <T>
   *          T extends CircularlyAccessibleValueObject
   * @param vos
   *          排序VO数组
   * @param fields
   *          排序属性数组
   */
  public static <T extends CircularlyAccessibleValueObject> void descSort(
      List<T> volist, String[] fields) {
    if (volist == null || volist.size() == 0) {
      return;
    }
    if (fields == null || fields.length == 0) {
      return;
    }
    int[] ascFlags = new int[fields.length];
    Arrays.fill(ascFlags, VOSortUtils.VOComparator.DESC);
    VOSortUtils.sort(volist, fields, ascFlags);
  }

  /**
   * VO排序，可以同时指定排序字段数组及升降序数组，这两个数组应该等长
   *
   * @param <T>
   * @param volist
   *          排序VO集合
   * @param fields
   *          排序属性数组
   * @param ascFlags
   *          升降序数组
   */
  public static <T extends CircularlyAccessibleValueObject> void descSort(
      T[] vos, String[] fields) {
    if (vos == null) {
      return;
    }
    if (fields == null || fields.length == 0) {
      return;
    }
    int[] ascFlags = new int[fields.length];
    Arrays.fill(ascFlags, VOSortUtils.VOComparator.DESC);
    VOSortUtils.sort(vos, fields, ascFlags);
  }

  /**
   * VO按升序排序
   * 
   * @param <T>
   * @param volist
   *          排序VO集合
   * @param fields
   *          排序属性数组
   */
  public static <T extends CircularlyAccessibleValueObject> void sort(
      List<T> volist, String[] fields, int[] ascFlags) {
    if (volist == null || volist.size() == 0) {
      return;
    }
    if (fields == null || fields.length == 0) {
      return;
    }
    Comparator<T> c = new VOComparator<T>(fields, ascFlags);
    Collections.sort(volist, c);
  }

  /**
   * VO按降序排序
   * 
   * @param <T>
   * @param volist
   *          排序VO集合
   * @param fields
   *          排序属性数组
   */
  public static <T extends CircularlyAccessibleValueObject> void sort(T[] vos,
      String[] fields, int[] ascFlags) {
    if (vos == null) {
      return;
    }
    if (fields == null || fields.length == 0) {
      return;
    }
    Comparator<T> c = new VOComparator<T>(fields, ascFlags);
    Arrays.sort(vos, c);
  }

  public static int compareContainsNull(Comparable s1, Comparable s2) {
    if (s1 == null && s2 == null) {
      return 0;
    } else if (s1 == null) {
      return -1;
    } else if (s2 == null) {
      return 1;
    } else {
      return s1.compareTo(s1);
    }
  }
}
