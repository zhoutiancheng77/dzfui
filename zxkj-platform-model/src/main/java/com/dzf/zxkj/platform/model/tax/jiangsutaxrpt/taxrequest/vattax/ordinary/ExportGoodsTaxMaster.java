package com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.vattax.ordinary;

import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.TaxExcelPos;

//生产企业出口货物征（免）税明细主表  sb10101016vo_01
@TaxExcelPos(reportID = "10101016", reportname = "生产企业出口货物征（免）税明细主表 ", rowBegin = 6, rowEnd = 25, col = 1)
public class ExportGoodsTaxMaster {
	// 申报序号
	@TaxExcelPos(col = 0)
	private String sbxh;

	// 出口发票号码
	@TaxExcelPos(col = 1)
	private String ckfphm;

	// 出口日期
	@TaxExcelPos(col = 2)
	private String ckrq;

	// 贸易性质
	@TaxExcelPos(col = 3)
	private String myxz;

	// 人民币金额
	@TaxExcelPos(col = 4)
	private DZFDouble rmbje;

	// 记账凭证号
	@TaxExcelPos(col = 5)
	private String jzpzh;

	public String getSbxh() {
		return sbxh;
	}

	public void setSbxh(String sbxh) {
		this.sbxh = sbxh;
	}

	public String getCkfphm() {
		return ckfphm;
	}

	public void setCkfphm(String ckfphm) {
		this.ckfphm = ckfphm;
	}

	public String getCkrq() {
		return ckrq;
	}

	public void setCkrq(String ckrq) {
		this.ckrq = ckrq;
	}

	public String getMyxz() {
		return myxz;
	}

	public void setMyxz(String myxz) {
		this.myxz = myxz;
	}

	public DZFDouble getRmbje() {
		return rmbje;
	}

	public void setRmbje(DZFDouble rmbje) {
		this.rmbje = rmbje;
	}

	public String getJzpzh() {
		return jzpzh;
	}

	public void setJzpzh(String jzpzh) {
		this.jzpzh = jzpzh;
	}

}
