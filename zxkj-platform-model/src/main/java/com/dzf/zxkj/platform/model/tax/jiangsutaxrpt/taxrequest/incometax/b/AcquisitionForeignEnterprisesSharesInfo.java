package com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.incometax.b;

import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.TaxExcelPos;

// 报告人收购外国企业股份情况
@TaxExcelPos(reportID = "10413002", reportname = "居民企业参股外国企业信息报告表", rowBegin = 22, rowEnd = 24, col = 0)
public class AcquisitionForeignEnterprisesSharesInfo {
	// 被收购股份类型
	@TaxExcelPos(col = 0, isCode = true)
	private String bsggflx;
	// 交易日期
	@TaxExcelPos(col = 5)
	private DZFDate jyrq;
	// 收购方式
	@TaxExcelPos(col = 9)
	private String sgfs;
	// 收购前报告人在外国企业持股份额
	@TaxExcelPos(col = 12)
	private String sgqwgqygfe;
	// 收购后报告人在外国企业持股份额
	@TaxExcelPos(col = 14)
	private String sghwgqygfe;

	public String getBsggflx() {
		return bsggflx;
	}

	public void setBsggflx(String bsggflx) {
		this.bsggflx = bsggflx;
	}

	public DZFDate getJyrq() {
		return jyrq;
	}

	public void setJyrq(DZFDate jyrq) {
		this.jyrq = jyrq;
	}

	public String getSgfs() {
		return sgfs;
	}

	public void setSgfs(String sgfs) {
		this.sgfs = sgfs;
	}

	public String getSgqwgqygfe() {
		return sgqwgqygfe;
	}

	public void setSgqwgqygfe(String sgqwgqygfe) {
		this.sgqwgqygfe = sgqwgqygfe;
	}

	public String getSghwgqygfe() {
		return sghwgqygfe;
	}

	public void setSghwgqygfe(String sghwgqygfe) {
		this.sghwgqygfe = sghwgqygfe;
	}

}
