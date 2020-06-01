package com.dzf.zxkj.app.model.resp.bean;


import com.dzf.zxkj.common.lang.DZFBoolean;

public class UserLsBean extends ResponseBaseBeanVO {
	
	private String userid ;
	private String usercode;
	private String username ;
	private String regdate ;
	private String userstate ;
	private String tel;
	private String job;
	private String qq;
	private String email;
	private String usergrade;
	private DZFBoolean iaudituser;
	
	public DZFBoolean getIaudituser() {
		return iaudituser;
	}
	public void setIaudituser(DZFBoolean iaudituser) {
		this.iaudituser = iaudituser;
	}
	public String getUsergrade() {
		return usergrade;
	}
	public void setUsergrade(String usergrade) {
		this.usergrade = usergrade;
	}
	public String getJob() {
		return job;
	}
	public void setJob(String job) {
		this.job = job;
	}
	public String getQq() {
		return qq;
	}
	public void setQq(String qq) {
		this.qq = qq;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public String getUsercode() {
		return usercode;
	}
	public void setUsercode(String usercode) {
		this.usercode = usercode;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getRegdate() {
		return regdate;
	}
	public void setRegdate(String regdate) {
		this.regdate = regdate;
	}
	public String getUserstate() {
		return userstate;
	}
	public void setUserstate(String userstate) {
		this.userstate = userstate;
	}
	
}
