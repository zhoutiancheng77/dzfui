package com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.incometax.b;

import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.TaxExcelPos;

// 主表 sb10413001VO
public class IncomeTax {
	private IncomeTaxMain data1;
	private IncomeTaxAgent data2;

	public IncomeTaxMain getData1() {
		return data1;
	}

	public void setData1(IncomeTaxMain data1) {
		this.data1 = data1;
	}

	public IncomeTaxAgent getData2() {
		return data2;
	}

	public void setData2(IncomeTaxAgent data2) {
		this.data2 = data2;
	}
}
