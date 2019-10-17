package com.dzf.zxkj.platform.model.report;


import com.dzf.zxkj.common.model.SuperVO;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 账龄
 * @author lbj
 *
 */
public class AccountAgeVO extends SuperVO {

	@JsonProperty("id")
	private String pk_age;
	//账龄类型
	@JsonProperty("type")
	private Integer age_type;
	//编号
	private String code;
	private String name;
	@JsonProperty("corp")
	private String pk_corp;
	//单位天数
	private Integer days;
	
	private Integer dr;
	

	public String getPk_age() {
		return pk_age;
	}

	public void setPk_age(String pk_age) {
		this.pk_age = pk_age;
	}

	public Integer getAge_type() {
		return age_type;
	}

	public void setAge_type(Integer age_type) {
		this.age_type = age_type;
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

	public Integer getDays() {
		return days;
	}

	public void setDays(Integer days) {
		this.days = days;
	}

	public Integer getDr() {
		return dr;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
	}

	@Override
	public String getPKFieldName() {
		return "pk_age";
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return "sys_account_age";
	}

}
