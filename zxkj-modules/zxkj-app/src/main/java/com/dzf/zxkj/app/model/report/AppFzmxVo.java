package com.dzf.zxkj.app.model.report;


import com.dzf.zxkj.common.model.SuperVO;

public class AppFzmxVo extends SuperVO {

	private String mc;//明细名称

	private String bqfsjf;//本期发生借方
	
	private String bqfsdf;//本期发生贷方
	
	private String rq;//日期
	
	private String count;//数量
	
	private String dj;//单价
	
	private String ye;//金额
	
	private String fx;//0 借方，1 贷方
	
	private String fzlx;//辅助类型 0 采购 1销售 2 剩余
	
	private String fzlx_mc;//辅助类型名称 采，售，余
	
	public String getMc() {
		return mc;
	}

	public void setMc(String mc) {
		this.mc = mc;
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

	public String getFx() {
		return fx;
	}

	public void setFx(String fx) {
		this.fx = fx;
	}

	public String getFzlx_mc() {
		return fzlx_mc;
	}

	public void setFzlx_mc(String fzlx_mc) {
		this.fzlx_mc = fzlx_mc;
	}

	public String getDj() {
		return dj;
	}

	public void setDj(String dj) {
		this.dj = dj;
	}

	public String getFzlx() {
		return fzlx;
	}

	public void setFzlx(String fzlx) {
		this.fzlx = fzlx;
	}

	public String getRq() {
		return rq;
	}

	public void setRq(String rq) {
		this.rq = rq;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public String getYe() {
		return ye;
	}

	public void setYe(String ye) {
		this.ye = ye;
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
