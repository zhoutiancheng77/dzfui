package com.dzf.zxkj.platform.util.zncs;

public class OcrInvoiceColumns {

	public static String fileID ="图片ID";
	// 识别的网站结果表头字段
	public static String[] HEAD_NAMES = { "发票代码", "发票号码", "开票日期", "发票类型", "购买方名称", "购买方纳税号", "销售方名称", "销售方纳税号", "价税合计",
			"金额合计", "税额合计", "销售方开户账号", "销售方地址电话", "购买方开户账号", "购买方地址电话", "首件货物名称", "识别状态", "查验次数", "机器编号", "校验码", "标志",
			"二维码","备注","单据标识号","代开标识","图片ID" };

	// 识别的票通结果表头字段
	public static String[] HEAD_NAMES1 = { "fpdm", "fphm", "kprq", "fpzl", "gfmc", "gfsbh", "xfmc", "xfsbh", "jshj",
			"je", "se", "xfyhzh", "xfdzdh", "gfsbh", "gfdzdh", "vfirsrinvname", "istate", "cycs", "jqbh", "jym", "zfbz",
			"drcode" };

	// 识别后的中间表 表头字段
	public static String[] HEAD_CODES = { "vinvoicecode", "vinvoiceno", "dinvoicedate", "invoicetype", "vpurchname",
			"vpurchtaxno", "vsalename", "vsaletaxno", "ntotaltax", "nmny", "ntaxnmny", "vsaleopenacc", "vsalephoneaddr",
			"vpuropenacc", "vpurphoneaddr", "vfirsrinvname", "istate", "cycs", "jqbh", "jym", "zfbz", "drcode","vmemo",
			"uniquecode","dkbs","webid" };

	// 识别的网站结果表体字段
	public static String[] ITEM_NAMES = { "行货物或应税劳务名称", "行规格型号", "行单位", "行数量", "行单价", "行金额", "行税率", "行税额" };
	
	// 识别的网站结果表体字段
	public static String[] ITEM_NAMES_PASSMNY = { "行项目名称", "行车牌号", "行类型", "行数量", "行单价", "行金额", "行税率", "行税额", "行通行日期起", "行通行日期止" };

	// 识别的票通结果表体字段
	public static String[] ITEM_NAMES1 = { "hwmc", "ggxh", "dw", "sl", "dj", "je", "slv", "se", "txrqq", "txrqz" };

	// 识别后的中间表 表体字段
	public static String[] ITEM_CODES = { "invname", "invtype", "itemunit", "itemamount", "itemprice", "itemmny",
			"itemtaxrate", "itemtaxmny","txrqq","txrqz" };

	// ocr识别的字段
	public static String[] OCR_CODES = { "vinvoicecode", "vinvoiceno", "dinvoicedate", "vpurchname", "vpurchtaxno",
			"vsalename", "vsaletaxno", "ntotaltax", "nmny", "ntaxnmny", "vtotaltaxcapital", "items", "ntax",
			"vocrdrcode", "checkcode", "invoicetype" };
	public static String[] OCR_NAMES = { "发票代码", "发票号码", "开票日期", "购方企业名称", "购方纳税号", "销方企业名称", "销方纳税号", "价税合计", "金额",
			"税额", "价税合计大写", "明细", "税率", "二维码", "校验码", "票据类型" };

	public static String[] OCR_CODES1 = { "vinvoicecode", "vinvoiceno", "dinvoicedate", "checkcode", "vpurchname",
			"vtotaltaxcapital", "ntotaltax", "vsalename", "nmny", "vocrdrcode" };
	public static String[] OCR_NAMES1 = { "发票代码", "发票号码", "开票日期", "校验码", "购买方名称", "价税合计大写", "小写", "销售方名称", "金额",
			"二维码" };

	// ocr字段对应的序号
	// public static int[] OCR_INDEXS = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11,
	// 13 };

	// 识别的网站结果表头字段
	public static String[] HEAD_NEW_NAMES = { "发票代码", "发票号码", "发票日期", "发票类型", "无税金额", "验证码" };

	// 识别后的中间表 表头字段
	public static String[] HEAD_NEW_CODES = { "vinvoicecode", "vinvoiceno", "dinvoicedate", "invoicetype", "nmny",
			"jym" };

	// ocr识别银行字段
	public static String[][] OCR_BANK_CODEAMES = {
		{"invoicetype","单据类型"},
		{"vpurchname","付款方名称"},
		{"vsalename","收款方名称"},
		{"dinvoicedate","日期"},
		{"ntotaltax","金额"},
		{"vsaleopenacc","银行名称"},
		{"vsalephoneaddr","备注"},
		{"vpurphoneaddr","识别时间"},
		{"vmemo","税项明细"},
		{"istate","识别状态"},
		{"uniquecode","单据标识号"},
		{"vpurchtaxno","付款方账号"},
		{"vsaletaxno","收款方账号"},
		{"vpuropenacc","单据标题"},
		{"zfbz","标志"},
		{"webid",fileID},
		{"vpurbankname","付款银行"},
		{"vsalebankname","收款银行"}
	};

	// 火车票 定额发票
	public static String[][] OCR_QUOTAINVOICE_CODENAMES = {
		{"invoicetype","单据类型"},
		{"vpurchname","目的地"},
		{"vsalename","出发地"},
		{"dinvoicedate","日期"},
		{"ntotaltax","金额"},
		{"vpurphoneaddr","识别时间"},
		{"istate","识别状态"},
		{"vinvoicecode","发票代码"},
		{"vinvoiceno","发票号码"},
		{"vsalephoneaddr","备注"},
		{"zfbz","标志"},
		{"webid",fileID}
	};
}
