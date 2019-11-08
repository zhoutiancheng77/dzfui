package com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.incometax.b;

import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.TaxExcelPos;

// 报告人处置外国企业股份情况
@TaxExcelPos(reportID = "10413002", reportname = "居民企业参股外国企业信息报告表", rowBegin = 27, rowEnd = 29, col = 0)
public class DisposalForeignEnterprisesSharesInfo {
	// 被处置股份类型
	@TaxExcelPos(col = 0, isCode = true)
	private String bczgflx;
	// 处置日期
	@TaxExcelPos(col = 5)
	private DZFDate czrq;
	// 处置方式
	@TaxExcelPos(col = 9)
	private String czfs;
	// 处置前报告人在外国企业持股份额
	@TaxExcelPos(col = 12)
	private String czqwgqygfe;
	// 处置后报告人在外国企业持股份额
	@TaxExcelPos(col = 14)
	private String czhwgqygfe;

	public String getBczgflx() {
		return bczgflx;
	}

	public void setBczgflx(String bczgflx) {
		this.bczgflx = bczgflx;
	}

	public DZFDate getCzrq() {
		return czrq;
	}

	public void setCzrq(DZFDate czrq) {
		this.czrq = czrq;
	}

	public String getCzfs() {
		return czfs;
	}

	public void setCzfs(String czfs) {
		this.czfs = czfs;
	}

	public String getCzqwgqygfe() {
		return czqwgqygfe;
	}

	public void setCzqwgqygfe(String czqwgqygfe) {
		this.czqwgqygfe = czqwgqygfe;
	}

	public String getCzhwgqygfe() {
		return czhwgqygfe;
	}

	public void setCzhwgqygfe(String czhwgqygfe) {
		this.czhwgqygfe = czhwgqygfe;
	}

}
