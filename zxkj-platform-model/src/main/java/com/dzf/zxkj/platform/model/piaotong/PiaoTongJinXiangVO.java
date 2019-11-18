package com.dzf.zxkj.platform.model.piaotong;

import java.io.Serializable;
/**
 * 票通进项采集token/概要数据  请求、返回 封装VO
 * @author wangzhn
 *
 */
public class PiaoTongJinXiangVO implements Serializable {

	private String code;//返回值代码
	
	private PiaoTongJinXiangRespVO response;//返回数据主体
	
	private String message;//返回值信息

	public String getCode() {
		return code;
	}

	public PiaoTongJinXiangRespVO getResponse() {
		return response;
	}

	public String getMessage() {
		return message;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setResponse(PiaoTongJinXiangRespVO response) {
		this.response = response;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
