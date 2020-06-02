package com.dzf.zxkj.app.model.ticket;

import com.dzf.zxkj.common.model.SuperVO;

/**
 * （2）货运运输业增值税专用发票返回报文内层内容说明（发票类型:02）明细
 * @author zhangj
 *
 */
public class HyZzsTicketBVO extends SuperVO {
	
	private String fyxm;//费用项目
	private String je;//金额

	public String getFyxm() {
		return fyxm;
	}

	public void setFyxm(String fyxm) {
		this.fyxm = fyxm;
	}

	public String getJe() {
		return je;
	}

	public void setJe(String je) {
		this.je = je;
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
