package com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.vattax.ordinary;

import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.TaxExcelPos;

//生产企业出口货物征（免）税明细从表 sb10101017vo_01
@TaxExcelPos(reportID = "10101017", reportname = "生产企业出口货物征（免）税明细从表", rowBegin = 6, rowEnd = 25, col = 1)
public class ExportGoodsTaxSlave {
	// 申报序号
	@TaxExcelPos(col = 0)
	private String sbxh;

	// 报关单号码
	@TaxExcelPos(col = 1)
	private String bgdhm;

	// 代理出口证明单
	@TaxExcelPos(col = 2)
	private String dlckzmd;

	// 商品名称
	@TaxExcelPos(col = 3)
	private String spmc;

	// 计量单位
	@TaxExcelPos(col = 4)
	private String jldw;

	// 出口数量
	@TaxExcelPos(col = 5)
	private DZFDouble cksl;

	// 报关单金额
	@TaxExcelPos(col = 6)
	private DZFDouble bgdje;

	// 出口发票金额
	@TaxExcelPos(col = 7)
	private DZFDouble ckfpje;

	// 征税率
	@TaxExcelPos(col = 8)
	private DZFDouble zsl;

	// 退税率
	@TaxExcelPos(col = 9)
	private DZFDouble tsl;

	// 不予抵扣税额
	@TaxExcelPos(col = 10)
	private DZFDouble bydkse;

	// 应免抵退税额
	@TaxExcelPos(col = 11)
	private DZFDouble ymdtse;

	// 应抵退抵减额
	@TaxExcelPos(col = 12)
	private DZFDouble ydtdje;

	// 实际免抵退税额
	@TaxExcelPos(col = 13)
	private DZFDouble sjmdtse;

	public String getSbxh() {
		return sbxh;
	}

	public void setSbxh(String sbxh) {
		this.sbxh = sbxh;
	}

	public String getBgdhm() {
		return bgdhm;
	}

	public void setBgdhm(String bgdhm) {
		this.bgdhm = bgdhm;
	}

	public String getDlckzmd() {
		return dlckzmd;
	}

	public void setDlckzmd(String dlckzmd) {
		this.dlckzmd = dlckzmd;
	}

	public String getSpmc() {
		return spmc;
	}

	public void setSpmc(String spmc) {
		this.spmc = spmc;
	}

	public String getJldw() {
		return jldw;
	}

	public void setJldw(String jldw) {
		this.jldw = jldw;
	}

	public DZFDouble getCksl() {
		return cksl;
	}

	public void setCksl(DZFDouble cksl) {
		this.cksl = cksl;
	}

	public DZFDouble getBgdje() {
		return bgdje;
	}

	public void setBgdje(DZFDouble bgdje) {
		this.bgdje = bgdje;
	}

	public DZFDouble getCkfpje() {
		return ckfpje;
	}

	public void setCkfpje(DZFDouble ckfpje) {
		this.ckfpje = ckfpje;
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

	public DZFDouble getBydkse() {
		return bydkse;
	}

	public void setBydkse(DZFDouble bydkse) {
		this.bydkse = bydkse;
	}

	public DZFDouble getYmdtse() {
		return ymdtse;
	}

	public void setYmdtse(DZFDouble ymdtse) {
		this.ymdtse = ymdtse;
	}

	public DZFDouble getYdtdje() {
		return ydtdje;
	}

	public void setYdtdje(DZFDouble ydtdje) {
		this.ydtdje = ydtdje;
	}

	public DZFDouble getSjmdtse() {
		return sjmdtse;
	}

	public void setSjmdtse(DZFDouble sjmdtse) {
		this.sjmdtse = sjmdtse;
	}

}
