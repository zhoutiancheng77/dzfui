package com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.financialtax.annual.ordinary;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 财务报表一般企业年报
 * 
 * @author liubj
 *
 */
public class FinancialOrdinaryRequest {
	// 资产负债表
	@JSONField(name = "Data1_01")
	private BalanceSheet data1_01;
	// 利润表
	@JSONField(name = "Data2_01")
	private ProfitStatement data2_01;
	// 现金流量表
	@JSONField(name = "Data3_01")
	private CashFlowStatement data3_01;
	// 所有者权益变更表
//	@JSONField(name = "Data4_01")
//	private ChangeOfOwnersEquity[] data4_01;

	public BalanceSheet getData1_01() {
		return data1_01;
	}

	public void setData1_01(BalanceSheet data1_01) {
		this.data1_01 = data1_01;
	}

	public ProfitStatement getData2_01() {
		return data2_01;
	}

	public void setData2_01(ProfitStatement data2_01) {
		this.data2_01 = data2_01;
	}

	public CashFlowStatement getData3_01() {
		return data3_01;
	}

	public void setData3_01(CashFlowStatement data3_01) {
		this.data3_01 = data3_01;
	}

	/*public ChangeOfOwnersEquity[] getData4_01() {
		return data4_01;
	}

	public void setData4_01(ChangeOfOwnersEquity[] data4_01) {
		this.data4_01 = data4_01;
	}*/

}
