package com.dzf.zxkj.common.utils;

import com.dzf.zxkj.common.lang.DZFDouble;

public class SafeCompute {
  public static DZFDouble add(DZFDouble d1, DZFDouble d2)
  {
    d1 = d1 == null ? DZFDouble.ZERO_DBL : d1;
    d2 = d2 == null ? DZFDouble.ZERO_DBL : d2;
    return d1.add(d2);
  }

  public static DZFDouble div(DZFDouble d1, DZFDouble d2)
  {
    d1 = d1 == null ? DZFDouble.ZERO_DBL : d1;
    d2 = d2 == null ? DZFDouble.ZERO_DBL : d2;
    return d1.div(d2);
  }

  public static DZFDouble multiply(DZFDouble d1, DZFDouble d2)
  {
    d1 = d1 == null ? DZFDouble.ZERO_DBL : d1;
    d2 = d2 == null ? DZFDouble.ZERO_DBL : d2;
    return d1.multiply(d2);
  }

  public static DZFDouble sub(DZFDouble d1, DZFDouble d2)
  {
    d1 = d1 == null ? DZFDouble.ZERO_DBL : d1;
    d2 = d2 == null ? DZFDouble.ZERO_DBL : d2;
    return d1.sub(d2);
  }
}
