package com.dzf.zxkj.platform.model.tax.jiangsutaxrpt;
/**
 * 申报查询结果VO
 * @author 
 *
 */
public class QueryResultVO {
	private String sb_zlbh;		//申报种类编号
	private String sb_zlmc;		//申报种类名称
	private String sbzt_dm;		//申报状态代码
	private String sb_lsh;			//流水号
	public String getSb_zlbh() {
		return sb_zlbh;
	}
	public String getSb_zlmc() {
		return sb_zlmc;
	}
	public String getSbzt_dm() {
		return sbzt_dm;
	}
	public String getSb_lsh() {
		return sb_lsh;
	}
	public void setSb_zlbh(String sb_zlbh) {
		this.sb_zlbh = sb_zlbh;
	}
	public void setSb_zlmc(String sb_zlmc) {
		this.sb_zlmc = sb_zlmc;
	}
	public void setSbzt_dm(String sbzt_dm) {
		this.sbzt_dm = sbzt_dm;
	}
	public void setSb_lsh(String sb_lsh) {
		this.sb_lsh = sb_lsh;
	}
}
