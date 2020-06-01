package com.dzf.zxkj.app.model.app.user;


import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.model.SuperVO;

public class TempUserVO extends SuperVO {

	private String pk_user;
	private String pk_corp;
	private String pk_temp_corp;
	private DZFBoolean ismanager;
	private String usercode;
	private String password;
	private String phone;
	private String username;
	private DZFDateTime registertm;
	private String memo;
	private DZFDateTime ts;
	private Integer dr;
	private Integer state;
	private String corpname;
	
	
	public String getCorpname() {
		return corpname;
	}
	public void setCorpname(String corpname) {
		this.corpname = corpname;
	}
	public Integer getState() {
		return state;
	}
	public void setState(Integer state) {
		this.state = state;
	}
	public String getPk_corp() {
		return pk_corp;
	}
	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}
	public String getPk_temp_corp() {
		return pk_temp_corp;
	}
	public void setPk_temp_corp(String pk_temp_corp) {
		this.pk_temp_corp = pk_temp_corp;
	}
	public DZFBoolean getIsmanager() {
		return ismanager;
	}
	public void setIsmanager(DZFBoolean ismanager) {
		this.ismanager = ismanager;
	}
	public String getPk_user() {
		return pk_user;
	}
	public void setPk_user(String pk_user) {
		this.pk_user = pk_user;
	}
	
	public String getUsercode() {
		return usercode;
	}
	public void setUsercode(String usercode) {
		this.usercode = usercode;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public DZFDateTime getRegistertm() {
		return registertm;
	}
	public void setRegistertm(DZFDateTime registertm) {
		this.registertm = registertm;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
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
		return "pk_user";
	}
	@Override
	public String getTableName() {
		return "app_registeruser";
	}
	@Override
	public String getParentPKFieldName() {
		// TODO Auto-generated method stub
		return null;
	}
}
