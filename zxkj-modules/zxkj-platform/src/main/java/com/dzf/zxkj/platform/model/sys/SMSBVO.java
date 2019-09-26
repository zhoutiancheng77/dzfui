package com.dzf.zxkj.platform.model.sys;

import java.io.Serializable;
import java.util.Map;

public class SMSBVO implements Serializable {

	private String appid; // appid
	private String[] phone; // 手机号
	private String dxqm;// 签名
	private String templatecode;// 模版编号
	private String smsip;// IP

	private Map<String, String> params = null;// 模版参数

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public String[] getPhone() {
		return phone;
	}

	public void setPhone(String[] phone) {
		this.phone = phone;
	}

	public String getDxqm() {
		return dxqm;
	}

	public void setDxqm(String dxqm) {
		this.dxqm = dxqm;
	}

	public String getTemplatecode() {
		return templatecode;
	}

	public void setTemplatecode(String templatecode) {
		this.templatecode = templatecode;
	}

	public String getSmsip() {
		return smsip;
	}

	public void setSmsip(String smsip) {
		this.smsip = smsip;
	}

	public Map<String, String> getParams() {
		return params;
	}

	public void setParams(Map<String, String> params) {
		this.params = params;
	}

}
