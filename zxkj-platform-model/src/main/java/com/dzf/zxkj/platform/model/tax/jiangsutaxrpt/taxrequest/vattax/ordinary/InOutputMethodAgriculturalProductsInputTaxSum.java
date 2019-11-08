package com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.vattax.ordinary;

import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.TaxExcelPos;

//投入产出法核定农产品增值税进项税额计算表(附表一)
@TaxExcelPos(reportID = "10101011", reportname = "投入产出法核定农产品增值税进项税额计算表")
public class InOutputMethodAgriculturalProductsInputTaxSum {
	// 合计
	@TaxExcelPos(row = 19, col = 11)
	private DZFDouble hjje;

	public DZFDouble getHjje() {
		return hjje;
	}

	public void setHjje(DZFDouble hjje) {
		this.hjje = hjje;
	}

}
