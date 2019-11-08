package com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.vattax.ordinary;

import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.TaxExcelPos;

//购进农产品直接销售核定农产品增值税进项税额计算表
@TaxExcelPos(reportID = "10101013", reportname = "购进农产品直接销售核定农产品增值税进项税额计算表")
public class PurchaseAgriculturalProductsInputTaxSum {
	// 合计
	@TaxExcelPos(row = 18, col = 12)
	private DZFDouble hjje;

	public DZFDouble getHjje() {
		return hjje;
	}

	public void setHjje(DZFDouble hjje) {
		this.hjje = hjje;
	}

}
