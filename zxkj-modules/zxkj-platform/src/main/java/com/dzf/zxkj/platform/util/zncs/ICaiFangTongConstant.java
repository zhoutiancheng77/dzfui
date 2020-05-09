package com.dzf.zxkj.platform.util.zncs;

public class ICaiFangTongConstant {

	//发票类型 来源财房通
	public static final String FPZLDM_Z0 = "Z0";//增值税专用发票
	public static final String FPZLDM_Z2 = "Z2";//增值税普通发票
	public static final String FPZLDM_41 = "41";//卷式
	public static final String FPZLDM_51 = "51";//电子发票
	public static final String FPZLDM_JDC = "JDC";//机动车发票
	
	//发票类型 来源发票扫码
	public static final String FPZLDM_SM_01 = "01";//增值税专用发票
	public static final String FPZLDM_SM_02 = "02";//货运运输业增值税专用发票
	public static final String FPZLDM_SM_03 = "03";//机动车销售统一发票
	public static final String FPZLDM_SM_04 = "04";//增值税普通发票
	public static final String FPZLDM_SM_10 = "10";//增值税电子普通发票
	public static final String FPZLDM_SM_51 = "51";//增值税电子普通发票
	
	//开票票类型 来源发票扫码
	public static final String FPLX_SM_1 = "1";//正常
	public static final String FPLX_SM_2 = "2";//作废（和zfbz标志一致，信息有冗余）
	public static final String FPLX_SM_3 = "3";//发票不一致（查验金额不一致等）
	public static final String FPLX_SM_4 = "4";//查无此票
	
	//开票类型 来源票通进项
	public static final String FPLX_PT_0 = "0";//正常
	public static final String FPLX_PT_1 = "1";//冲红->失控
	public static final String FPLX_PT_2 = "2";//作废
	public static final String FPLX_PT_3 = "3";//失控->冲红
	public static final String FPLX_PT_4 = "4";//异常
	public static final String FPLX_PT_N = "N";//未作废
	public static final String FPLX_PT_Y = "Y";//作废
	
	//开票类型 公共的 汇集的
	public static final String FPLX_1 = "1";//正票
	public static final String FPLX_2 = "2";//红票
	public static final String FPLX_3 = "3";//空白废票
	public static final String FPLX_4 = "4";//正废  更名为填开作废
	public static final String FPLX_5 = "5";//负废
	
	//财房通vo来源整理
	public static final int LYLX_XX = 1;//财房通  销项
	public static final int LYLX_JX = 2;//票通  进项
	public static final int LYLX_XX_KP = 3;//票通开票 销项
	public static final int LYLX_SM = 5;//发票扫码
	public static final int _LYLX_HXP = 6;//票通回写

	
	
	//销进项界面数据 来源
	public static final String LYDJLX = "CFT01";//来源于财房通
	public static final String LYDJLX_SM = "CFT02";//来源于发票扫码
	public static final String LYDJLX_PT = "CFT03";//来源于票通进项
	public static final String LYDJLX_PTKP = "CFT04";//来源于票通开票
	public static final String LYDJLX_OCR = "CFT05";//来源于OCR
	public static final String LYDJLX_KP = "CFT06";//来源开票处理
	
	//系统参数
	public static final String SUCCESS = "0000";
	
}
