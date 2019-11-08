package com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.vattax.ordinary;

import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.TaxExcelPos;

// 购进农产品用于生产经营且不构成货物实体核定农产品增值税进项税额计算表
@TaxExcelPos(reportID = "10101014", reportname = "购进农产品用于生产经营且不构成货物实体核定农产品增值税进项税额", rowBegin = 7, rowEnd = 16, col = 1)
public class PurchaseAgriculturalProductsNoEntityInputTax {
	// 产品名称
	@TaxExcelPos(col = 1, isName = true)
	private String cpmc;
	// 耗用农产品名称
	@TaxExcelPos(col = 2)
	private String hyncpmc;
	// 当期耗用农产品数量吨
	@TaxExcelPos(col = 3)
	private DZFDouble dqhyncpsl;
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
	// 农产品平均购买单价元/吨
	@TaxExcelPos(col = 8)
	private DZFDouble ncppjgmdj;
	// 扣除率
	@TaxExcelPos(col = 9)
	private DZFDouble kcl;
	// 当期允许抵扣农产品进项税额元
	@TaxExcelPos(col = 10)
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

	public DZFDouble getDqhyncpsl() {
		return dqhyncpsl;
	}

	public void setDqhyncpsl(DZFDouble dqhyncpsl) {
		this.dqhyncpsl = dqhyncpsl;
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

	public DZFDouble getNcppjgmdj() {
		return ncppjgmdj;
	}

	public void setNcppjgmdj(DZFDouble ncppjgmdj) {
		this.ncppjgmdj = ncppjgmdj;
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
