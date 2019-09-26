package com.dzf.zxkj.platform.model.sys;

import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;

public class InvAccModelVO extends SuperVO {

	/**
	 * 存货科目关系模版
	 */
	private static final long serialVersionUID = -1872061792962345871L;
	@JsonProperty("id")
	private String pk_model;
	@JsonProperty("itype")
	private Integer itype; // 科目类型 0----- 采购业务科目 1------销售业务科目
	@JsonProperty("ictype")
	private Integer icolumntype;// 字段类型
	@JsonProperty("accmid")
	private String pk_trade_accountschema; // 科目方案
	private String accountschemaname;// 科目方案名称
	@JsonProperty("corpid")
	private String pk_corp; // 公司
	@JsonProperty("zy")
	private String zy;// 摘要
	@JsonProperty("bm")
	private String kmbm;// 科目编码
	@JsonProperty("mc")
	private String kmmc;// 科目名称
	@JsonProperty("accid")
	private String pk_accsubj;// 科目主键
	private Integer dr; // 删除标志
	private DZFDateTime ts;// 时间戳

	public String getPk_model() {
		return pk_model;
	}

	public void setPk_model(String pk_model) {
		this.pk_model = pk_model;
	}

	public Integer getItype() {
		return itype;
	}

	public void setItype(Integer itype) {
		this.itype = itype;
	}

	public Integer getIcolumntype() {
		return icolumntype;
	}

	public void setIcolumntype(Integer icolumntype) {
		this.icolumntype = icolumntype;
	}

	public String getPk_trade_accountschema() {
		return pk_trade_accountschema;
	}

	public void setPk_trade_accountschema(String pk_trade_accountschema) {
		this.pk_trade_accountschema = pk_trade_accountschema;
	}

	public String getAccountschemaname() {
		return accountschemaname;
	}

	public void setAccountschemaname(String accountschemaname) {
		this.accountschemaname = accountschemaname;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getZy() {
		return zy;
	}

	public void setZy(String zy) {
		this.zy = zy;
	}

	public String getKmbm() {
		return kmbm;
	}

	public void setKmbm(String kmbm) {
		this.kmbm = kmbm;
	}

	public String getKmmc() {
		return kmmc;
	}

	public void setKmmc(String kmmc) {
		this.kmmc = kmmc;
	}

	public String getPk_accsubj() {
		return pk_accsubj;
	}

	public void setPk_accsubj(String pk_accsubj) {
		this.pk_accsubj = pk_accsubj;
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

	@Override
	public String getPKFieldName() {
		return "pk_model";
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return "ynt_invaccmodel";
	}

}
