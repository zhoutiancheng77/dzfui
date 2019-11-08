package com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.financialtax.annual.small;

/**
 * 财务报表小企业年报
 * 
 * @author liubj
 *
 */
public class FinancialSmallRequest {

	// 资产负债表
	private BalanceSheet sb39806001vo;
	// 利润表
	private ProfitStatement sb39806002vo;
	// 现金流量表
	private CashFlowStatement sb39806003vo;

	public BalanceSheet getSb39806001vo() {
		return sb39806001vo;
	}

	public void setSb39806001vo(BalanceSheet sb39806001vo) {
		this.sb39806001vo = sb39806001vo;
	}

	public ProfitStatement getSb39806002vo() {
		return sb39806002vo;
	}

	public void setSb39806002vo(ProfitStatement sb39806002vo) {
		this.sb39806002vo = sb39806002vo;
	}

	public CashFlowStatement getSb39806003vo() {
		return sb39806003vo;
	}

	public void setSb39806003vo(CashFlowStatement sb39806003vo) {
		this.sb39806003vo = sb39806003vo;
	}

}
