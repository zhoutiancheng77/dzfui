package com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.incometax.a;


import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.TaxExcelPos;

@TaxExcelPos(reportID = "10412005", reportname = "A202000企业所得税汇总纳税分支机构所得税分配表", rowBegin = 8, rowEnd = 25, col = 1)
public class BranchIncomeTaxDetail {
	// 分支机构登记序号
	private String fzjgdjxh;
	// 分支机构类型类别
	private String fzjglxlb;

	// 纳税人识别号
	@TaxExcelPos(col = 1)
	private String fzjgnsrsbh;

	@TaxExcelPos(col = 2)
	// 分支机构名称
	private String fzjgmc;

	// 收入总额
	@TaxExcelPos(col = 3)
	private DZFDouble yysr;

	// 工资总额
	@TaxExcelPos(col = 4)
	private DZFDouble zgxc;

	// 资产总额
	@TaxExcelPos(col = 5)
	private DZFDouble zcze;

	// 分配比例
	@TaxExcelPos(col = 6)
	private Double fpbl;

	// 分支机构分配税额
	@TaxExcelPos(col = 7)
	private DZFDouble fpsdse;

	public String getFzjgdjxh() {
		return fzjgdjxh;
	}

	public void setFzjgdjxh(String fzjgdjxh) {
		this.fzjgdjxh = fzjgdjxh;
	}

	public String getFzjglxlb() {
		return fzjglxlb;
	}

	public void setFzjglxlb(String fzjglxlb) {
		this.fzjglxlb = fzjglxlb;
	}

	public String getFzjgnsrsbh() {
		return fzjgnsrsbh;
	}

	public void setFzjgnsrsbh(String fzjgnsrsbh) {
		this.fzjgnsrsbh = fzjgnsrsbh;
	}

	public String getFzjgmc() {
		return fzjgmc;
	}

	public void setFzjgmc(String fzjgmc) {
		this.fzjgmc = fzjgmc;
	}

	public DZFDouble getYysr() {
		return yysr;
	}

	public void setYysr(DZFDouble yysr) {
		this.yysr = yysr;
	}

	public DZFDouble getZgxc() {
		return zgxc;
	}

	public void setZgxc(DZFDouble zgxc) {
		this.zgxc = zgxc;
	}

	public DZFDouble getZcze() {
		return zcze;
	}

	public void setZcze(DZFDouble zcze) {
		this.zcze = zcze;
	}

	public Double getFpbl() {
		return fpbl;
	}

	public void setFpbl(Double fpbl) {
		this.fpbl = fpbl;
	}

	public DZFDouble getFpsdse() {
		return fpsdse;
	}

	public void setFpsdse(DZFDouble fpsdse) {
		this.fpsdse = fpsdse;
	}

}