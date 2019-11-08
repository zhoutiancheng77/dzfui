package com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.vattax.ordinary;

import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.TaxExcelPos;

// 成本法核定农产品增值税进项税额计算表(附表二)
@TaxExcelPos(reportID = "10101012", reportname = "成本法核定农产品增值税进项税额计算表", rowBegin = 7, rowEnd = 18, col = 1)
public class CostMethodAgriculturalProductsInputTax {
	// 当期允许抵扣农产品进项税额元
	@TaxExcelPos(col = 5)
	private DZFDouble dqyxdkncpjxse;
	// 农产品耗用率
	@TaxExcelPos(col = 4)
	private DZFDouble ncphyl;
	// 当期主营业务成本元
	@TaxExcelPos(col = 3)
	private DZFDouble dqzyywcb;
	// 扣除率
	@TaxExcelPos(col = 2)
	private DZFDouble kcl;
	// 产品名称
	@TaxExcelPos(col = 1, isName = true)
	private String cpmc;

	public DZFDouble getDqyxdkncpjxse() {
		return dqyxdkncpjxse;
	}

	public void setDqyxdkncpjxse(DZFDouble dqyxdkncpjxse) {
		this.dqyxdkncpjxse = dqyxdkncpjxse;
	}

	public DZFDouble getNcphyl() {
		return ncphyl;
	}

	public void setNcphyl(DZFDouble ncphyl) {
		this.ncphyl = ncphyl;
	}

	public DZFDouble getDqzyywcb() {
		return dqzyywcb;
	}

	public void setDqzyywcb(DZFDouble dqzyywcb) {
		this.dqzyywcb = dqzyywcb;
	}

	public DZFDouble getKcl() {
		return kcl;
	}

	public void setKcl(DZFDouble kcl) {
		this.kcl = kcl;
	}

	public String getCpmc() {
		return cpmc;
	}

	public void setCpmc(String cpmc) {
		this.cpmc = cpmc;
	}

}
