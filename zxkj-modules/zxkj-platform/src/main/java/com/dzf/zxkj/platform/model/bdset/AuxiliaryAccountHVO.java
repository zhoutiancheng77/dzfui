package com.dzf.zxkj.platform.model.bdset;

import com.dzf.zxkj.base.model.SuperVO;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 辅助核算主表
 * @author liubj
 *
 */
public class AuxiliaryAccountHVO extends SuperVO {
	@JsonProperty("id")
	private String pk_auacount_h;
	@JsonProperty("gsid")
	private String pk_corp;
	private String name;
	private Integer dr;
	private Integer code;
	
	public String getPk_auacount_h() {
		return pk_auacount_h;
	}
	public void setPk_auacount_h(String pk_auacount_h) {
		this.pk_auacount_h = pk_auacount_h;
	}
	public String getPk_corp() {
		return pk_corp;
	}
	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getDr() {
		return dr;
	}
	public void setDr(Integer dr) {
		this.dr = dr;
	}
	public Integer getCode() {
		return code;
	}
	public void setCode(Integer code) {
		this.code = code;
	}
	@Override
	public String getPKFieldName() {
		// TODO Auto-generated method stub
		return "pk_auacount_h";
	}
	@Override
	public String getParentPKFieldName() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return "ynt_fzhs_h";
	}	
}