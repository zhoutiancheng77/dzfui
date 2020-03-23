package com.dzf.zxkj.platform.model.zncs;

import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.model.SuperVO;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 智能财税
 * 白名单
 *
 *
 */

public class ZncswhiteVO extends SuperVO {
	@JsonProperty("id")
	private String pk_whitelist ;//主键
	@JsonProperty("pk_corp")
	private String pk_corp;//公司主键
	@JsonProperty("corpname")
	private  String corpname;//公司名称

	private String corpcode;//公司编码

	private String operator;//操作人主键

	private String operatorname;//操作人名称
	private Integer dr;
	private DZFDateTime ts;



	public String getPk_corp() {
		return pk_corp;
	}
	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}
	public String getCorpname() {
		return corpname;
	}
	public void setCorpname(String corpname) {
		this.corpname = corpname;
	}
	public String getCorpcode() {
		return corpcode;
	}
	public void setCorpcode(String corpcode) {
		this.corpcode = corpcode;
	}
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	public String getOperatorname() {
		return operatorname;
	}
	public void setOperatorname(String operatorname) {
		this.operatorname = operatorname;
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

	public String getPk_whitelist() {
		return pk_whitelist;
	}
	public void setPk_whitelist(String pk_whitelist) {
		this.pk_whitelist = pk_whitelist;
	}
	@Override
	public String getPKFieldName() {

		return "pk_whitelist";
	}
	@Override
	public String getParentPKFieldName() {

		return null;
	}
	@Override
	public String getTableName() {

		return "zncs_whitelist";
	}






}
