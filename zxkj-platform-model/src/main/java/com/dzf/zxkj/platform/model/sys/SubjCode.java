package com.dzf.zxkj.platform.model.sys;

import java.util.Map;

public class SubjCode implements java.io.Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -963617353619150356L;

	private String pk_accsubj = null;
	
	private String parent_code = null;//上级code
	
	private String code = null;
	
	private String name = null;
	
	private String matchstyle = null;
	
	private Integer level = 0;//期初空级别
	
	private Integer accountkind = 0;
	
	private Integer direction = 0;
	
	private Map<String,SubjCode> codemap = null;//下级级别科目
	

	public String getPk_accsubj() {
		return pk_accsubj;
	}

	public void setPk_accsubj(String pk_accsubj) {
		this.pk_accsubj = pk_accsubj;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public Map<String,SubjCode> getCodelist() {
		return codemap;
	}

	public void setCodelist(Map<String,SubjCode> codemap) {
		this.codemap = codemap;
	}
	
	public String getMatchstyle() {
		return matchstyle;
	}

	public void setMatchstyle(String matchstyle) {
		this.matchstyle = matchstyle;
	}

	public String getParent_code() {
		return parent_code;
	}

	public void setParent_code(String parent_code) {
		this.parent_code = parent_code;
	}
	
	

	public Integer getAccountkind() {
		return accountkind;
	}

	public void setAccountkind(Integer accountkind) {
		this.accountkind = accountkind;
	}

	public Integer getDirection() {
		return direction;
	}

	public void setDirection(Integer direction) {
		this.direction = direction;
	}

	@Override
	public String toString() {
		return name;
	}
}
