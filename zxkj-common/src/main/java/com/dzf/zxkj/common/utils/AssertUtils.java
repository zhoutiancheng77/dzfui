package com.dzf.zxkj.common.utils;

/**
 * 断言工具类。在接口处使用，确保调用者传过来的参数是正确的。之所以不采取java自身的断言是因为编译器选项可以忽略
 * 它。因此采用这种调用可以随时根据正式产品地日志分析问题的原因。
 */
public class AssertUtils {
  private AssertUtils() {
    // 缺省构造方法
  }

  /**
   * 断言工具类的工厂方法。
   * 
   * @return 返回断言工具类的实例
   * @deprecated 用具体的static方法替代
   */
  @Deprecated
  public static AssertUtils getInstance() {
    return new AssertUtils();
  }

  /**
   * 对当前的条件进行断言处理。如果传入的flag参数不为true。则将expression作为异常的内容抛出
   * 
   * @param flag 要判断的条件
   * @param expression 出现异常时要包含在异常中的内容
   */
  public static void assertValue(boolean flag, String expression) {
    if (!flag) {
      String message = "the argument value is not valid,the expression is: ";
      message += expression;
      throw new IllegalArgumentException(message);
    }
  }

}
