package com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.vattax.ordinary;

import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.TaxExcelPos;

//增值税纳税申报表附列资料（五）（不动产分期抵扣计算表）  sb10101023vo_01
@TaxExcelPos(reportID = "10101023", reportname = "增值税纳税申报表附列资料（五）")
public class TaxReturnAttach5 {
	// 期初余额(期初待抵扣不动产进项税额)
	@TaxExcelPos(row = 6, col = 0)
	private DZFDouble qcddkbdcjxse;
	// 本期发生额(本期不动产进项税额增加额)
	@TaxExcelPos(row = 6, col = 1)
	private DZFDouble bqbdcjxsezje;
	// 本期应抵减税额(本期可抵扣不动产进项税额)
	@TaxExcelPos(row = 6, col = 2)
	private DZFDouble bqkdkbdcjxse;
	// 本期实际抵减税额(本期转入的待抵扣不动产进项税额)
	@TaxExcelPos(row = 6, col = 3)
	private DZFDouble bqzrddkbdcjxse;
	// 本期实际抵减税额1(本期转出的待抵扣不动产进项税额)
	@TaxExcelPos(row = 6, col = 4)
	private DZFDouble bqzcddkbdcjxse;
	// 期末余额(期末待抵扣不动产进项税额)
	@TaxExcelPos(row = 6, col = 5, isTotal = true)
	private DZFDouble qmddkbdcjxse;

	public DZFDouble getQcddkbdcjxse() {
		return qcddkbdcjxse;
	}

	public void setQcddkbdcjxse(DZFDouble qcddkbdcjxse) {
		this.qcddkbdcjxse = qcddkbdcjxse;
	}

	public DZFDouble getBqbdcjxsezje() {
		return bqbdcjxsezje;
	}

	public void setBqbdcjxsezje(DZFDouble bqbdcjxsezje) {
		this.bqbdcjxsezje = bqbdcjxsezje;
	}

	public DZFDouble getBqkdkbdcjxse() {
		return bqkdkbdcjxse;
	}

	public void setBqkdkbdcjxse(DZFDouble bqkdkbdcjxse) {
		this.bqkdkbdcjxse = bqkdkbdcjxse;
	}

	public DZFDouble getBqzrddkbdcjxse() {
		return bqzrddkbdcjxse;
	}

	public void setBqzrddkbdcjxse(DZFDouble bqzrddkbdcjxse) {
		this.bqzrddkbdcjxse = bqzrddkbdcjxse;
	}

	public DZFDouble getBqzcddkbdcjxse() {
		return bqzcddkbdcjxse;
	}

	public void setBqzcddkbdcjxse(DZFDouble bqzcddkbdcjxse) {
		this.bqzcddkbdcjxse = bqzcddkbdcjxse;
	}

	public DZFDouble getQmddkbdcjxse() {
		return qmddkbdcjxse;
	}

	public void setQmddkbdcjxse(DZFDouble qmddkbdcjxse) {
		this.qmddkbdcjxse = qmddkbdcjxse;
	}

}
