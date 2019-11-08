package com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.vattax.ordinary;

import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.TaxExcelPos;

// 投入产出法核定农产品增值税进项税额计算表(附表一)
@TaxExcelPos(reportID = "10101011", reportname = "投入产出法核定农产品增值税进项税额计算表", rowBegin = 7, rowEnd = 18, col = 1)
public class InOutputMethodAgriculturalProductsInputTax {
	// 产品名称
	@TaxExcelPos(col = 1, isName = true)
	private String cpmc;
	// 耗用农产品名称
	@TaxExcelPos(col = 2)
	private String hyncpmc;
	// 核定的单耗数量吨
	@TaxExcelPos(col = 3)
	private DZFDouble hdddhsl;
	// 期初库存农产品数量
	@TaxExcelPos(col = 4)
	private DZFDouble qckcncpsl;
	// 期初平均买价
	@TaxExcelPos(col = 5)
	private DZFDouble qcpjmj;
	// 当期购进农产品数量
	@TaxExcelPos(col = 6)
	private DZFDouble dqgjncpsl;
	// 当期买价元/吨
	@TaxExcelPos(col = 7)
	private DZFDouble dqmj;
	// 平均购买单价元/吨
	@TaxExcelPos(col = 8)
	private DZFDouble pjgmdj;
	// 当期销售货物数量吨
	@TaxExcelPos(col = 9)
	private DZFDouble dqxshwsl;
	// 扣除率
	@TaxExcelPos(col = 10)
	private DZFDouble kcl;
	// 当期允许抵扣农产品进项税额元
	@TaxExcelPos(col = 11)
	private DZFDouble dqyxdkncpjxse;

	public String getCpmc() {
		return cpmc;
	}

	public void setCpmc(String cpmc) {
		this.cpmc = cpmc;
	}

	public String getHyncpmc() {
		return hyncpmc;
	}

	public void setHyncpmc(String hyncpmc) {
		this.hyncpmc = hyncpmc;
	}

	public DZFDouble getHdddhsl() {
		return hdddhsl;
	}

	public void setHdddhsl(DZFDouble hdddhsl) {
		this.hdddhsl = hdddhsl;
	}

	public DZFDouble getQckcncpsl() {
		return qckcncpsl;
	}

	public void setQckcncpsl(DZFDouble qckcncpsl) {
		this.qckcncpsl = qckcncpsl;
	}

	public DZFDouble getQcpjmj() {
		return qcpjmj;
	}

	public void setQcpjmj(DZFDouble qcpjmj) {
		this.qcpjmj = qcpjmj;
	}

	public DZFDouble getDqgjncpsl() {
		return dqgjncpsl;
	}

	public void setDqgjncpsl(DZFDouble dqgjncpsl) {
		this.dqgjncpsl = dqgjncpsl;
	}

	public DZFDouble getDqmj() {
		return dqmj;
	}

	public void setDqmj(DZFDouble dqmj) {
		this.dqmj = dqmj;
	}

	public DZFDouble getPjgmdj() {
		return pjgmdj;
	}

	public void setPjgmdj(DZFDouble pjgmdj) {
		this.pjgmdj = pjgmdj;
	}

	public DZFDouble getDqxshwsl() {
		return dqxshwsl;
	}

	public void setDqxshwsl(DZFDouble dqxshwsl) {
		this.dqxshwsl = dqxshwsl;
	}

	public DZFDouble getKcl() {
		return kcl;
	}

	public void setKcl(DZFDouble kcl) {
		this.kcl = kcl;
	}

	public DZFDouble getDqyxdkncpjxse() {
		return dqyxdkncpjxse;
	}

	public void setDqyxdkncpjxse(DZFDouble dqyxdkncpjxse) {
		this.dqyxdkncpjxse = dqyxdkncpjxse;
	}

}
