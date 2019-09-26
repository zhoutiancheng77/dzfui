package com.dzf.zxkj.common.constant;


import com.dzf.zxkj.common.utils.IDefaultValue;

/**
 * 参数设置常量
 * @author wangzhn
 *
 */
public interface IParameterConstants extends IDefaultValue {

	public static final int LEVEL_0 = 0;//集团
	
	public static final int LEVEL_1 = 1;//会计公司
	
	public static final int LEVEL_2 = 2;//小企业
	
	public static final String DZF004 = "dzf004";
	public static final int FROMSALT = 0;//清单取数
	public static final int FROMGL = 1;//总账取数
	
	//产品要求全部走 接口填报，这个参数删除。
//	public static final String DZF006 = "dzf006";//是否是接口填报
//	public static final int ISINTER = 0;//接口填报
//	public static final int ISYJBS = 1;//一键报税填报
	
	public static final String DZF009 = "dzf009";//数量精度
	public static final String DZF010 = "dzf010";//单价精度
	public static final String DZF011 = "dzf011";//汇率精度
	public static final String DZF012 = "dzf012";//进销项及银行单据生成凭证制单日期
	public static final String DZF015 = "dzf015";//打印设置
	public static final String DZF016 = "dzf016";//账表科目显示
	public static final String DZF017 = "dzf017";//凭证管理科目显示
	public static final String DZF018 = "dzf018";//制单时显示科目余额
	public static final String DZF019 = "dzf019";//制单时显示科目数量
	public static final String DZF020 = "dzf020";//凭证显示最后修改时间
	public static final String DZF021 = "dzf021";//一键取票方式
}
