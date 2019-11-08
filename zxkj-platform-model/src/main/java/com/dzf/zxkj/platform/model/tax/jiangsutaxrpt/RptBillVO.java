package com.dzf.zxkj.platform.model.tax.jiangsutaxrpt;
/**
 * 查询待申报的报表清单结果VO
 * @author 
 *
 */

public class RptBillVO {
	private String xh;			//序号
	private String bb_zlid;		//编码”:”101010001”,”
	private String bb_zlmc;		//名称”,”增值税主表”
	public String getXh() {
		return xh;
	}
	public String getBb_zlid() {
		return bb_zlid;
	}
	public String getBb_zlmc() {
		return bb_zlmc;
	}
	public void setXh(String xh) {
		this.xh = xh;
	}
	public void setBb_zlid(String bb_zlid) {
		this.bb_zlid = bb_zlid;
	}
	public void setBb_zlmc(String bb_zlmc) {
		this.bb_zlmc = bb_zlmc;
	}
}
