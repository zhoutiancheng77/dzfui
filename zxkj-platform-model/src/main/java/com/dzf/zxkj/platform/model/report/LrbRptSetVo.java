package com.dzf.zxkj.platform.model.report;

import com.dzf.zxkj.common.model.SuperVO;

public class LrbRptSetVo extends SuperVO {

	public static final String TABLE_NAME = "ynt_lrb_set";

	public static final String PK_FIELD = "pk_lrb_set";

	private String	pk_lrb_set;//主键
	private String	xm;//项目名称
	private String	hc;//行次
	private String hc_id;//不可编辑
	private String	km;//科目取数
	private String	xm2;//项目名称
	private String	hc2;//行次
	private String	km2;//科目取数
	private String	pk_corp;//公司
	private Integer	ordernum;//序号
	private String pk_trade_accountschema;// 行业
	private Integer ilevel;//层级
	
	public String getHc_id() {
		return hc_id;
	}

	public void setHc_id(String hc_id) {
		this.hc_id = hc_id;
	}

	public String getXm2() {
		return xm2;
	}

	public void setXm2(String xm2) {
		this.xm2 = xm2;
	}

	public String getHc2() {
		return hc2;
	}

	public void setHc2(String hc2) {
		this.hc2 = hc2;
	}

	public String getKm2() {
		return km2;
	}

	public void setKm2(String km2) {
		this.km2 = km2;
	}

	public Integer getIlevel() {
		return ilevel;
	}

	public void setIlevel(Integer ilevel) {
		this.ilevel = ilevel;
	}

	public String getPk_trade_accountschema() {
		return pk_trade_accountschema;
	}

	public void setPk_trade_accountschema(String pk_trade_accountschema) {
		this.pk_trade_accountschema = pk_trade_accountschema;
	}

	public String getPk_lrb_set() {
		return pk_lrb_set;
	}

	public void setPk_lrb_set(String pk_lrb_set) {
		this.pk_lrb_set = pk_lrb_set;
	}

	public String getXm() {
		return xm;
	}

	public void setXm(String xm) {
		this.xm = xm;
	}

	public String getHc() {
		return hc;
	}

	public void setHc(String hc) {
		this.hc = hc;
	}

	public String getKm() {
		return km;
	}

	public void setKm(String km) {
		this.km = km;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public Integer getOrdernum() {
		return ordernum;
	}

	public void setOrdernum(Integer ordernum) {
		this.ordernum = ordernum;
	}

	@Override
	public String getPKFieldName() {
		return PK_FIELD;
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return TABLE_NAME;
	}
	
	
	

}
