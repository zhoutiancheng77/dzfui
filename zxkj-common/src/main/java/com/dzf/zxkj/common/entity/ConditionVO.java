package com.dzf.zxkj.common.entity;

public class ConditionVO {

	private String cdname;
	private String cdsymbol;
	private Object cdvalue;
	
	
	
	public ConditionVO(String name, String symbol, Object value){
		this.cdname=name;
		this.cdsymbol=symbol;
		this.cdvalue=value;
	}
	
	public ConditionVO() {
	}

	public String getCdname() {
		return cdname;
	}
	public void setCdname(String cdname) {
		this.cdname = cdname;
	}
	public String getCdsymbol() {
		return cdsymbol;
	}
	public void setCdsymbol(String cdsymbol) {
		this.cdsymbol = cdsymbol;
	}
	public Object getCdvalue() {
		return cdvalue;
	}
	public void setCdvalue(Object cdvalue) {
		this.cdvalue = cdvalue;
	}
	
	
	
}
