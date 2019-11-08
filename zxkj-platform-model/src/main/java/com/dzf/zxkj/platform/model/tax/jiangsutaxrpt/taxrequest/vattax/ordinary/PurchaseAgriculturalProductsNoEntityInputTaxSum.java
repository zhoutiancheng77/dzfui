package com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.vattax.ordinary;

import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.TaxExcelPos;

//购进农产品用于生产经营且不构成货物实体核定农产品增值税进项税额计算表
@TaxExcelPos(reportID = "10101014", reportname = "购进农产品用于生产经营且不构成货物实体核定农产品增值税进项税额")
public class PurchaseAgriculturalProductsNoEntityInputTaxSum {
	// 合计
	@TaxExcelPos(row = 17, col = 10)
	private DZFDouble hjje;

	public DZFDouble getHjje() {
		return hjje;
	}

	public void setHjje(DZFDouble hjje) {
		this.hjje = hjje;
	}

}
