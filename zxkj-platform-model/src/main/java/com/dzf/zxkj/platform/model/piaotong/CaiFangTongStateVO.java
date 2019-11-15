package com.dzf.zxkj.platform.model.piaotong;

import java.io.Serializable;

public class CaiFangTongStateVO implements Serializable{
	private String returnCode;//返回代码	0000 成功，其他为错误
	private String returnMessage;//返回描述	0000返回成功、其他返回错误描述 
	public String getReturnCode() {
		return returnCode;
	}
	public String getReturnMessage() {
		return returnMessage;
	}
	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}
	public void setReturnMessage(String returnMessage) {
		this.returnMessage = returnMessage;
	}
	
}
