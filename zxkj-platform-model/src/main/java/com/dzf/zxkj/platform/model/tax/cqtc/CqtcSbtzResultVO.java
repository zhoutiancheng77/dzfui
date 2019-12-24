package com.dzf.zxkj.platform.model.tax.cqtc;

public class CqtcSbtzResultVO {
	private String sbzt; //申报状态
	
	private String szdm;//税种代码
	
	private String taxno;//纳税人识别号
	
	private String note;//备注说明

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getTaxno() {
		return taxno;
	}

	public void setTaxno(String taxno) {
		this.taxno = taxno;
	}

	public String getSbzt() {
		return sbzt;
	}

	public void setSbzt(String sbzt) {
		this.sbzt = sbzt;
	}

	public String getSzdm() {
		return szdm;
	}

	public void setSzdm(String szdm) {
		this.szdm = szdm;
	}
	
	
}
