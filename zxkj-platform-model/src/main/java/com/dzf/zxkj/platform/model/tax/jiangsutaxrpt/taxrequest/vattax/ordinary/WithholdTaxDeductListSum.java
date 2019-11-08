package com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.vattax.ordinary;

import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.TaxExcelPos;

//代扣代缴税收通用缴款书抵扣清单合计 sb10101007vo_02
@TaxExcelPos(reportID = "10101007", reportname = "代扣代缴税收通用缴款书抵扣清单")
public class WithholdTaxDeductListSum {
	// 合计-税额
	@TaxExcelPos(row = 26, col = 5)
	private DZFDouble sehj;

	public DZFDouble getSehj() {
		return sehj;
	}

	public void setSehj(DZFDouble sehj) {
		this.sehj = sehj;
	}
}
