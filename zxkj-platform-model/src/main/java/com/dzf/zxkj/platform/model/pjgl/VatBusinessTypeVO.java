package com.dzf.zxkj.platform.model.pjgl;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;

public class VatBusinessTypeVO extends SuperVO {

	@JsonProperty("id")
	private String pk_vatbusitype;
	private String stype;//类型
	private String businame;//业务类型名称
	private String selectvalue;//勾选的计算方式
	@JsonProperty("cid")
	private String coperatorid;//操作人
	@JsonProperty("ddate")
	private DZFDate doperatedate;//操作日期
	@JsonProperty("corpid")
	private String pk_corp;//公司pk
	private int dr;
	private DZFDateTime ts;
	
	private String showvalue;//展示的结算方式
	
	public String getPk_vatbusitype() {
		return pk_vatbusitype;
	}

	public String getCoperatorid() {
		return coperatorid;
	}

	public DZFDate getDoperatedate() {
		return doperatedate;
	}

	public String getBusiname() {
		return businame;
	}

	public void setBusiname(String businame) {
		this.businame = businame;
	}

	public String getStype() {
		return stype;
	}

	public void setStype(String stype) {
		this.stype = stype;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public int getDr() {
		return dr;
	}

	public DZFDateTime getTs() {
		return ts;
	}

	public String getShowvalue() {
		return showvalue;
	}

	public void setPk_vatbusitype(String pk_vatbusitype) {
		this.pk_vatbusitype = pk_vatbusitype;
	}

	public String getSelectvalue() {
		return selectvalue;
	}

	public void setSelectvalue(String selectvalue) {
		this.selectvalue = selectvalue;
	}

	public void setCoperatorid(String coperatorid) {
		this.coperatorid = coperatorid;
	}

	public void setDoperatedate(DZFDate doperatedate) {
		this.doperatedate = doperatedate;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public void setDr(int dr) {
		this.dr = dr;
	}

	public void setTs(DZFDateTime ts) {
		this.ts = ts;
	}

	public void setShowvalue(String showvalue) {
		this.showvalue = showvalue;
	}

	@Override
	public String getPKFieldName() {
		return "pk_vatbusitype";
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return "ynt_vatbusitype";
	}

}
