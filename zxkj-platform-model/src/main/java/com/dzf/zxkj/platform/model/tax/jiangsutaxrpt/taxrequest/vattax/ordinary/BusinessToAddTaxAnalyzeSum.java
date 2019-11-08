package com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.vattax.ordinary;

import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.TaxExcelPos;

// 营改增税负分析测算明细表合计
@TaxExcelPos(reportID = "10101024", reportname = "营改增税负分析测算明细表")
public class BusinessToAddTaxAnalyzeSum {
	// 增值税不含税销售额
	@TaxExcelPos(row = 8, col = 3)
	private DZFDouble hj1;
	// 合计-增值税销项应纳税额
	@TaxExcelPos(row = 8, col = 4)
	private DZFDouble hj2;
	// 合计-增值税价税合计
	@TaxExcelPos(row = 8, col = 5)
	private DZFDouble hj3;
	// 合计-增值税本期实际扣除金额
	@TaxExcelPos(row = 8, col = 6)
	private DZFDouble hj4;
	// 合计-增值税扣除后含税销售额
	@TaxExcelPos(row = 8, col = 7)
	private DZFDouble hj5;
	// 合计-增值税扣除后销项应纳税额
	@TaxExcelPos(row = 8, col = 8)
	private DZFDouble hj6;
	// 合计-增值税应纳税额
	@TaxExcelPos(row = 8, col = 9)
	private DZFDouble hj7;
	// 合计-营业税期初余额
	@TaxExcelPos(row = 8, col = 10)
	private DZFDouble hj8;
	// 合计-营业税本期发生额
	@TaxExcelPos(row = 8, col = 11)
	private DZFDouble hj9;
	// 合计-营业税本期应扣除金额
	@TaxExcelPos(row = 8, col = 12)
	private DZFDouble hj10;
	// 合计-营业税本期实际扣除金额
	@TaxExcelPos(row = 8, col = 13)
	private DZFDouble hj11;
	// 合计-营业税期末余额
	@TaxExcelPos(row = 8, col = 14)
	private DZFDouble hj12;
	// 合计-营业税应税营业额
	@TaxExcelPos(row = 8, col = 15)
	private DZFDouble hj13;
	// 合计-营业税应纳税额
	@TaxExcelPos(row = 8, col = 16)
	private DZFDouble hj14;

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

	public DZFDouble getHj11() {
		return hj11;
	}

	public void setHj11(DZFDouble hj11) {
		this.hj11 = hj11;
	}

	public DZFDouble getHj12() {
		return hj12;
	}

	public void setHj12(DZFDouble hj12) {
		this.hj12 = hj12;
	}

	public DZFDouble getHj13() {
		return hj13;
	}

	public void setHj13(DZFDouble hj13) {
		this.hj13 = hj13;
	}

	public DZFDouble getHj14() {
		return hj14;
	}

	public void setHj14(DZFDouble hj14) {
		this.hj14 = hj14;
	}

}
