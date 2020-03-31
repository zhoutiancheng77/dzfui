package com.dzf.zxkj.platform.model.batchprint;

public enum PrintStatusEnum {

	PROCESSING(0, "待生成"), GENERATE(1, "已生成"), LOADED(2, "已下载"), GENFAIL(3, "生成失败");

	private Integer code;
	private String name;

	private PrintStatusEnum(Integer code, String name) {
		this.code = code;
		this.name = name;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
