package com.dzf.zxkj.common.constant;

public class TaxRptConst {
//	101:未提交  大账房定义
//	下面属性属于查询结果
//	0-已提交  1-受理失败 2-受理成功 3-申报失败 4-申报成功 5-作废 6-缴款失败 7-缴款成功'; 
	public static int iSBZT_DM_UnSubmit = 101;
	public static int iSBZT_DM_Submitted = 0;
	public static int iSBZT_DM_AcceptFailute = 1;
	public static int iSBZT_DM_AcceptSuccess = 2;
	public static int iSBZT_DM_ReportFailute = 3;
	public static int iSBZT_DM_ReportSuccess = 4;
	public static int iSBZT_DM_ReportCancel = 5;
	public static int iSBZT_DM_PayFailure = 6;
	public static int iSBZT_DM_PaySuccess = 7;
	
	public static String SRPTNAME10101001 = "增值税纳税申报表";
	public static String SRPTNAME10101002 = "增值税纳税申报表附列资料（一）";
	public static String SRPTNAME10101003 = "增值税纳税申报表附列资料（二）";
	public static String SRPTNAME10101004 = "增值税纳税申报表附列资料（三）";
	public static String SRPTNAME10101005 = "增值税纳税申报表附列资料（四）";
	public static String SRPTNAME10101006 = "固定资产进项税额抵扣情况表";
	public static String SRPTNAME10101007 = "代扣代缴税收通用缴款书抵扣清单";
	public static String SRPTNAME10101008 = "应税服务减免项目清单表";
	public static String SRPTNAME10101009 = "成品油购销存情况明细表";
	public static String SRPTNAME10101010 = "农产品核定扣除增值税进项税额计算表（汇总表）";
	public static String SRPTNAME10101011 = "投入产出法核定农产品增值税进项税额计算表";
	public static String SRPTNAME10101012 = "成本法核定农产品增值税进项税额计算表";
	public static String SRPTNAME10101013 = "购进农产品直接销售核定农产品增值税进项税额计算表";
	public static String SRPTNAME10101014 = "购进农产品用于生产经营且不构成货物实体核定农产品增值税进项税额";
	public static String SRPTNAME10101015 = "生产企业进料加工抵扣明细表";
	public static String SRPTNAME10101016 = "生产企业出口货物征（免）税明细主表 ";
	public static String SRPTNAME10101017 = "生产企业出口货物征（免）税明细从表";
	public static String SRPTNAME10101018 = "国际运输征免税明细数据表";
	public static String SRPTNAME10101019 = "研发、设计服务征免税明细数据表";
	public static String SRPTNAME10101020 = "部分产品销售统计表";
	public static String SRPTNAME10101021 = "增值税减免税申报明细表";

	/**
	 * 必须和ynt_tax_sbzl表中的数据一致，否则有问题。
	 */
	public static String SB_ZLBH10101 = "10101";	//增值税一般纳税人申报表月报
	public static String SB_ZLBH1010101 = "1010101";	//增值税一般纳税人申报表季报,重庆在用
	public static String SB_ZLBH10102 = "10102";	//增值税小规模纳税人申报表季报
	public static String SB_ZLBH1010201 = "1010201";	//增值税小规模纳税人申报表月度
	public static String SB_ZLBH50101 = "50101";	//附加税【代征地税】(一般纳税人)月报  季报
	public static String SB_ZLBH50102 = "50102";	//附加税【代征地税】(小规模纳税人)季报 月报
	public static String SB_ZLBH10412 = "10412";	//所得税季度(月度)纳税申报表(A类),不区分一般人和小规模
	public static String SB_ZLBH10413 = "10413";	//所得税季度(月度)纳税申报表(B类),不区分一般人和小规模
	public static String SB_ZLBH39801 = "39801";	// 财报-一般企业年报
	public static String SB_ZLBH39806 = "39806";	// 财报-小企业年报
	public static String SB_ZLBH_SETTLEMENT = "A";	//企业所得税年度汇算清缴
	public static String SB_ZLBH10601 = "10601";    //文化事业建设费月报
	public static String SB_ZLBHD1 = "D1";			//印花税月报
	public static String SB_ZLBHC1 = "C1";          //财报-小企业(季)报(月报)
	public static String SB_ZLBHC2 = "C2";          //财报-一般企业(季)报(月报)
	// 财报-企业会计制度
	public static String SB_ZLBH29805 = "29805";
	//如果这里面没有，就新增。
	public static String SB_ZLBHGS = "G";			//个人所得税--月报
	// 地方各项基金费
	public static String SB_ZLBH_LOCAL_FUND_FEE = "31399";  
	
	public static String KJQJ_2013="00000100AA10000000000BMD";//小企业
	public static String KJQJ_2007="00000100AA10000000000BMF";//企业
	// 企业会计制度 
	public static String KJQJ_QYKJZD = "00000100000000Ig4yfE0005";//企业会计
	public static String KJQJ_MINFEI = "00000100AA10000000000BMQ";//民非
	public static String KJQJ_SHIYE  = "00000100000000Ig4yfE0003";//事业单位

	//单据状态	8:未审核  1：已审核  9:待确认  10:确认通过  11:确认不通过
	public static int IBILLSTATUS_UNAPPROVE = 8;
	public static int IBILLSTATUS_APPROVEED = 1;
	public static int IBILLSTATUS_UNCONFIRM = 9;
	public static int IBILLSTATUS_CONFIRMPASS = 10;
	public static int IBILLSTATUS_COMFIRMUNPASS = 11;
	
	public static String getSBzt_mc(int isbzt_dm)
	{
		if (isbzt_dm == iSBZT_DM_UnSubmit)
		{
			return "未提交";
		}
		else if (isbzt_dm == iSBZT_DM_Submitted)
		{
			return "已提交";
		}
		else if (isbzt_dm == iSBZT_DM_AcceptFailute)
		{
			return "受理失败";
		}
		else if (isbzt_dm == iSBZT_DM_AcceptSuccess)
		{
			return "受理成功";
		}
		else if (isbzt_dm == iSBZT_DM_ReportFailute)
		{
			return "申报失败";
		}
		else if (isbzt_dm == iSBZT_DM_ReportSuccess)
		{
			return "申报成功";
		}
		else if (isbzt_dm == iSBZT_DM_ReportCancel)
		{
			return "作废";
		}
		else if (isbzt_dm == iSBZT_DM_PayFailure)
		{
			return "缴款失败";
		}
		else if (isbzt_dm == iSBZT_DM_PaySuccess)
		{
			return "缴款成功";
		}
		return "未知";
	}
}
