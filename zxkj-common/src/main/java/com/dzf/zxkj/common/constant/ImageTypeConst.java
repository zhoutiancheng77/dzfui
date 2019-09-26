package com.dzf.zxkj.common.constant;

public class ImageTypeConst {

	/***************** 发票 需要区分是采购还是销售 *****************/
	// 专用发票
	public static final String SPECIA_INVOICE_CODE = "01";
	public static final String SPECIA_INVOICE_NAME = "专用发票";
	public static final String SPECIA_INVOICE_SHORTNAME = "专票";

	// 普通发票
	public static final String ORDINARY_INVOICE_CODE = "04";
	public static final String ORDINARY_INVOICE_NAME = "普通发票";
	public static final String ORDINARY_INVOICE_SHORTNAME = "普票";

	// 机动车销售统一发票
	public static final String MOTOR_INVOICE = "机动车销售统一发票";

	/***************** 银行类票据 *****************/

	public static final int BANK_UNDEFINED_BILL2 = -2;// 未定义类型（需要按照摘要匹配）
	public static final int BANK_UNDEFINED_BILL1 = -1;// 未定义类型（需要根据收付款方匹配）
	public static final int BANK_RECEIPTS_BILL = 4;// 银行收款单
	public static final int BANK_PAY_BILL = 5;// 银行付款单
	public static final int BANK_CHARGES_BILL = 6;// 银行手续费
	public static final int BANK_COST_BILL = 7;// 银行费用
	public static final int BANK_PAYTAXES_BILL = 9;// 缴纳税款
	public static final int BANK_SOCIALSECURITY_BILL = 8;// 支付社保
	public static final int QUOTA_TRAVELBUSINESS_BILL = 14;// 差旅报销单
	public static final int QUOTA_COST_BILL = 102;// 费用报销单

	public static final int BANK_OTHER_BILL = 15;// 其他单据
	public static final int BANK_EXISTENTIAL_BILL = 101;// 存现

	public static final String BANK__BILL_FLAG = "b";// 银行类票据标识
	public static final String QUOTA_INVOICE_FLAG = "c";// 定额发票标识

	/***************** 银行收付款回单 需要区分收款还是付款 *****************/
	public static final String KHSFKRZTZ = "客户收付款入账通知";
	public static final String TCZDRZHD = "同城自动入账回单";
	public static final String DHPZ = "电汇凭证";
	public static final String RZTZ = "入账通知";

	/***************** 银行收款单 4 *****************/
	public static final String SK = "(收款)";
	public static final String KHSK = "客户收款";
	public static final String DJHD = "贷记回单";
	public static final String ZFYWSKHD = "支付业务收款回单";
	public static final String YHDJ = "银行单据";
	public static final String TCSK = "同城收款";
	public static final String SKHD = "收款回单";
	public static final String FKHDTCSK = "付款回单同城收款";
	public static final String DZHDPZ = "电子回单凭证";
	public static final String DJTZ = "贷记通知";
	public static final String SKPZ = "收款凭证";
	public static final String KHSKHD = "客户收款回单";
	public static final String DZHR = "电子汇入";

	/***************** 银行付款单 5 *****************/
	public static final String JJHD = "借记回单";
	public static final String FK = "(付款)";
	public static final String KHFK = "客户付款";
	public static final String FKPZ = "付款凭证";
	public static final String JJTZ = "借记通知";
	public static final String YCDQKK = "银承到期扣款";
	public static final String EDZF = "二代支付";
	public static final String ZKHD = "转款回单";
	public static final String DIZZPZ = "电子转账凭证";

	/***************** 银行手续费 6 *****************/
	public static final String SFRZTZS = "收费入账通知书";
	public static final String QYWYSXF = "企业网银手续费";
	public static final String KFRZTZS = "扣费入账通知书";
	public static final String FKHD = "付费回单";
	public static final String SFPZ = "收费凭证";
	public static final String ZZHKSXF = "转账汇款手续费";
	public static final String QYWYJYSXF = "企业网银交易手续费";
	
	/***************** 银行费用 7 *****************/
	public static final String LXSRHD = "利息收入回单";
	public static final String DKLXQD = "贷款利息清单";
	public static final String SFHD = "收费回单";
	public static final String CKJXHD = "存款结息回单";

	/***************** 缴纳税款 9 *****************/
	public static final String DZJSFKPZ = "电子缴税付款凭证";

	/***************** 支付社保 8 *****************/
	public static final String SBGJJJCD = "社保、公积金缴存单";

	/***************** 存现 101 *****************/
	public static final String DGXJCKHD = "对公现金存款回单";

	/******************* 定额票据 ******************/
	/***************** 14://差旅费 *****************/
	public static final String HCP = "火车票";
	/***************** 102://费用报销单 *****************/
	public static final String DEFP = "定额发票";

}
