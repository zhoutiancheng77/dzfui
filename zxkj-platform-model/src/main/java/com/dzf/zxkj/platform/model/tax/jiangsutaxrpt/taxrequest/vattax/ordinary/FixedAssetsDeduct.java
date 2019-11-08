package com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.vattax.ordinary;

import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.TaxExcelPos;

// 固定资产（不含不动产）进项税额抵扣情况表 sb10101006vo_01
@TaxExcelPos(reportID = "10101006", reportname = "固定资产（不含不动产）进项税额抵扣情况表")
public class FixedAssetsDeduct {
	// 海关进口增值税专用缴款书-当期申报抵扣的固定资产进项税额累计
	@TaxExcelPos(row = 5, col = 2)
	private DZFDouble hgjkzzszyjkssbdkdgdzcjxselj;
	// 增值税专用发票-当期申报抵扣的固定资产进项税额累计
	@TaxExcelPos(row = 4, col = 2)
	private DZFDouble zzszyfpsbdkdgdzcjxselj;
	// 海关进口增值税专用缴款书-当期申报抵扣的固定资产进项税额
	@TaxExcelPos(row = 5, col = 1)
	private DZFDouble hgjkzzszyjksdqsbdkdgdzcjxse;
	// 增值税专用发票-当期申报抵扣的固定资产进项税额
	@TaxExcelPos(row = 4, col = 1)
	private DZFDouble zzszyfpdqsbdkdgdzcjxse;

	// 合计-当期申报抵扣的固定资产进项税额
	@TaxExcelPos(row = 6, col = 1)
	private DZFDouble hjdqsbdkdgdzcjxse;
	// 合计-申报抵扣的固定资产进项税额累计
	@TaxExcelPos(row = 6, col = 2)
	private DZFDouble hjsbdkdgdzcjxselj;

	public DZFDouble getHgjkzzszyjkssbdkdgdzcjxselj() {
		return hgjkzzszyjkssbdkdgdzcjxselj;
	}

	public void setHgjkzzszyjkssbdkdgdzcjxselj(
			DZFDouble hgjkzzszyjkssbdkdgdzcjxselj) {
		this.hgjkzzszyjkssbdkdgdzcjxselj = hgjkzzszyjkssbdkdgdzcjxselj;
	}

	public DZFDouble getZzszyfpsbdkdgdzcjxselj() {
		return zzszyfpsbdkdgdzcjxselj;
	}

	public void setZzszyfpsbdkdgdzcjxselj(DZFDouble zzszyfpsbdkdgdzcjxselj) {
		this.zzszyfpsbdkdgdzcjxselj = zzszyfpsbdkdgdzcjxselj;
	}

	public DZFDouble getHgjkzzszyjksdqsbdkdgdzcjxse() {
		return hgjkzzszyjksdqsbdkdgdzcjxse;
	}

	public void setHgjkzzszyjksdqsbdkdgdzcjxse(
			DZFDouble hgjkzzszyjksdqsbdkdgdzcjxse) {
		this.hgjkzzszyjksdqsbdkdgdzcjxse = hgjkzzszyjksdqsbdkdgdzcjxse;
	}

	public DZFDouble getZzszyfpdqsbdkdgdzcjxse() {
		return zzszyfpdqsbdkdgdzcjxse;
	}

	public void setZzszyfpdqsbdkdgdzcjxse(DZFDouble zzszyfpdqsbdkdgdzcjxse) {
		this.zzszyfpdqsbdkdgdzcjxse = zzszyfpdqsbdkdgdzcjxse;
	}

	public DZFDouble getHjdqsbdkdgdzcjxse() {
		return hjdqsbdkdgdzcjxse;
	}

	public void setHjdqsbdkdgdzcjxse(DZFDouble hjdqsbdkdgdzcjxse) {
		this.hjdqsbdkdgdzcjxse = hjdqsbdkdgdzcjxse;
	}

	public DZFDouble getHjsbdkdgdzcjxselj() {
		return hjsbdkdgdzcjxselj;
	}

	public void setHjsbdkdgdzcjxselj(DZFDouble hjsbdkdgdzcjxselj) {
		this.hjsbdkdgdzcjxselj = hjsbdkdgdzcjxselj;
	}

}
