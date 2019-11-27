package com.dzf.zxkj.platform.model.jzcl;


import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.model.SuperVO;

public class YJJZSetVO extends SuperVO {
	
	private String pk_yjjzset;

	private String pk_corp;
	
	private String result1;//
	
	private DZFDateTime ts;
	
	private Integer dr;

	public String getPk_yjjzset() {
		return pk_yjjzset;
	}

	public void setPk_yjjzset(String pk_yjjzset) {
		this.pk_yjjzset = pk_yjjzset;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getResult1() {
		return result1;
	}

	public void setResult1(String result1) {
		this.result1 = result1;
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

	@Override
	public String getPKFieldName() {
		return "pk_yjjzset";
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return "ynt_yjjzset";
	}
	
}
