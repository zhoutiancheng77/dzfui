package com.dzf.zxkj.app.pub;

public enum PayMethodEnum {

	CASH(0, "现金"), BANK(1, "银行"), OTHER(2, "其他");

	private Integer code;
	private String value;

	private PayMethodEnum(Integer code, String value) {
		this.code = code;
		this.value = value;
	}

	public Integer getCode() {
		return code;
	}

	public String getMsg() {
		return value;
	}

}
