package com.dzf.zxkj.platform.model.tax;

public class Taxclassifyheadinfo implements java.io.Serializable{
	private String reqsys;
	private String reqtime;
	private String reqno;
	private String authkey;
	private String reqauthkey;
	public String getReqsys() {
		return reqsys;
	}
	public void setReqsys(String reqsys) {
		this.reqsys = reqsys;
	}
	public String getReqtime() {
		return reqtime;
	}
	public void setReqtime(String reqtime) {
		this.reqtime = reqtime;
	}
	public String getReqno() {
		return reqno;
	}
	public void setReqno(String reqno) {
		this.reqno = reqno;
	}
	public String getReqauthkey() {
		return reqauthkey;
	}
	public void setReqauthkey(String reqauthkey) {
		this.reqauthkey = reqauthkey;
	}
	public String getAuthkey() {
		return authkey;
	}
	public void setAuthkey(String authkey) {
		this.authkey = authkey;
	}
}