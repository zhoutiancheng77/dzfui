package com.dzf.zxkj.platform.model.sys;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SalaryModelHVO extends SuperVO<SalaryModelBVO> {
	@JsonProperty("id")
	private String pk_model_h;
	private String pk_corp;
	private String accountschemaname;
	@JsonProperty("hykmid")
	private String pk_trade_accountschema;
	private Integer temp_type;
	private Integer dr;
	private DZFDateTime ts;

	public String getPk_model_h() {
		return pk_model_h;
	}

	public void setPk_model_h(String pk_model_h) {
		this.pk_model_h = pk_model_h;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getAccountschemaname() {
		return accountschemaname;
	}

	public void setAccountschemaname(String accountschemaname) {
		this.accountschemaname = accountschemaname;
	}

	public String getPk_trade_accountschema() {
		return pk_trade_accountschema;
	}

	public void setPk_trade_accountschema(String pk_trade_accountschema) {
		this.pk_trade_accountschema = pk_trade_accountschema;
	}

	public Integer getTemp_type() {
		return temp_type;
	}

	public void setTemp_type(Integer temp_type) {
		this.temp_type = temp_type;
	}

	public Integer getDr() {
		return dr;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
	}

	public DZFDateTime getTs() {
		return ts;
	}

	public void setTs(DZFDateTime ts) {
		this.ts = ts;
	}

	public String getParentPKFieldName() {
		return null;
	}

	public String getPKFieldName() {
		return "pk_model_h";
	}

	public String getTableName() {
		return "ynt_salarymodel_h";
	}
}
