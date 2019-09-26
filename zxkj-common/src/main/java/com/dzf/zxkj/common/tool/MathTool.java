package com.dzf.zxkj.common.tool;

import com.dzf.zxkj.common.lang.DZFDouble;

/**
 * 数学运算工具类
 * 
 */
public class MathTool {
  private MathTool() {
    // 缺省构造方法
  }

  /**
   * 数学运算工具类的工厂方法。
   * 
   * @return 返回数学运算工具类的实例
   * @deprecated 用具体的static方法替代
   */
  @Deprecated
  public static MathTool getInstance() {
    return new MathTool();
  }

  /**
   * 两个数值的加法算法 数值为null时作为0对待
   * 
   * @param d1 要计算的数值1
   * @param d2 要计算的数值2
   * @return d1加上d2的运算结果
   */
  public static DZFDouble add(DZFDouble d1, DZFDouble d2) {
    DZFDouble oper1 = MathTool.nvl(d1);
    DZFDouble oper2 = MathTool.nvl(d2);
    return oper1.add(oper2);
  }

  /**
   * 两个数值值是否相等 数值为null时作为0对待
   * 
   * @param d1 要计算的数值1
   * @param d2 要计算的数值2
   * @return 两个数值相等返回真
   */
  public static boolean equals(DZFDouble d1, DZFDouble d2) {
    return MathTool.compareTo(d1, d2) == 0;
  }

  /**
   * 数值1是否大于数值2 数值为null时作为0对待
   * 
   * @param d1 要计算的数值1
   * @param d2 要计算的数值2
   * @return 数值1大于数值2时返回真
   */
  public static boolean greaterThan(DZFDouble d1, DZFDouble d2) {
    return MathTool.compareTo(d1, d2) > 0;
  }

  /**
   * 数值1是否小于数值2 数值为null时作为0对待
   * 
   * @param d1 要计算的数值1
   * @param d2 要计算的数值2
   * @return 数值1小于数值2时返回真
   */
  public static boolean lessThan(DZFDouble d1, DZFDouble d2) {
    return MathTool.compareTo(d1, d2) < 0;
  }

  /**
   * 两个数值的减法算法 。数值为null时，作为0对待
   * 
   * @param d1 要计算的数值1
   * @param d2 要计算的数值2
   * @return d1减去d2的运算结果
   */
  public static DZFDouble sub(DZFDouble d1, DZFDouble d2) {
    DZFDouble oper1 = MathTool.nvl(d1);
    DZFDouble oper2 = MathTool.nvl(d2);
    return oper1.sub(oper2);
  }

  /**
   * 比较两个数值的大小。数值为null时，作为0对待
   * 
   * @param d1 要计算的数值1
   * @param d2 要计算的数值2
   * @return 0 相等
   *         小于0 d1小于d2
   *         大于0 d1大于d2
   */
  public static int compareTo(DZFDouble d1, DZFDouble d2) {
    DZFDouble oper1 = MathTool.nvl(d1);
    DZFDouble oper2 = MathTool.nvl(d2);
    return oper1.compareTo(oper2);
  }

  /**
   * 比较两个数值的绝对值大小。数值为null时，作为0对待
   * 
   * @param d1 要计算的数值1
   * @param d2 要计算的数值2
   * @return 0 相等
   *         小于0 d1小于d2
   *         大于0 d1大于d2
   */
  public static int absCompareTo(DZFDouble d1, DZFDouble d2) {
    DZFDouble oper1 = d1;
    DZFDouble oper2 = d2;

    if (oper1 == null) {
      oper1 = DZFDouble.ZERO_DBL;
    }
    else {
      oper1 = oper1.abs();
    }
    if (oper2 == null) {
      oper2 = DZFDouble.ZERO_DBL;
    }
    else {
      oper2 = oper2.abs();
    }
    return oper1.compareTo(oper2);
  }

  /**
   * 比较两个数值的算术符号是否相反
   * 
   * @param d1 要计算的数值1
   * @param d2 要计算的数值2
   * @return 算术符号相反时返回true
   */
  public static boolean isDiffSign(DZFDouble d1, DZFDouble d2) {
    DZFDouble oper1 = MathTool.nvl(d1);
    DZFDouble oper2 = MathTool.nvl(d2);
    
    // 如果有一个为零则认为符号相同
    if (isZero(oper1) || isZero(oper2)) {
    	return false;
    }
    boolean isNegative1 =  DZFDouble.ZERO_DBL.compareTo(oper1) < 0;
    boolean isNegative2 =  DZFDouble.ZERO_DBL.compareTo(oper2) < 0;
    
    return (isNegative1 && !isNegative2) || (!isNegative1 && isNegative2);
  }

  /**
   * 如果数值为空，则转化为0返回
   * 
   * @param d 要计算的数值
   * @return 数值为null时，返回0。否则返回元数值
   */
  public static DZFDouble nvl(DZFDouble d) {
    return d == null ? DZFDouble.ZERO_DBL : d;
  }

  /**
   * 数值取绝对值。如果数值为空，则转化为0返回
   * 
   * @param d 要计算的数值
   * @return 数值的绝对值
   */
  public static DZFDouble abs(DZFDouble d) {
    return MathTool.nvl(d).abs();
  }

  /**
   * 数值是否为0。数值为null时，作为0对待
   * 
   * @param d 要计算的数值
   * @return 数值为null或者等于0时返回真
   */
  public static boolean isZero(DZFDouble d) {
    return MathTool.equals(d, DZFDouble.ZERO_DBL);
  }

  /**
   * 数值取反。
   * 
   * @param d 要计算的数值
   * @return 与参数符号相反的数.如果参数为null，则返回0
   */
  public static DZFDouble oppose(DZFDouble d) {
    return MathTool.sub(DZFDouble.ZERO_DBL, d);
  }
}
