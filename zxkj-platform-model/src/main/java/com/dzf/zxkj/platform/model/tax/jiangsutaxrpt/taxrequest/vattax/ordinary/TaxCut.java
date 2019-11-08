package com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.vattax.ordinary;

import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.TaxExcelPos;

// 增值税减免税申报明细表-减税项目 sb10101021vo_01
@TaxExcelPos(reportID = "10101021", reportname = "增值税减免税申报明细表", rowBegin = 8, rowEnd = 12, col = 0)
public class TaxCut {
	// 减税代码
	@TaxExcelPos(col = 0, splitIndex = 0)
	private String jsxz_dm;
	// 税务事项代码
	@TaxExcelPos(col = 0, splitIndex = 1)
	private String swsx_dm;
	// 减税名称
	@TaxExcelPos(col = 0, splitIndex = 3)
	private String jsdmmc;
	// 期初余额
	@TaxExcelPos(col = 3)
	private DZFDouble qcye;
	// 本期发生额
	@TaxExcelPos(col = 4)
	private DZFDouble bqfse;
	// 本期应抵减税额
	@TaxExcelPos(col = 5)
	private DZFDouble bqydjse;
	// 本期实际抵减税额
	@TaxExcelPos(col = 6)
	private DZFDouble bqsjdjse;
	// 期末余额
	@TaxExcelPos(col = 7)
	private DZFDouble qmye;

	public String getJsxz_dm() {
		return jsxz_dm;
	}

	public void setJsxz_dm(String jsxz_dm) {
		this.jsxz_dm = jsxz_dm;
	}

	public String getSwsx_dm() {
		return swsx_dm;
	}

	public void setSwsx_dm(String swsx_dm) {
		this.swsx_dm = swsx_dm;
	}

	public String getJsdmmc() {
		return jsdmmc;
	}

	public void setJsdmmc(String jsdmmc) {
		this.jsdmmc = jsdmmc;
	}

	public DZFDouble getQcye() {
		return qcye;
	}

	public void setQcye(DZFDouble qcye) {
		this.qcye = qcye;
	}

	public DZFDouble getBqfse() {
		return bqfse;
	}

	public void setBqfse(DZFDouble bqfse) {
		this.bqfse = bqfse;
	}

	public DZFDouble getBqydjse() {
		return bqydjse;
	}

	public void setBqydjse(DZFDouble bqydjse) {
		this.bqydjse = bqydjse;
	}

	public DZFDouble getBqsjdjse() {
		return bqsjdjse;
	}

	public void setBqsjdjse(DZFDouble bqsjdjse) {
		this.bqsjdjse = bqsjdjse;
	}

	public DZFDouble getQmye() {
		return qmye;
	}

	public void setQmye(DZFDouble qmye) {
		this.qmye = qmye;
	}

}
