package com.dzf.zxkj.platform.model.tax.cqtc;

/**
 * 用于参数message解析
 * @author shiyan
 *
 */
public class CqtcMessageVO {
	private String pzzl;//凭证种类代码
	
	private String periodfrom;//区间开始时间
	
	private String periodto;//区间结束时间
	
	private String bb;//申报报表信息

	public String getPzzl() {
		return pzzl;
	}

	public void setPzzl(String pzzl) {
		this.pzzl = pzzl;
	}

	public String getPeriodfrom() {
		return periodfrom;
	}

	public void setPeriodfrom(String periodfrom) {
		this.periodfrom = periodfrom;
	}

	public String getPeriodto() {
		return periodto;
	}

	public void setPeriodto(String periodto) {
		this.periodto = periodto;
	}

	public String getBb() {
		return bb;
	}

	public void setBb(String bb) {
		this.bb = bb;
	}
	
}
