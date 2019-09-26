package com.dzf.zxkj.platform.model.sys;

public class AddInfo implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1568548052311799300L;

	private String[] subjcodes = null;
	
	private String parentcode = null;
	
	private SubjCode parentsubj = null;
	
	private Integer level = -1;

	public String[] getSubjcodes() {
		return subjcodes;
	}

	public void setSubjcodes(String[] subjcodes) {
		this.subjcodes = subjcodes;
	}

	public String getParentcode() {
		return parentcode;
	}

	public void setParentcode(String parentcode) {
		this.parentcode = parentcode;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public SubjCode getParentsubj() {
		return parentsubj;
	}

	public void setParentsubj(SubjCode parentsubj) {
		this.parentsubj = parentsubj;
	}
	
}
