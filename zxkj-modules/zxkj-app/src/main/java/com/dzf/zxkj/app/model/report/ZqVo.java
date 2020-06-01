package com.dzf.zxkj.app.model.report;


import com.dzf.zxkj.common.model.SuperVO;

public class ZqVo extends SuperVO {

	private String rq;
	
	private String jzrq;//截止日期

	private String content;//内容
	
	private String[] szs;//税种

	private String zqlx;// 征期类型
	
	private String title;//标题
	
	public String[] getSzs() {
		return szs;
	}

	public void setSzs(String[] szs) {
		this.szs = szs;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getJzrq() {
		return jzrq;
	}

	public void setJzrq(String jzrq) {
		this.jzrq = jzrq;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getRq() {
		return rq;
	}

	public void setRq(String rq) {
		this.rq = rq;
	}

	public String getContent() {
		return content;
	}

	public String getZqlx() {
		return zqlx;
	}

	public void setZqlx(String zqlx) {
		this.zqlx = zqlx;
	}

	@Override
	public String getPKFieldName() {
		return null;
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return null;
	}

}
