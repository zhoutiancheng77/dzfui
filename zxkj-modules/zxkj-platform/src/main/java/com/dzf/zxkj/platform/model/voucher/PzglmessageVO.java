package com.dzf.zxkj.platform.model.voucher;

import java.io.Serializable;

public class PzglmessageVO implements Serializable,Comparable<PzglmessageVO>{
	
	private String pzh = null;
	
	private String gsname = null;
	
	private String period = null;
	
	private String errorinfo = null;
	
	private String pk_corp = null;

	public String getPzh() {
		return pzh;
	}

	public void setPzh(String pzh) {
		this.pzh = pzh;
	}

	public String getGsname() {
		return gsname;
	}

	public void setGsname(String gsname) {
		this.gsname = gsname;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public String getErrorinfo() {
		return errorinfo;
	}

	public void setErrorinfo(String errorinfo) {
		this.errorinfo = errorinfo;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	@Override
	public int compareTo(PzglmessageVO vo) {
		String a1 = this.getPk_corp()+","+this.getPeriod()+","+this.getPzh();
		String a2 = vo.getPk_corp()+","+vo.getPeriod()+","+vo.getPzh();
		return a1.compareTo(a2);
	}
}
