package com.dzf.zxkj.platform.model.piaotong;

import java.io.Serializable;

public class PiaoTongResVO implements Serializable {

	private String code; //响应状态 业务返回码

	private String msg;//响应消息 业务返回码描述
	
	private String sign;//签名串
	
	private String serialNo;//交易请求流水号
	
	private String content;//业务报文内容 3DES加密

	public String getCode() {
		return code;
	}

	public String getMsg() {
		return msg;
	}

	public String getSign() {
		return sign;
	}

	public String getSerialNo() {
		return serialNo;
	}

	public String getContent() {
		return content;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
}
