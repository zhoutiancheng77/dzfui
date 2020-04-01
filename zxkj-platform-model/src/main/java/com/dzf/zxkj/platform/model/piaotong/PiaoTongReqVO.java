package com.dzf.zxkj.platform.model.piaotong;

import java.io.Serializable;

public class PiaoTongReqVO implements Serializable {

	private String platformCode;//平台编码 票通分配给开发者的平台编码
	private String signType;//加密类型 目前支持RSA
	private String sign;//签名串	商户请求参数的签名串
	private String format;//业务报文格式	目前支持JSON
	private String timestamp;//请求时间	yyyy-MM-dd HH:mm:ss
	private String version;//版本号	调用接口版本，固定为1.0
	private String serialNo;//交易请求流水号	4位平台简称＋14位日期yyyymmddhhmmss)+8位随机数（通过SDK生成）
	
	private String content;//请求参数的集合，最大长度不限，除公共参数外所有请求参数都必须放在这个参数中传递

	public String getPlatformCode() {
		return platformCode;
	}

	public String getSignType() {
		return signType;
	}

	public String getSign() {
		return sign;
	}

	public String getFormat() {
		return format;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public String getVersion() {
		return version;
	}

	public String getSerialNo() {
		return serialNo;
	}

	public String getContent() {
		return content;
	}

	public void setPlatformCode(String platformCode) {
		this.platformCode = platformCode;
	}

	public void setSignType(String signType) {
		this.signType = signType;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}

	public void setContent(String content) {
		this.content = content;
	}

}

