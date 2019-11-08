package com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.vattax.small;

import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.TaxExcelPos;

//增值税纳税申报表（适用于增值税小规模纳税人）附列资料 Data2_01
@TaxExcelPos(reportID = "10102002", reportname = "增值税纳税申报表（小规模纳税人适用）附列资料")
public class TaxReturnAttach {
	// 期初余额
	@TaxExcelPos(row = 7, col = 0)
	private DZFDouble kce3_qcye;
	// 本期发生额
	@TaxExcelPos(row = 7, col = 1)
	private DZFDouble kce3_bqfse;
	// 本期扣除额
	@TaxExcelPos(row = 7, col = 2)
	private DZFDouble kce3_bqkce;
	// 期末余额
	@TaxExcelPos(row = 7, col = 3, isTotal = true)
	private DZFDouble kce3_qmye;

	// 全部含税收入
	@TaxExcelPos(row = 11, col = 0)
	private DZFDouble xse3_qbhssr;
	// 本期扣除额
	@TaxExcelPos(row = 11, col = 1)
	private DZFDouble xse3_bqkce;
	// 含税销售额
	@TaxExcelPos(row = 11, col = 2)
	private DZFDouble xse3_hsxse;
	// 不含税销售额
	@TaxExcelPos(row = 11, col = 3)
	private DZFDouble xse3_bhsxse;

	// 期初余额（5%征收率）
	@TaxExcelPos(row = 15, col = 0)
	private DZFDouble kce5_qcye;
	// 本期发生额（5%征收率）
	@TaxExcelPos(row = 15, col = 1)
	private DZFDouble kce5_bqfse;
	// 本期扣除额（5%征收率）
	@TaxExcelPos(row = 15, col = 2)
	private DZFDouble kce5_bqkce;
	// 期末余额（5%征收率）
	@TaxExcelPos(row = 15, col = 3, isTotal = true)
	private DZFDouble kce5_qmye;

	// 全部含税收入（5%征收率）
	@TaxExcelPos(row = 19, col = 0)
	private DZFDouble xse5_qbhssr;
	// 本期扣除额
	@TaxExcelPos(row = 19, col = 1)
	private DZFDouble xse5_bqkce;
	// 含税销售额（5%征收率）
	@TaxExcelPos(row = 19, col = 2)
	private DZFDouble xse5_hsxse;
	// 不含税销售额（5%征收率）
	@TaxExcelPos(row = 19, col = 3)
	private DZFDouble xse5_bhsxse;

	public DZFDouble getKce3_qcye() {
		return kce3_qcye;
	}

	public void setKce3_qcye(DZFDouble kce3_qcye) {
		this.kce3_qcye = kce3_qcye;
	}

	public DZFDouble getKce3_bqfse() {
		return kce3_bqfse;
	}

	public void setKce3_bqfse(DZFDouble kce3_bqfse) {
		this.kce3_bqfse = kce3_bqfse;
	}

	public DZFDouble getKce3_bqkce() {
		return kce3_bqkce;
	}

	public void setKce3_bqkce(DZFDouble kce3_bqkce) {
		this.kce3_bqkce = kce3_bqkce;
	}

	public DZFDouble getKce3_qmye() {
		return kce3_qmye;
	}

	public void setKce3_qmye(DZFDouble kce3_qmye) {
		this.kce3_qmye = kce3_qmye;
	}

	public DZFDouble getXse3_qbhssr() {
		return xse3_qbhssr;
	}

	public void setXse3_qbhssr(DZFDouble xse3_qbhssr) {
		this.xse3_qbhssr = xse3_qbhssr;
	}

	public DZFDouble getXse3_bqkce() {
		return xse3_bqkce;
	}

	public void setXse3_bqkce(DZFDouble xse3_bqkce) {
		this.xse3_bqkce = xse3_bqkce;
	}

	public DZFDouble getXse3_hsxse() {
		return xse3_hsxse;
	}

	public void setXse3_hsxse(DZFDouble xse3_hsxse) {
		this.xse3_hsxse = xse3_hsxse;
	}

	public DZFDouble getXse3_bhsxse() {
		return xse3_bhsxse;
	}

	public void setXse3_bhsxse(DZFDouble xse3_bhsxse) {
		this.xse3_bhsxse = xse3_bhsxse;
	}

	public DZFDouble getKce5_qcye() {
		return kce5_qcye;
	}

	public void setKce5_qcye(DZFDouble kce5_qcye) {
		this.kce5_qcye = kce5_qcye;
	}

	public DZFDouble getKce5_bqfse() {
		return kce5_bqfse;
	}

	public void setKce5_bqfse(DZFDouble kce5_bqfse) {
		this.kce5_bqfse = kce5_bqfse;
	}

	public DZFDouble getKce5_bqkce() {
		return kce5_bqkce;
	}

	public void setKce5_bqkce(DZFDouble kce5_bqkce) {
		this.kce5_bqkce = kce5_bqkce;
	}

	public DZFDouble getKce5_qmye() {
		return kce5_qmye;
	}

	public void setKce5_qmye(DZFDouble kce5_qmye) {
		this.kce5_qmye = kce5_qmye;
	}

	public DZFDouble getXse5_qbhssr() {
		return xse5_qbhssr;
	}

	public void setXse5_qbhssr(DZFDouble xse5_qbhssr) {
		this.xse5_qbhssr = xse5_qbhssr;
	}

	public DZFDouble getXse5_bqkce() {
		return xse5_bqkce;
	}

	public void setXse5_bqkce(DZFDouble xse5_bqkce) {
		this.xse5_bqkce = xse5_bqkce;
	}

	public DZFDouble getXse5_hsxse() {
		return xse5_hsxse;
	}

	public void setXse5_hsxse(DZFDouble xse5_hsxse) {
		this.xse5_hsxse = xse5_hsxse;
	}

	public DZFDouble getXse5_bhsxse() {
		return xse5_bhsxse;
	}

	public void setXse5_bhsxse(DZFDouble xse5_bhsxse) {
		this.xse5_bhsxse = xse5_bhsxse;
	}
}
