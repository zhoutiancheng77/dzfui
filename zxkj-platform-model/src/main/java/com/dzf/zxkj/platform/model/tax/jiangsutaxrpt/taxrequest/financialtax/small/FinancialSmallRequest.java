package com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.financialtax.small;

/**
 * 财务报表小企业
 * 
 * @author liubj
 *
 */
public class FinancialSmallRequest {

	// 资产负债表
	private BalanceSheet sb29806001vo;
	// 利润表
	private ProfitStatement sb29806002vo;
	// 现金流量表
	private CashFlowStatement sb29806003vo;

	public BalanceSheet getSb29806001vo() {
		return sb29806001vo;
	}

	public void setSb29806001vo(BalanceSheet sb29806001vo) {
		this.sb29806001vo = sb29806001vo;
	}

	public ProfitStatement getSb29806002vo() {
		return sb29806002vo;
	}

	public void setSb29806002vo(ProfitStatement sb29806002vo) {
		this.sb29806002vo = sb29806002vo;
	}

	public CashFlowStatement getSb29806003vo() {
		return sb29806003vo;
	}

	public void setSb29806003vo(CashFlowStatement sb29806003vo) {
		this.sb29806003vo = sb29806003vo;
	}

}
