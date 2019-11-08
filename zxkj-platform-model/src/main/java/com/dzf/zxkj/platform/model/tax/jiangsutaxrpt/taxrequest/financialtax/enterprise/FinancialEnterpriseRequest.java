package com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.financialtax.enterprise;

/**
 * 财务报表 企业会计制度
 * 
 * @author liubj
 *
 */
public class FinancialEnterpriseRequest {
	// 资产负债表
	private BalanceSheet sb29805001vo;
	// 利润表
	private ProfitStatement sb29805002vo;
	// 现金流量表
	private CashFlowStatement sb29805003vo;

	public BalanceSheet getSb29805001vo() {
		return sb29805001vo;
	}

	public void setSb29805001vo(BalanceSheet sb29805001vo) {
		this.sb29805001vo = sb29805001vo;
	}

	public ProfitStatement getSb29805002vo() {
		return sb29805002vo;
	}

	public void setSb29805002vo(ProfitStatement sb29805002vo) {
		this.sb29805002vo = sb29805002vo;
	}

	public CashFlowStatement getSb29805003vo() {
		return sb29805003vo;
	}

	public void setSb29805003vo(CashFlowStatement sb29805003vo) {
		this.sb29805003vo = sb29805003vo;
	}

}
