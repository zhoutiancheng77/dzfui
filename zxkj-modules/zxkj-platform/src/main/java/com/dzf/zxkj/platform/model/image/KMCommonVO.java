package com.dzf.zxkj.platform.model.image;

import com.dzf.zxkj.base.model.SuperVO;

public class KMCommonVO extends SuperVO {
	
	
	private String id;
	
	private String code;
	
	private String name;
	
	private String pk_corp;
	
	private String pk_accschema;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getPk_accschema() {
		return pk_accschema;
	}

	public void setPk_accschema(String pk_accschema) {
		this.pk_accschema = pk_accschema;
	}

	@Override
	public String getPKFieldName() {
		return null;
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return null;
	}

}
