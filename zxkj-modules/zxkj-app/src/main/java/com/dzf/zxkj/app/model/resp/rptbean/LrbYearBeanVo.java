package com.dzf.zxkj.app.model.resp.rptbean;


import com.dzf.zxkj.common.model.SuperVO;

/**
 * 利润表本年合计
 * @author admin
 *
 */
public class LrbYearBeanVo extends SuperVO {

	private String rq;//月份
	
	private String jlr;//净利润
	
	private String sr;//收入
	
	private String zc;//支出
	
	
	public String getRq() {
		return rq;
	}

	public void setRq(String rq) {
		this.rq = rq;
	}

	public String getJlr() {
		return jlr;
	}

	public void setJlr(String jlr) {
		this.jlr = jlr;
	}

	public String getSr() {
		return sr;
	}

	public void setSr(String sr) {
		this.sr = sr;
	}

	public String getZc() {
		return zc;
	}

	public void setZc(String zc) {
		this.zc = zc;
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
