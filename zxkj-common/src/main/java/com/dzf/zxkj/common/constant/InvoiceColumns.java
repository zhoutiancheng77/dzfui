package com.dzf.zxkj.common.constant;

public class InvoiceColumns {

	// 识别后的中间表 表头字段
	public static String[] OCR_CODES = { "vinvoicecode", "vinvoiceno", "dinvoicedate", "vpurchname", "vpurchtaxno",
			"vsalename", "vsaletaxno", "ntotaltax", "nmny", "ntaxnmny", "vtotaltaxcapital", "checkcode", "invoicetype",
			"sourceid", "vocrdrcode" };

	public static String[] SCANNER_CODES = { "Code1", "Code2", "Date", "BuyerName", "BuyerTaxN0", "PurchaserName",
			"PurchaserTaxN0", "TotalCapital2", "Sum", "SumTax", "TotalCapital1", "CheckCode", "invoicetype", "sourceid",
			"drcode" };

	public static String[] INVOICE_HCODES = { "vinvoicecode", "vinvoiceno", "dinvoicedate", "invoicetype", "vpurchname",
			"vpurchtaxno", "vsalename", "vsaletaxno", "ntotaltax", "nmny", "ntaxnmny", "vsaleopenacc", "vsalephoneaddr",
			"vpuropenacc", "vpurphoneaddr", "vfirsrinvname","vmemo" };

	// 进项清单
	public static String[] INCOM_HCODES = { "fp_dm", "fp_hm", "kprj", "fpzl", "ghfmc", "ghfsbh", "xhfmc", "xhfsbh",
			"jshj", "hjje", "spse", "xhfyhzh", "xhfdzdh", "ghfyhzh", "ghfdzdh", "spmc","demo" };
	// 销项清单
	public static String[] INCOM_HCODES1 = { "fp_dm", "fp_hm", "kprj", "fpzl", "khmc", "ghfsbh", "xhfmc", "xhfsbh",
			"jshj", "hjje", "spse", "xhfyhzh", "xhfdzdh", "ghfyhzh", "ghfdzdh", "spmc","demo" };

	public static String[] INVOICE_BCODES = { "invname", "invtype", "itemunit", "itemamount", "itemprice", "itemmny",
			"itemtaxrate", "itemtaxmny" };
	public static String[] INCOM_BCODES = { "bspmc", "invspec", "measurename", "bnum", "bprice", "bhjje", "bspsl",
			"bspse" };

}
