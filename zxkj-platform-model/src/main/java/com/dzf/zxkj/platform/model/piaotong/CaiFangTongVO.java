package com.dzf.zxkj.platform.model.piaotong;

import java.io.Serializable;

public class CaiFangTongVO implements Serializable{

	private String appId;//应用标识
	
	private String version;//接口版本	默认为1.0
	
	private String passWord;//10位随机数+Base64({（10位随机数+注册码）MD5})
					//10位随机数+Base64({（10位随机数+8位固定秘钥（53520764））MD5})
	
	private String encryptCode;//0,1,2 	加密方式代码0:不加密（base64）  1: 3DES加密   2：CA加密
	
	private String content;//base64请求数据内容或返回数据内容
	
	private CaiFangTongStateVO state;//返回状态
	
	public String getAppId() {
		return appId;
	}

	public String getVersion() {
		return version;
	}

	public String getPassWord() {
		return passWord;
	}

	public String getEncryptCode() {
		return encryptCode;
	}

	public String getContent() {
		return content;
	}

	public CaiFangTongStateVO getState() {
		return state;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}

	public void setEncryptCode(String encryptCode) {
		this.encryptCode = encryptCode;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setState(CaiFangTongStateVO state) {
		this.state = state;
	}

}

