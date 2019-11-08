package com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.vattax.ordinary;

import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.TaxExcelPos;

//成本法核定农产品增值税进项税额计算表(附表二)
@TaxExcelPos(reportID = "10101012", reportname = "成本法核定农产品增值税进项税额计算表")
public class CostMethodAgriculturalProductsInputTaxSum {
	// 合计
	@TaxExcelPos(row = 19, col = 5)
	private DZFDouble hjje;

	public DZFDouble getHjje() {
		return hjje;
	}

	public void setHjje(DZFDouble hjje) {
		this.hjje = hjje;
	}

}
