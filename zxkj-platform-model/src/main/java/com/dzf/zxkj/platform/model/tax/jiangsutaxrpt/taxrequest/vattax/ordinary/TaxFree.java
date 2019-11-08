package com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.vattax.ordinary;

import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.TaxExcelPos;

//增值税减免税申报-免税项目 sb10101021vo_02
@TaxExcelPos(reportID = "10101021", reportname = "增值税减免税申报明细表", rowBegin = 19, rowEnd = 31, col = 0)
public class TaxFree {
	// 免税代码
	@TaxExcelPos(col = 0, splitIndex = 0)
	private String msxz_dm;
	// 税务事项代码
	@TaxExcelPos(col = 0, splitIndex = 1)
	private String swsx_dm;
	// 免税名称
	@TaxExcelPos(col = 0, splitIndex = 3)
	private String msdmmc;
	// 免征增值税项目销售额
	@TaxExcelPos(col = 3)
	private DZFDouble mzxse;
	// 本期实际扣除金额
	@TaxExcelPos(col = 4)
	private DZFDouble bqsjkcje;
	// 扣除后免税销售额
	@TaxExcelPos(col = 5)
	private DZFDouble kchmsxse;
	// 免税销售额对应的进项税额
	@TaxExcelPos(col = 6)
	private DZFDouble jxse;
	// 免税额
	@TaxExcelPos(col = 7)
	private DZFDouble mse;

	public String getMsxz_dm() {
		return msxz_dm;
	}

	public void setMsxz_dm(String msxz_dm) {
		this.msxz_dm = msxz_dm;
	}

	public String getSwsx_dm() {
		return swsx_dm;
	}

	public void setSwsx_dm(String swsx_dm) {
		this.swsx_dm = swsx_dm;
	}

	public String getMsdmmc() {
		return msdmmc;
	}

	public void setMsdmmc(String msdmmc) {
		this.msdmmc = msdmmc;
	}

	public DZFDouble getMzxse() {
		return mzxse;
	}

	public void setMzxse(DZFDouble mzxse) {
		this.mzxse = mzxse;
	}

	public DZFDouble getBqsjkcje() {
		return bqsjkcje;
	}

	public void setBqsjkcje(DZFDouble bqsjkcje) {
		this.bqsjkcje = bqsjkcje;
	}

	public DZFDouble getKchmsxse() {
		return kchmsxse;
	}

	public void setKchmsxse(DZFDouble kchmsxse) {
		this.kchmsxse = kchmsxse;
	}

	public DZFDouble getJxse() {
		return jxse;
	}

	public void setJxse(DZFDouble jxse) {
		this.jxse = jxse;
	}

	public DZFDouble getMse() {
		return mse;
	}

	public void setMse(DZFDouble mse) {
		this.mse = mse;
	}

}
