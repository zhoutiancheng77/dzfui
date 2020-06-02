package com.dzf.zxkj.app.model.resp.bean;


import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 子系统登录
 * 
 * @author zhangj
 *
 */
public class SubCopMsgBeanVO extends ResponseBaseBeanVO {

	private String[] longitude;// 服务机构经纬度
	private String[] latitude;
	private String[] pk_svorg;// 服务机构主键信息
	private String user_name;// 用户名字
	private String tips;//提示信息

	private String corplongitude;// 公司经纬度
	private String corplatitude;

	
	private String job;
	private String app_user_mail;
	private String app_user_memo;
	private String app_user_qq;
	private String app_user_tel;

	private String photopath;

	@JsonProperty("tcorp") // 未签约的公司
	private String pk_tempcorp;//
	private String hximaccountid;// 环信账户ID
	private String hximpwd;// 环信账户PWD
	private String hxuuid;// 环信UUID
	private String srvcustid;// 聊天客服ID

	private String isaccorp;

	// 代账机构信息
	private String accname;
	private String acccode;
	private String accid;
	private String accfc;//代账机构是否封存0 封存，1 未封存

	private String account;

	private String usergrade;

	private String token;
	private String account_id;
	private String isdemo;
	
	
	public String getAccfc() {
		return accfc;
	}

	public void setAccfc(String accfc) {
		this.accfc = accfc;
	}

	public String getTips() {
		return tips;
	}

	public void setTips(String tips) {
		this.tips = tips;
	}

	public String getAcccode() {
		return acccode;
	}

	public void setAcccode(String acccode) {
		this.acccode = acccode;
	}

	public String getAccid() {
		return accid;
	}

	public void setAccid(String accid) {
		this.accid = accid;
	}

	public String[] getLongitude() {
		return longitude;
	}

	public void setLongitude(String[] longitude) {
		this.longitude = longitude;
	}

	public String[] getLatitude() {
		return latitude;
	}

	public String getAccname() {
		return accname;
	}

	public void setAccname(String accname) {
		this.accname = accname;
	}

	public String getIsaccorp() {
		return isaccorp;
	}

	public void setIsaccorp(String isaccorp) {
		this.isaccorp = isaccorp;
	}

	public void setLatitude(String[] latitude) {
		this.latitude = latitude;
	}

	public String[] getPk_svorg() {
		return pk_svorg;
	}

	public void setPk_svorg(String[] pk_svorg) {
		this.pk_svorg = pk_svorg;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getCorplongitude() {
		return corplongitude;
	}

	public void setCorplongitude(String corplongitude) {
		this.corplongitude = corplongitude;
	}

	public String getCorplatitude() {
		return corplatitude;
	}

	public void setCorplatitude(String corplatitude) {
		this.corplatitude = corplatitude;
	}

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}

	public String getApp_user_mail() {
		return app_user_mail;
	}

	public void setApp_user_mail(String app_user_mail) {
		this.app_user_mail = app_user_mail;
	}

	public String getApp_user_memo() {
		return app_user_memo;
	}

	public void setApp_user_memo(String app_user_memo) {
		this.app_user_memo = app_user_memo;
	}

	public String getApp_user_qq() {
		return app_user_qq;
	}

	public void setApp_user_qq(String app_user_qq) {
		this.app_user_qq = app_user_qq;
	}

	public String getApp_user_tel() {
		return app_user_tel;
	}

	public void setApp_user_tel(String app_user_tel) {
		this.app_user_tel = app_user_tel;
	}

	public String getPhotopath() {
		return photopath;
	}

	public void setPhotopath(String photopath) {
		this.photopath = photopath;
	}

	public String getHximaccountid() {
		return hximaccountid;
	}

	public void setHximaccountid(String hximaccountid) {
		this.hximaccountid = hximaccountid;
	}

	public String getHximpwd() {
		return hximpwd;
	}

	public void setHximpwd(String hximpwd) {
		this.hximpwd = hximpwd;
	}

	public String getHxuuid() {
		return hxuuid;
	}

	public void setHxuuid(String hxuuid) {
		this.hxuuid = hxuuid;
	}

	public String getSrvcustid() {
		return srvcustid;
	}

	public void setSrvcustid(String srvcustid) {
		this.srvcustid = srvcustid;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getUsergrade() {
		return usergrade;
	}

	public void setUsergrade(String usergrade) {
		this.usergrade = usergrade;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getAccount_id() {
		return account_id;
	}

	public void setAccount_id(String account_id) {
		this.account_id = account_id;
	}

	public String getIsdemo() {
		return isdemo;
	}

	public void setIsdemo(String isdemo) {
		this.isdemo = isdemo;
	}

	public String getPk_tempcorp() {
		return pk_tempcorp;
	}

	public void setPk_tempcorp(String pk_tempcorp) {
		this.pk_tempcorp = pk_tempcorp;
	}

}
