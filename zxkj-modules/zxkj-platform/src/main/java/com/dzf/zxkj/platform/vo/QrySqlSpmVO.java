package com.dzf.zxkj.platform.vo;


import com.dzf.zxkj.base.framework.SQLParameter;

public class QrySqlSpmVO {

	private String sql;
	
	private SQLParameter spm;

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public SQLParameter getSpm() {
		return spm;
	}

	public void setSpm(SQLParameter spm) {
		this.spm = spm;
	}
}
