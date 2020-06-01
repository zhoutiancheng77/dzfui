package com.dzf.zxkj.app.model.report;


import com.dzf.zxkj.common.model.SuperVO;

/**
 * 辅助余额信息
 * @author zhangj
 *
 */
public class AppFzYeVo extends SuperVO {
	
	private String mc;//名称
	
	private String fzid;//辅助id
	
	private String bqfs;//本期发生借方
	
	private String bqfsjf;//本期发生借方
	
	private String bqfsdf;//本期发生贷方
	
	private String rq;//日期
	
	private String ye;//余额
	
	private String fx;//0 借方 1贷方 2平
	
	private AppFzmxVo[] mxvos;//明细信息
	
	public String getFzid() {
		return fzid;
	}

	public void setFzid(String fzid) {
		this.fzid = fzid;
	}

	public String getFx() {
		return fx;
	}

	public void setFx(String fx) {
		this.fx = fx;
	}

	public String getBqfsjf() {
		return bqfsjf;
	}

	public void setBqfsjf(String bqfsjf) {
		this.bqfsjf = bqfsjf;
	}

	public String getBqfsdf() {
		return bqfsdf;
	}

	public void setBqfsdf(String bqfsdf) {
		this.bqfsdf = bqfsdf;
	}

	public AppFzmxVo[] getMxvos() {
		return mxvos;
	}

	public void setMxvos(AppFzmxVo[] mxvos) {
		this.mxvos = mxvos;
	}

	public String getYe() {
		return ye;
	}

	public void setYe(String ye) {
		this.ye = ye;
	}

	public String getMc() {
		return mc;
	}

	public void setMc(String mc) {
		this.mc = mc;
	}
	
	public String getBqfs() {
		return bqfs;
	}

	public void setBqfs(String bqfs) {
		this.bqfs = bqfs;
	}

	public String getRq() {
		return rq;
	}

	public void setRq(String rq) {
		this.rq = rq;
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
