package com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.vattax.ordinary;

import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.TaxExcelPos;

//农产品核定扣除增值税进项税额计算表（汇总表）
@TaxExcelPos(reportID = "10101010", reportname = "农产品核定扣除增值税进项税额计算表（汇总表)")
public class AgriculturalProductsDeductInputTaxSum {
	// 当期允许抵扣农产品增值税进项税额-合计
	@TaxExcelPos(row = 10, col = 3)
	private DZFDouble hjse;

	public DZFDouble getHjse() {
		return hjse;
	}

	public void setHjse(DZFDouble hjse) {
		this.hjse = hjse;
	}

}
