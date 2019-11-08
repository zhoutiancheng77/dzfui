package com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.incometax.a;

import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.TaxExcelPos;

@TaxExcelPos(reportID = "10412005", reportname = "A202000企业所得税汇总纳税分支机构所得税分配表")
public class BranchIncomeTaxMain {
	// 总机构名称
	@TaxExcelPos(row = 2, col = 2)
	private String zjgnsrmc;
	// 总机构纳税人识别号
	@TaxExcelPos(row = 3, col = 2)
	private String zjgnsrsbh;

	// 应纳税所得额
	@TaxExcelPos(row = 5, col = 1)
	private DZFDouble zjgynsdse;
	// 总机构分摊所得税额
	@TaxExcelPos(row = 5, col = 2)
	private DZFDouble zjgftsdse;
	// 总机构财政集中分配所得税额
	@TaxExcelPos(row = 5, col = 3)
	private DZFDouble zjgczjzfpsdse;
	// 分支机构分摊的所得税额
	@TaxExcelPos(row = 5, col = 6)
	private DZFDouble fzjgftsdse;

	// 营业收入
	@TaxExcelPos(row = 34, col = 3)
	private DZFDouble fzjgyysrhj;
	// 职工薪酬
	@TaxExcelPos(row = 34, col = 4)
	private DZFDouble fzjgzgxchj;
	// 资产总额
	@TaxExcelPos(row = 34, col = 5)
	private DZFDouble fzjgzczehj;
	// 分配比例
	@TaxExcelPos(row = 34, col = 6)
	private DZFDouble fzjgfpblhj;
	// 分配所得税额
	@TaxExcelPos(row = 34, col = 7)
	private DZFDouble fzjgfpsdehj;

	public String getZjgnsrmc() {
		return zjgnsrmc;
	}

	public void setZjgnsrmc(String zjgnsrmc) {
		this.zjgnsrmc = zjgnsrmc;
	}

	public String getZjgnsrsbh() {
		return zjgnsrsbh;
	}

	public void setZjgnsrsbh(String zjgnsrsbh) {
		this.zjgnsrsbh = zjgnsrsbh;
	}

	public DZFDouble getZjgynsdse() {
		return zjgynsdse;
	}

	public void setZjgynsdse(DZFDouble zjgynsdse) {
		this.zjgynsdse = zjgynsdse;
	}

	public DZFDouble getZjgftsdse() {
		return zjgftsdse;
	}

	public void setZjgftsdse(DZFDouble zjgftsdse) {
		this.zjgftsdse = zjgftsdse;
	}

	public DZFDouble getZjgczjzfpsdse() {
		return zjgczjzfpsdse;
	}

	public void setZjgczjzfpsdse(DZFDouble zjgczjzfpsdse) {
		this.zjgczjzfpsdse = zjgczjzfpsdse;
	}

	public DZFDouble getFzjgftsdse() {
		return fzjgftsdse;
	}

	public void setFzjgftsdse(DZFDouble fzjgftsdse) {
		this.fzjgftsdse = fzjgftsdse;
	}

	public DZFDouble getFzjgyysrhj() {
		return fzjgyysrhj;
	}

	public void setFzjgyysrhj(DZFDouble fzjgyysrhj) {
		this.fzjgyysrhj = fzjgyysrhj;
	}

	public DZFDouble getFzjgzgxchj() {
		return fzjgzgxchj;
	}

	public void setFzjgzgxchj(DZFDouble fzjgzgxchj) {
		this.fzjgzgxchj = fzjgzgxchj;
	}

	public DZFDouble getFzjgzczehj() {
		return fzjgzczehj;
	}

	public void setFzjgzczehj(DZFDouble fzjgzczehj) {
		this.fzjgzczehj = fzjgzczehj;
	}

	public DZFDouble getFzjgfpblhj() {
		return fzjgfpblhj;
	}

	public void setFzjgfpblhj(DZFDouble fzjgfpblhj) {
		this.fzjgfpblhj = fzjgfpblhj;
	}

	public DZFDouble getFzjgfpsdehj() {
		return fzjgfpsdehj;
	}

	public void setFzjgfpsdehj(DZFDouble fzjgfpsdehj) {
		this.fzjgfpsdehj = fzjgfpsdehj;
	}

}