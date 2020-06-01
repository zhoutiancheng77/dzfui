package com.dzf.zxkj.app.model.resp.bean;


import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 机构相关信息
 * @author zhangj
 *
 */
public class ContainCorpVo {

	private String cname;//公司名字
	private String ccode;//公司编码
	@JsonProperty("ph")
	private String phone;//公司编码
	
	private String accountname;//机构名字
	
	@JsonProperty("corp")
	private String pk_corp ;//机构主键
	
	
	public String getCname() {
		return cname;
	}
	public void setCname(String cname) {
		this.cname = cname;
	}
	public String getCcode() {
		return ccode;
	}
	public void setCcode(String ccode) {
		this.ccode = ccode;
	}
	public String getAccountname() {
		return accountname;
	}
	public void setAccountname(String accountname) {
		this.accountname = accountname;
	}
	public String getPk_corp() {
		return pk_corp;
	}
	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
}
