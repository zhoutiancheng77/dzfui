package com.dzf.zxkj.platform.model.pzgl;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDateTime;

/**
 * 凭证关联关系表
 * 
 * @author lbj
 * 
 */
public class PzSourceRelationVO extends SuperVO {
	private String pk_relation;
	// 凭证ID
	private String pk_tzpz_h;
	// 来源
	private String sourcebilltype;
	// 来源ID
	private String sourcebillid;
	private String pk_corp;
	private Integer dr;
	private DZFDateTime ts;

	public String getPk_relation() {
		return pk_relation;
	}

	public void setPk_relation(String pk_relation) {
		this.pk_relation = pk_relation;
	}

	public String getPk_tzpz_h() {
		return pk_tzpz_h;
	}

	public void setPk_tzpz_h(String pk_tzpz_h) {
		this.pk_tzpz_h = pk_tzpz_h;
	}

	public String getSourcebilltype() {
		return sourcebilltype;
	}

	public void setSourcebilltype(String sourcebilltype) {
		this.sourcebilltype = sourcebilltype;
	}

	public String getSourcebillid() {
		return sourcebillid;
	}

	public void setSourcebillid(String sourcebillid) {
		this.sourcebillid = sourcebillid;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
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
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getPKFieldName() {
		return "pk_relation";
	}

	@Override
	public String getTableName() {
		return "ynt_pz_sourcerelation";
	}

}
