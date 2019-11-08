package com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest;


public class RequestBodyVO {
	private String action;
	private String sign;
	// 电子税局门户登录名
	private String login_user;
	// 电子税局门户登录密码（需加密）
	private String login_pwd;
	private String ywbw;

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getLogin_user() {
		return login_user;
	}

	public void setLogin_user(String login_user) {
		this.login_user = login_user;
	}

	public String getLogin_pwd() {
		return login_pwd;
	}

	public void setLogin_pwd(String login_pwd) {
		this.login_pwd = login_pwd;
	}

	public String getYwbw() {
		return ywbw;
	}

	public void setYwbw(String ywbw) {
		this.ywbw = ywbw;
	}
}
