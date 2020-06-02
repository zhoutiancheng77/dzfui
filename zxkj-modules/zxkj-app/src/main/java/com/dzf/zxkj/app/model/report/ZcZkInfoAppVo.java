package com.dzf.zxkj.app.model.report;


import com.dzf.zxkj.common.model.SuperVO;

public class ZcZkInfoAppVo extends SuperVO {

	private String xm;
	private String ncje;
	private String qmje;

	public String getXm() {
		return xm;
	}

	public void setXm(String xm) {
		this.xm = xm;
	}

	public String getNcje() {
		return ncje;
	}

	public void setNcje(String ncje) {
		this.ncje = ncje;
	}

	public String getQmje() {
		return qmje;
	}

	public void setQmje(String qmje) {
		this.qmje = qmje;
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
