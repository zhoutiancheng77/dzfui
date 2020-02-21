package com.dzf.zxkj.platform.util.taxrpt.cqtc.deal;

import java.util.HashMap;
import java.util.Map;

/**
 * 天南星常量类
 * @author mfz
 */
public interface CqtcConst {

	// 成功
	public static final String SUCCESS = "0";
	// 失败
	public static final String FAIL = "99";
	// 成功信息
	public static final String SUCCESSINFO = "请求成功";
	// 失败
	public static final String FAILINFO = "请求失败";
	// 处理中
	public static final String CALL_STATUS_PROCESS = "P";
	// 成功
	public static final String CALL_STATUS_SUCCESS = "S";
	// 失败
	public static final String CALL_STATUS_FAIL = "F";
	//请求类型
	public static final String REQTYPECODE1 = "CQTC_TO_DZF";
	public static final String REQTYPECODE2 = "DZF_TO_CQTC";
	// 流入
	public static final String DATA_DIRECTION_IN = "IN";
	// 流出
	public static final String DATA_DIRECTION_OUT = "OUT";
	// openid超时时间
	public static final int DATA_TIMEOUT = 7200;
	// openid超时返回值
	public static final int FAIL_TIMEOUT = 55;
	
//	//双方系统约定authkey
//	public static String AUTHKEY = CqtcPropertyUtils.getProperties().getProperty("authkey");
//	//限定的ip
//	public static String IPENABLE = CqtcPropertyUtils.getProperties().getProperty("ipEnable");
//	public static String IPADDRS = CqtcPropertyUtils.getProperties().getProperty("ipAddrs");
	/***
	 * 方法名对照
	 */
	@SuppressWarnings("serial")
	public final Map<String, String> METHOD_BUSNAME_MAPPING = new HashMap<String, String>() {
		{
			put("login", "用户登录");
			put("update", "修改用户");
			put("getReportInfo", "生成报文");
			put("saveInvoiceSummary", "保存进项销项总额");
			put("updateDeclareStatus", "回写申报状态");
			put("saveReportInit", "获取期初数据");
			put("saveInvoiceSummary", "保存进项销项总额");
			put("queryCorpList", "获取纳税人列表");
		}
	};
	
	/**
	 * 增值税申报表和模板表的对照
	 */
	public final Map<String,String[]> TAXREPORT_PK_MAPPING = new HashMap<String, String[]>(){
		{
			put("zzs0_t0", new String[]{"chongqing010100000000001","10101001","增值税纳税申报表（适用于增值税一般纳税人）"});
			put("zzs0_t1", new String[]{"chongqing010100000000002","10101002","增值税纳税申报表附列资料（一）"});
			put("zzs0_t2", new String[]{"chongqing010100000000003","10101003","增值税纳税申报表附列资料（二）"});
			put("zzs0_t3", new String[]{"chongqing010100000000004","10101004","增值税纳税申报表附列资料（三）"});
			put("zzs0_t4", new String[]{"chongqing010100000000005","10101005","增值税纳税申报表附列资料（四）"});
			put("zzs0_t5", new String[]{"chongqing010100000000023","10101023","增值税纳税申报表附列资料（五）"});
			put("zzs0_t6", new String[]{"chongqing010100000000006","10101006","固定资产（不含不动产）进项税额抵扣情况表"});
			put("zzs0_t7", new String[]{"chongqing010100000000022","10101022","本期抵扣进项税额结构明细表"});
			put("zzs0_t8", new String[]{"chongqing010100000000021","10101021","增值税减免税申报明细表"});
			put("zzs0_t9", new String[]{"chongqing010100000000024","10101024","营改增税负分析测算明细表"});
			put("zzs0_t10", new String[]{"chongqing010100000000007","10101007","代扣代缴税收通用缴款书抵扣清单"});
			put("zzs0_t11", new String[]{"chongqing010100000000009","10101009","成品油购销存情况明细表"});
			put("zzs0_t12", new String[]{"chongqing010100000000026","10101026","汇总纳税企业增值税分配表"});
		}
	};
}
