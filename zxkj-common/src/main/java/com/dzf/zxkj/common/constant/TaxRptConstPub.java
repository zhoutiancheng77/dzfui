package com.dzf.zxkj.common.constant;

/**
 * 这个类要和com.dzf.model.gl.jiangsutaxrpt.TaxRptConst这个类一样。 
 * 必须和ynt_tax_sbzl表中的数据一致
 * @author zpm
 *
 */
public class TaxRptConstPub {

	public static String SB_ZLBH10101 = "10101";	//增值税一般纳税人申报表月报
	public static String SB_ZLBH10102 = "10102";	//增值税小规模纳税人申报表季报
	public static String SB_ZLBH50101 = "50101";	//附加税【代征地税】(一般纳税人)月报  季报
	public static String SB_ZLBH50102 = "50102";	//附加税【代征地税】(小规模纳税人)季报 月报
	public static String SB_ZLBH10412 = "10412";	//所得税季度(月度)纳税申报表(A类),不区分一般人和小规模
	public static String SB_ZLBH10413 = "10413";    //所得税季度(月度)纳税申报表(B类),不区分一般人和小规模
	public static String SB_ZLBHA06442 = "A06442";   //个人所得税季度(月度)生产经营所得纳税申报表（A表）

	public static String SB_ZLBHC1 = "C1";          //财报-小企业 季 报(月报)
	public static String SB_ZLBHC2 = "C2";          //财报-一般企业 季  报(月报)
	public static String SB_ZLBHQYKJZD = "29805";          //企业会计制度
	
	public static String ZSXMCODE_01 = "01";//增值税
	public static String ZSXMCODE_04 = "04";//所得税A类
	public static String ZSXMCODE_05 = "05";//所得税B类
	public static String ZSXMCODE_80 = "80";//附加税【代征地税】
	
	public static String ZSXMCODE_D1 = "D1";//印花税月报
	public static String ZSXMCODE_31399 ="31399";//地方各项基金费（工会经费、垃圾处理费）申报表
	
	public static int INCOMTAXTYPE_QY = 0;//所得税类型  0：企业所得税 ， 1：个人所得税生产经营所得
	public static int INCOMTAXTYPE_GR = 1;//所得税类型  0：企业所得税 ， 1：个人所得税生产经营所得
	
	public static int TAXLEVYTYPE_HDZS = 0;// 征收方式 WJX 征收方式:0:定期定额征收（核定征收），1:查账征收
	public static int TAXLEVYTYPE_CZZS = 1;// 征收方式 WJX 征收方式:0:定期定额征收（核定征收），1:查账征收
	
	public static int SHOWSDSRPT_A  = 1;//A表
	public static int SHOWSDSRPT_B  = 2;//B表
	public static int SHOWSDSRPT_GR = 3;//个人
}