package com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.vattax.small;

import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.TaxExcelPos;

//增值税减免税申报明细表合计 Data4_03
@TaxExcelPos(reportID = "10102004", reportname = "增值税减免税申报明细表")
public class TaxReliefSum {

	// 减税项目合计
	// 期初余额
	@TaxExcelPos(row = 7, col = 3)
	private DZFDouble hj1;
	// 本期发生额
	@TaxExcelPos(row = 7, col = 4)
	private DZFDouble hj2;
	// 本期应抵减税额
	@TaxExcelPos(row = 7, col = 5)
	private DZFDouble hj3;
	// 本期实际抵减税额
	@TaxExcelPos(row = 7, col = 6)
	private DZFDouble hj4;
	// 期末余额
	@TaxExcelPos(row = 7, col = 7)
	private DZFDouble hj5;

	// 免税项目合计
	// 免征增值税项目销售额
	@TaxExcelPos(row = 16, col = 3)
	private DZFDouble hj6;
	// 本期实际扣除金额
	@TaxExcelPos(row = 16, col = 4)
	private DZFDouble hj7;
	// 扣除后免税销售额
	@TaxExcelPos(row = 16, col = 5)
	private DZFDouble hj8;
	// 免税销售额对应的进项税额
	@TaxExcelPos(row = 16, col = 6)
	private DZFDouble hj9;
	// 免税额
	@TaxExcelPos(row = 16, col = 7)
	private DZFDouble hj10;

	public DZFDouble getHj1() {
		return hj1;
	}

	public void setHj1(DZFDouble hj1) {
		this.hj1 = hj1;
	}

	public DZFDouble getHj2() {
		return hj2;
	}

	public void setHj2(DZFDouble hj2) {
		this.hj2 = hj2;
	}

	public DZFDouble getHj3() {
		return hj3;
	}

	public void setHj3(DZFDouble hj3) {
		this.hj3 = hj3;
	}

	public DZFDouble getHj4() {
		return hj4;
	}

	public void setHj4(DZFDouble hj4) {
		this.hj4 = hj4;
	}

	public DZFDouble getHj5() {
		return hj5;
	}

	public void setHj5(DZFDouble hj5) {
		this.hj5 = hj5;
	}

	public DZFDouble getHj6() {
		return hj6;
	}

	public void setHj6(DZFDouble hj6) {
		this.hj6 = hj6;
	}

	public DZFDouble getHj7() {
		return hj7;
	}

	public void setHj7(DZFDouble hj7) {
		this.hj7 = hj7;
	}

	public DZFDouble getHj8() {
		return hj8;
	}

	public void setHj8(DZFDouble hj8) {
		this.hj8 = hj8;
	}

	public DZFDouble getHj9() {
		return hj9;
	}

	public void setHj9(DZFDouble hj9) {
		this.hj9 = hj9;
	}

	public DZFDouble getHj10() {
		return hj10;
	}

	public void setHj10(DZFDouble hj10) {
		this.hj10 = hj10;
	}

}
