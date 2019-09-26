package com.dzf.zxkj.common.constant;

import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class QmjzByDzfInfo {

	public static String dzf_pk_gs = null;//大账房技术公司主键
	public static String jfcode = null;//借方编码
	public static String dfcode = null;//贷方编码
	public static String deptcode = null;//部门辅助核算指定编码
	public static String projcode = null;//项目辅助核算指定编码
	
	static{
		ResourceBundle bundle = PropertyResourceBundle.getBundle("dzfqmjzconfig");
		dzf_pk_gs = bundle.getString("dzf_pk_gs");
		jfcode  = bundle.getString("jfcode");
		dfcode  = bundle.getString("dfcode");
		deptcode= bundle.getString("deptcode");
		projcode= bundle.getString("projcode");
	}
}
