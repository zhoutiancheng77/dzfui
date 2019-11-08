package com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.vattax.ordinary;

import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.TaxExcelPos;

// 农产品核定扣除增值税进项税额计算表（汇总表）
@TaxExcelPos(reportID = "10101010", reportname = "农产品核定扣除增值税进项税额计算表（汇总表)")
public class AgriculturalProductsDeductInputTax {
	// 购进农产品用于生产经营且不构成货物实体备注
	@TaxExcelPos(row = 8, col = 4)
	private String gjncpyscswbz;
	// 购进农产品用于生产经营且不构成货物实体当期允许抵扣农产品增值税进项税额元
	@TaxExcelPos(row = 8, col = 3)
	private DZFDouble gjncpyscswdqyxse;
	// 购进农产品直接销售备注
	@TaxExcelPos(row = 7, col = 4)
	private String gjncpzjxsbz;
	// 购进农产品直接销售当期允许抵扣农产品增值税进项税额元
	@TaxExcelPos(row = 7, col = 3)
	private DZFDouble gjncpzjxsdqyxse;
	// 以购进农产品为原料生产货物成本法备注
	@TaxExcelPos(row = 6, col = 4)
	private String ygjncpwtbfbz;
	// 以购进农产品为原料生产货物成本法当期允许抵扣农产品增值税进项税额元
	@TaxExcelPos(row = 6, col = 3)
	private DZFDouble ygjncpwtbfdqyxse;
	// 以购进农产品为原料生产货物投入产出法备注
	@TaxExcelPos(row = 5, col = 4)
	private String ygjncpwtrccbz;
	// 以购进农产品为原料生产货物投入产出法当期允许抵扣农产品增值税进项税额元
	@TaxExcelPos(row = 5, col = 3)
	private DZFDouble ygjncpwtrccdqyxse;

	public String getGjncpyscswbz() {
		return gjncpyscswbz;
	}

	public void setGjncpyscswbz(String gjncpyscswbz) {
		this.gjncpyscswbz = gjncpyscswbz;
	}

	public DZFDouble getGjncpyscswdqyxse() {
		return gjncpyscswdqyxse;
	}

	public void setGjncpyscswdqyxse(DZFDouble gjncpyscswdqyxse) {
		this.gjncpyscswdqyxse = gjncpyscswdqyxse;
	}

	public String getGjncpzjxsbz() {
		return gjncpzjxsbz;
	}

	public void setGjncpzjxsbz(String gjncpzjxsbz) {
		this.gjncpzjxsbz = gjncpzjxsbz;
	}

	public DZFDouble getGjncpzjxsdqyxse() {
		return gjncpzjxsdqyxse;
	}

	public void setGjncpzjxsdqyxse(DZFDouble gjncpzjxsdqyxse) {
		this.gjncpzjxsdqyxse = gjncpzjxsdqyxse;
	}

	public String getYgjncpwtbfbz() {
		return ygjncpwtbfbz;
	}

	public void setYgjncpwtbfbz(String ygjncpwtbfbz) {
		this.ygjncpwtbfbz = ygjncpwtbfbz;
	}

	public DZFDouble getYgjncpwtbfdqyxse() {
		return ygjncpwtbfdqyxse;
	}

	public void setYgjncpwtbfdqyxse(DZFDouble ygjncpwtbfdqyxse) {
		this.ygjncpwtbfdqyxse = ygjncpwtbfdqyxse;
	}

	public String getYgjncpwtrccbz() {
		return ygjncpwtrccbz;
	}

	public void setYgjncpwtrccbz(String ygjncpwtrccbz) {
		this.ygjncpwtrccbz = ygjncpwtrccbz;
	}

	public DZFDouble getYgjncpwtrccdqyxse() {
		return ygjncpwtrccdqyxse;
	}

	public void setYgjncpwtrccdqyxse(DZFDouble ygjncpwtrccdqyxse) {
		this.ygjncpwtrccdqyxse = ygjncpwtrccdqyxse;
	}

}
