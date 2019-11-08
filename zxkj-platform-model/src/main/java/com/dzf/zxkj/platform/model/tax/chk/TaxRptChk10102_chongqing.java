package com.dzf.zxkj.platform.model.tax.chk;

public class TaxRptChk10102_chongqing {
	public static String[] saCheckCondition = new String[] {
			// 主表
			// E列
			"增值税纳税申报表!E7 >= 增值税纳税申报表!E8 + 增值税纳税申报表!E9",
			"增值税纳税申报表!E13 >= 增值税纳税申报表!E14",
			"增值税纳税申报表!E19 >= 增值税纳税申报表!E20",
			"增值税纳税申报表!E25 >= SUM(增值税纳税申报表!E26 : 增值税纳税申报表!E27)",
			
			//F列
			"增值税纳税申报表!F7 >= 增值税纳税申报表!F8 + 增值税纳税申报表!F9",
			"增值税纳税申报表!F10 >= 增值税纳税申报表!F11 + 增值税纳税申报表!F12",
			"增值税纳税申报表!F19 >= 增值税纳税申报表!F20",
			"增值税纳税申报表!F25 >= 增值税纳税申报表!F26 + 增值税纳税申报表!F27",

			// 增值税纳税申报表（小规模纳税人适用）附列资料

			"增值税纳税申报表（小规模纳税人适用）附列资料!C8 <= 增值税纳税申报表（小规模纳税人适用）附列资料!A8 + 增值税纳税申报表（小规模纳税人适用）附列资料!B8",
			"增值税纳税申报表（小规模纳税人适用）附列资料!C8 <= 增值税纳税申报表（小规模纳税人适用）附列资料!A12",
			"增值税纳税申报表（小规模纳税人适用）附列资料!C16 <= 增值税纳税申报表（小规模纳税人适用）附列资料!A16 + 增值税纳税申报表（小规模纳税人适用）附列资料!B16",
			"增值税纳税申报表（小规模纳税人适用）附列资料!C16 <= 增值税纳税申报表（小规模纳税人适用）附列资料!A20",

			// 增值税减免税申报明细表

//			"增值税减免税申报明细表!D17 == 增值税纳税申报表!E17 + 增值税纳税申报表!F17",

			"增值税减免税申报明细表!G8 <= 增值税减免税申报明细表!F8", "增值税减免税申报明细表!G9 <= 增值税减免税申报明细表!F9",
			"增值税减免税申报明细表!G10 <= 增值税减免税申报明细表!F10", "增值税减免税申报明细表!G11 <= 增值税减免税申报明细表!F11",
			"增值税减免税申报明细表!G12 <= 增值税减免税申报明细表!F12", "增值税减免税申报明细表!G13 <= 增值税减免税申报明细表!F13" };
}