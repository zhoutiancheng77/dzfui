package com.dzf.zxkj.report.utils;


import com.dzf.zxkj.common.lang.DZFDouble;

public class VoUtils {

    public VoUtils() {
    }

    public static DZFDouble getDZFDouble(DZFDouble ufd) {
        return ufd == null ? DZFDouble.ZERO_DBL : ufd;
    }

    public static DZFDouble getDZFDouble(Object ufd) {
        return ufd == null ? DZFDouble.ZERO_DBL : (DZFDouble) ufd;
    }
}
