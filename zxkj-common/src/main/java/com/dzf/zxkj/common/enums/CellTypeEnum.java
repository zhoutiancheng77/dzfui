package com.dzf.zxkj.common.enums;

/****
 * 单元格类型
 * @author asoka
 *
 */
public enum CellTypeEnum {
	
	STA("STA","无此值不可编辑(不适用)"),
	NA("NA","可以有值"),
	FM("FM","使用公式");
	
	
	private String code;
	private String msg;
	private CellTypeEnum(String code,String msg){
		this.code = code;
		this.msg = msg;
	}
	public String getCode(){
		return code;
	}
	public String getMsg(){
		return msg;
	}

}
