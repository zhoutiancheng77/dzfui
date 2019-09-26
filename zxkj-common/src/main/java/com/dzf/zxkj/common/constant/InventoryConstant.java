package com.dzf.zxkj.common.constant;

/**
 * 总账存货－－－－－－－－出入库常量定义
 * @author zpm
 *
 */
public class InventoryConstant {
	public static String  IC_IN_PREFIX = "CG";
	
	public static String  IC_OUT_PREFIX = "XS";
	
	public static String IC_STYLE_IN  = "in";
	
	public static String IC_STYLE_OUT = "out";
	
	public static int IC_FZMXHS = 0;//辅助明细
	
	public static int IC_CHDLHS = 1;//存货大类
	
	public static int IC_NO_MXHS = 2;//不核算存货
	
	///////////////////////
	public static int IC_RULE_0 = 0;//存货名称+规格（型号）+计量单位
	public static int IC_RULE_1 = 1;//存货名称+计量单位
	
}