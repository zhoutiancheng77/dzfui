package com.dzf.zxkj.common.constant;

import java.util.HashMap;

/**
 * 收费管理产品
 * @author mfz
 *
 */
public interface IDzfServiceConst {
	public static String DzfServiceProduct_01="00000100000000qY52cV0006";//发票扫码
	public static String DzfServiceProduct_02="00000100000000qY52cV000B";//企业所得税年度汇算清缴风控系统
	public static String DzfServiceProduct_03="00000100000001CWAPJd0005";//一键报税
	public static String DzfServiceProduct_04="00000100000001CWAPJd000B";//智能凭证
	public static String DzfServiceProduct_05="00000100000001CWAPJd000H";//风控体检
	public static String DzfServiceProduct_06="00000100000001CWGISk0005";//普通版
	public static String DzfServiceProduct_07="00000100000001CWGISk000D";//C端版
	public static String DzfServiceProduct_08="00000100000001CWGISk000E";//渠道商版
	public static String DzfServiceProduct_09="00000100000001CWGISk000F";//标准版1
	public static String DzfServiceProduct_10="00000100000001CWGISk000G";//标准版2
	public static String DzfServiceProduct_11="00000100000001CWGISk000H";//标准版3
	public static String DzfServiceProduct_17="00000100000001CWGISk000N";//标准版4
	public static String DzfServiceProduct_12="00000100000001CWGISk000I";//旗舰版1
	public static String DzfServiceProduct_13="00000100000001CWGISk000J";//旗舰版2
	public static String DzfServiceProduct_14="00000100000001CWGISk000K";//旗舰版3
	public static String DzfServiceProduct_18="00000100000001CWGISk000O";//旗舰版4
	public static String DzfServiceProduct_15="00000100000001CWGISk000L";//加盟商版
	public static String DzfServiceProduct_16="00000100000001CWGISk000M";//工厂版
	public static String DzfServiceProduct_19="00000100000001CWGISk000P";//账房通
	public static String DzfServiceProduct_20="00000100000001CWGISk000Q";//票通宝
	public static String DzfServiceProduct_21="00000100000001CWGISk000R";//扫码枪-霍尼韦尔
	public static String DzfServiceProduct_22="00000100000001CWGISk000S";//扫描仪-3025款
	public static String DzfServiceProduct_23="00000100000001CWGISk000T";//USB服务器-30口
	public static String DzfServiceProduct_24="00000100000001CWGISk000U";//USB服务器-100口
	/**
	 * 会计公司版本管理，可能参照的产品
	 */
	public static String[] dzfProducts=new String[]{DzfServiceProduct_07,DzfServiceProduct_08,DzfServiceProduct_09,DzfServiceProduct_10,DzfServiceProduct_11,DzfServiceProduct_17,DzfServiceProduct_12,DzfServiceProduct_13,DzfServiceProduct_14,DzfServiceProduct_18,DzfServiceProduct_15,DzfServiceProduct_16};
	/**
	 * 会计公司线下购买，可能参照的产品
	 */
	public static String[] offLineProducts=new String[]{DzfServiceProduct_03,DzfServiceProduct_04,DzfServiceProduct_05};
	/**
	 * 硬件
	 */
	public static String[] hardwareProducts=new String[]{DzfServiceProduct_19,DzfServiceProduct_20,DzfServiceProduct_21,DzfServiceProduct_22,DzfServiceProduct_23,DzfServiceProduct_24};
	/**
	 * 收费产品
	 */
	
	public static String[][] chargeProducts= new String[][]{{DzfServiceProduct_03,"一键报税"},{DzfServiceProduct_04,"智能凭证"},{DzfServiceProduct_05,"风控体检"}};
	/**
	 * 按不同产品设置启动日
	 */
	public static String ChargeType_01="01";//一键报税
	public static String ChargeType_02="02";//智能凭证
	public static String ChargeType_03="03";//风控体检
	public static String ChargeType_04="04";//标准产品
	
	/**
	 * 版本
	 */
	public static String DzfVersion_01="000000000000000000000001";//普通版
    public static String DzfVersion_02="000000000000000000000002";//C端版
    public static String DzfVersion_03="000000000000000000000003";//渠道商版
    public static String DzfVersion_04="000000000000000000000004";//标准版
    public static String DzfVersion_05="000000000000000000000005";//旗舰版
    public static String DzfVersion_06="000000000000000000000006";//加盟商版
    public static String DzfVersion_07="000000000000000000000007";//工厂版
	
	/**
	 * 产品小版本和大版本对照关系
	 * key:小版本
	 * value:大版本
	 */
	public static HashMap<String, String> versionMap=new HashMap<String, String>(){{    
	    put(DzfServiceProduct_06, "000000000000000000000001");    
	    put(DzfServiceProduct_07, "000000000000000000000002");  
	    put(DzfServiceProduct_08, "000000000000000000000003");    
	    put(DzfServiceProduct_09, "000000000000000000000004");
	    put(DzfServiceProduct_10, "000000000000000000000004");    
	    put(DzfServiceProduct_11, "000000000000000000000004");  
	    put(DzfServiceProduct_17, "000000000000000000000004");    
	    put(DzfServiceProduct_12, "000000000000000000000005");
	    put(DzfServiceProduct_13, "000000000000000000000005");
	    put(DzfServiceProduct_14, "000000000000000000000005");    
	    put(DzfServiceProduct_18, "000000000000000000000005");  
	    put(DzfServiceProduct_15, "000000000000000000000006");    
	    put(DzfServiceProduct_16, "000000000000000000000007");
	}};  
}
