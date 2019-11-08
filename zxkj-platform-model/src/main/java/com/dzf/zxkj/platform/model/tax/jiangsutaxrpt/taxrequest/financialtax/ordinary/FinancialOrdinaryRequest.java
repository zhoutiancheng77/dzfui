package com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.financialtax.ordinary;

/**
 * 财务报表一般企业
 * 
 * @author liubj
 *
 */
public class FinancialOrdinaryRequest {
	// 资产负债表
	private BalanceSheet sb29801001vo;
	// 利润表
	private ProfitStatement sb29801002vo;
	// 现金流量表
	private CashFlowStatement sb29801003vo;

	public BalanceSheet getSb29801001vo() {
		return sb29801001vo;
	}

	public void setSb29801001vo(BalanceSheet sb29801001vo) {
		this.sb29801001vo = sb29801001vo;
	}

	public ProfitStatement getSb29801002vo() {
		return sb29801002vo;
	}

	public void setSb29801002vo(ProfitStatement sb29801002vo) {
		this.sb29801002vo = sb29801002vo;
	}

	public CashFlowStatement getSb29801003vo() {
		return sb29801003vo;
	}

	public void setSb29801003vo(CashFlowStatement sb29801003vo) {
		this.sb29801003vo = sb29801003vo;
	}

}
