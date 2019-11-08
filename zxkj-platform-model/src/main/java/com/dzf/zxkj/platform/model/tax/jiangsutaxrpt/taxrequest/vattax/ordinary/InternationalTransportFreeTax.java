package com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.vattax.ordinary;

import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.TaxExcelPos;

//国际运输征免税明细数据 sb10101018vo_01
@TaxExcelPos(reportID = "10101018", reportname = "国际运输征免税明细数据", rowBegin = 6, rowEnd = 25, col = 1)
public class InternationalTransportFreeTax {
	// 申报序号
	@TaxExcelPos(col = 0)
	private String sbxh;

	// 应税服务代码
	@TaxExcelPos(col = 1)
	private String ysfwdm;

	// 应税服务名称
	@TaxExcelPos(col = 2)
	private String ysfwmc;

	// 计量单位
	@TaxExcelPos(col = 3)
	private String jldw;

	// 本期运输次数
	@TaxExcelPos(col = 4)
	private DZFDouble bqyscs;

	// 自运舱单份数
	@TaxExcelPos(col = 5)
	private DZFDouble zycdfs;

	// 自运提单（运单）份数或载客人数
	@TaxExcelPos(col = 6)
	private DZFDouble zytdfshzkrs;

	// 折人民币
	@TaxExcelPos(col = 7)
	private DZFDouble zrmb;

	// 支付给非试点纳税人价款
	@TaxExcelPos(col = 8)
	private DZFDouble zfgfsdnsrjk;

	// 免抵退税计税金额
	@TaxExcelPos(col = 9)
	private DZFDouble mdtsjsje;

	// 征税率
	@TaxExcelPos(col = 10)
	private DZFDouble zsl;

	// 退税率
	@TaxExcelPos(col = 11)
	private DZFDouble tsl;

	// 应税服务免抵退税计税金额乘征退税率之差
	@TaxExcelPos(col = 12)
	private DZFDouble ysfwydtsjsjecztslzc;

	// 应税服务免抵退税计税金额乘退税额
	@TaxExcelPos(col = 13)
	private DZFDouble ysfwmd;

	public String getSbxh() {
		return sbxh;
	}

	public void setSbxh(String sbxh) {
		this.sbxh = sbxh;
	}

	public String getYsfwdm() {
		return ysfwdm;
	}

	public void setYsfwdm(String ysfwdm) {
		this.ysfwdm = ysfwdm;
	}

	public String getYsfwmc() {
		return ysfwmc;
	}

	public void setYsfwmc(String ysfwmc) {
		this.ysfwmc = ysfwmc;
	}

	public String getJldw() {
		return jldw;
	}

	public void setJldw(String jldw) {
		this.jldw = jldw;
	}

	public DZFDouble getBqyscs() {
		return bqyscs;
	}

	public void setBqyscs(DZFDouble bqyscs) {
		this.bqyscs = bqyscs;
	}

	public DZFDouble getZycdfs() {
		return zycdfs;
	}

	public void setZycdfs(DZFDouble zycdfs) {
		this.zycdfs = zycdfs;
	}

	public DZFDouble getZytdfshzkrs() {
		return zytdfshzkrs;
	}

	public void setZytdfshzkrs(DZFDouble zytdfshzkrs) {
		this.zytdfshzkrs = zytdfshzkrs;
	}

	public DZFDouble getZrmb() {
		return zrmb;
	}

	public void setZrmb(DZFDouble zrmb) {
		this.zrmb = zrmb;
	}

	public DZFDouble getZfgfsdnsrjk() {
		return zfgfsdnsrjk;
	}

	public void setZfgfsdnsrjk(DZFDouble zfgfsdnsrjk) {
		this.zfgfsdnsrjk = zfgfsdnsrjk;
	}

	public DZFDouble getMdtsjsje() {
		return mdtsjsje;
	}

	public void setMdtsjsje(DZFDouble mdtsjsje) {
		this.mdtsjsje = mdtsjsje;
	}

	public DZFDouble getZsl() {
		return zsl;
	}

	public void setZsl(DZFDouble zsl) {
		this.zsl = zsl;
	}

	public DZFDouble getTsl() {
		return tsl;
	}

	public void setTsl(DZFDouble tsl) {
		this.tsl = tsl;
	}

	public DZFDouble getYsfwydtsjsjecztslzc() {
		return ysfwydtsjsjecztslzc;
	}

	public void setYsfwydtsjsjecztslzc(DZFDouble ysfwydtsjsjecztslzc) {
		this.ysfwydtsjsjecztslzc = ysfwydtsjsjecztslzc;
	}

	public DZFDouble getYsfwmd() {
		return ysfwmd;
	}

	public void setYsfwmd(DZFDouble ysfwmd) {
		this.ysfwmd = ysfwmd;
	}

}
