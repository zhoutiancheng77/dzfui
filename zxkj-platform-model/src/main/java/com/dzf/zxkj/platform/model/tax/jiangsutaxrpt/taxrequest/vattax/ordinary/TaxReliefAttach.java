package com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.vattax.ordinary;

import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.TaxExcelPos;

//增值税减免税申报明细表补充 sb10101021vo_03
@TaxExcelPos(reportID = "10101021", reportname = "增值税减免税申报明细表")
public class TaxReliefAttach {
	// 出口免税-免征增值税项目销售额
	@TaxExcelPos(row = 17, col = 3)
	private DZFDouble mzzzsxseckmsxse;
	// 跨境服务-免征增值税项目销售额
	@TaxExcelPos(row = 18, col = 3)
	private DZFDouble mzzzsxsekjfwxse;

	public DZFDouble getMzzzsxseckmsxse() {
		return mzzzsxseckmsxse;
	}

	public void setMzzzsxseckmsxse(DZFDouble mzzzsxseckmsxse) {
		this.mzzzsxseckmsxse = mzzzsxseckmsxse;
	}

	public DZFDouble getMzzzsxsekjfwxse() {
		return mzzzsxsekjfwxse;
	}

	public void setMzzzsxsekjfwxse(DZFDouble mzzzsxsekjfwxse) {
		this.mzzzsxsekjfwxse = mzzzsxsekjfwxse;
	}

}
