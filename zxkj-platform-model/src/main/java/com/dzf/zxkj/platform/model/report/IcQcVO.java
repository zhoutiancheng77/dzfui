package com.dzf.zxkj.platform.model.report;


import com.dzf.zxkj.platform.model.icset.IcbalanceVO;

public class IcQcVO extends IcbalanceVO {
	
	private String pk_accsubj;//科目主键
	private String kmbm;
	private String km;
	private String spflid;//商品分类id
	private String spfl;//商品分类名称
	private String spflcode;//商品分类编码
	private String spgg;
	private String spxh;
	private String spbm;
	private String spmc;
	public String getPk_accsubj() {
		return pk_accsubj;
	}
	public void setPk_accsubj(String pk_accsubj) {
		this.pk_accsubj = pk_accsubj;
	}
	public String getKmbm() {
		return kmbm;
	}
	public void setKmbm(String kmbm) {
		this.kmbm = kmbm;
	}
	public String getKm() {
		return km;
	}
	public void setKm(String km) {
		this.km = km;
	}
	
	public String getSpflid() {
		return spflid;
	}
	public String getSpfl() {
		return spfl;
	}
	public String getSpflcode() {
		return spflcode;
	}
	public void setSpflid(String spflid) {
		this.spflid = spflid;
	}
	public void setSpfl(String spfl) {
		this.spfl = spfl;
	}
	public void setSpflcode(String spflcode) {
		this.spflcode = spflcode;
	}
	public String getSpgg() {
		return spgg;
	}
	public void setSpgg(String spgg) {
		this.spgg = spgg;
	}
	public String getSpxh() {
		return spxh;
	}
	public void setSpxh(String spxh) {
		this.spxh = spxh;
	}
	public String getSpbm() {
		return spbm;
	}
	public void setSpbm(String spbm) {
		this.spbm = spbm;
	}
	public String getSpmc() {
		return spmc;
	}
	public void setSpmc(String spmc) {
		this.spmc = spmc;
	}
	
}
