package com.dzf.zxkj.platform.model.jzcl;

import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.model.SuperVO;
import com.fasterxml.jackson.annotation.JsonProperty;

public class YjjzVO extends SuperVO {
	
	@JsonProperty("pk_id")
	private String pk_qmcl;
	@JsonProperty("id")
	private String pk_yjjz;
	@JsonProperty("qj")
	private String period;
	@JsonProperty("pk_gs")
	private String pk_corp; 
	private String corpname;
	@JsonProperty("jz")
	private DZFBoolean isjz;
	@JsonProperty("ts")
	private DZFDateTime ts;
	private Integer dr;
	
	private DZFBoolean ikc;//是否库存--->后台查询已赋值
	private DZFBoolean iwb;//是否外币-->没有
	private DZFBoolean igdzc;//是否固定资产--->后台查询已赋值
	private DZFBoolean isgz;//是否关账--->没有
	private DZFBoolean isybr;//是否一般人--->后台查询已赋值
	private DZFDate jzdate;//当前qmvo的pk_corp建账日期--->后台查询已赋值

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public DZFBoolean getIsjz() {
		return isjz;
	}

	public void setIsjz(DZFBoolean isjz) {
		this.isjz = isjz;
	}

	public DZFDateTime getTs() {
		return ts;
	}

	public void setTs(DZFDateTime ts) {
		this.ts = ts;
	}

	public Integer getDr() {
		return dr;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
	}
	
	public String getCorpname() {
		return corpname;
	}

	public void setCorpname(String corpname) {
		this.corpname = corpname;
	}

	public String getPk_yjjz() {
		return pk_yjjz;
	}

	public void setPk_yjjz(String pk_yjjz) {
		this.pk_yjjz = pk_yjjz;
	}

	public String getPk_qmcl() {
		return pk_qmcl;
	}

	public void setPk_qmcl(String pk_qmcl) {
		this.pk_qmcl = pk_qmcl;
	}

	public DZFBoolean getIkc() {
		return ikc;
	}

	public void setIkc(DZFBoolean ikc) {
		this.ikc = ikc;
	}

	public DZFBoolean getIwb() {
		return iwb;
	}

	public void setIwb(DZFBoolean iwb) {
		this.iwb = iwb;
	}

	public DZFBoolean getIgdzc() {
		return igdzc;
	}

	public void setIgdzc(DZFBoolean igdzc) {
		this.igdzc = igdzc;
	}

	public DZFBoolean getIsgz() {
		return isgz;
	}

	public void setIsgz(DZFBoolean isgz) {
		this.isgz = isgz;
	}

	public DZFBoolean getIsybr() {
		return isybr;
	}

	public void setIsybr(DZFBoolean isybr) {
		this.isybr = isybr;
	}

	public DZFDate getJzdate() {
		return jzdate;
	}

	public void setJzdate(DZFDate jzdate) {
		this.jzdate = jzdate;
	}

	@Override
	public String getPKFieldName() {
		return "pk_yjjz";
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return "ynt_yjjz";
	}
}
