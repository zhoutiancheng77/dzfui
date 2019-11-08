package com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.incometax.a;

// A202000企业所得税汇总纳税分支机构所得税分配表 sb10412005VO
public class BranchIncomeTax {

	private BranchIncomeTaxMain data2;
	private BranchIncomeTaxDetail[] data1;

	public BranchIncomeTaxMain getData2() {
		return data2;
	}

	public void setData2(BranchIncomeTaxMain data2) {
		this.data2 = data2;
	}

	public BranchIncomeTaxDetail[] getData1() {
		return data1;
	}

	public void setData1(BranchIncomeTaxDetail[] data1) {
		this.data1 = data1;
	}

}
