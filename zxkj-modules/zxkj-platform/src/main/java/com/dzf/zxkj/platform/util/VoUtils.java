package com.dzf.zxkj.platform.util;


import com.dzf.zxkj.common.lang.DZFDouble;

public class VoUtils {

	public VoUtils() {
	}
	public static DZFDouble getDZFDouble(DZFDouble ufd){
		return ufd==null?DZFDouble.ZERO_DBL:ufd;
	}
}
