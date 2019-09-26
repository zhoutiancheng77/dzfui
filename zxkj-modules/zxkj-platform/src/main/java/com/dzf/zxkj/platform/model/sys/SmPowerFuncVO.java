package com.dzf.zxkj.platform.model.sys;

import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SmPowerFuncVO extends SuperVO {

	
	private String pk_power;
	private String pk_role;
	private String resource_data_id;
	private Integer orgtypecode;
	private String pk_org;
	private DZFBoolean iscommon_power;
	private String pk_corp;
	
	@JsonProperty("mname")
    private String module;//---这个字段不存库
	
    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getPk_power() {
		return pk_power;
	}

	public void setPk_power(String pk_power) {
		this.pk_power = pk_power;
	}

	public String getPk_role() {
		return pk_role;
	}

	public void setPk_role(String pk_role) {
		this.pk_role = pk_role;
	}

	public String getResource_data_id() {
		return resource_data_id;
	}

	public void setResource_data_id(String resource_data_id) {
		this.resource_data_id = resource_data_id;
	}

	public Integer getOrgtypecode() {
		return orgtypecode;
	}

	public void setOrgtypecode(Integer orgtypecode) {
		this.orgtypecode = orgtypecode;
	}

	public String getPk_org() {
		return pk_org;
	}

	public void setPk_org(String pk_org) {
		this.pk_org = pk_org;
	}

	public DZFBoolean getIscommon_power() {
		return iscommon_power;
	}

	public void setIscommon_power(DZFBoolean iscommon_power) {
		this.iscommon_power = iscommon_power;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getPKFieldName() {
		return "pk_power";
	}

	@Override
	public String getTableName() {
		return "sm_power_func";
	}

}
