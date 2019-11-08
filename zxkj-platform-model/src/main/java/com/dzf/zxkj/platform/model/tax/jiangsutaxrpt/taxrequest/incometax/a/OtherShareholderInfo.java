package com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.incometax.a;

import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.TaxExcelPos;

// 持有外国企业10%以上股份或有表决权股份的其他股东情况
@TaxExcelPos(reportID = "10412006", reportname = "居民企业参股外国企业信息报告表", rowBegin = 10, rowEnd = 13, col = 0)
public class OtherShareholderInfo {
	// 持股股东中文名称
	@TaxExcelPos(col = 0)
	private String cggdmc;
	// 持股股东外文名称
	@TaxExcelPos(col = 2)
	private String cggdmcyw;
	// 居住地或成立地中文
	@TaxExcelPos(col = 6)
	private String jzd;
	// 居住地或成立地外文
	@TaxExcelPos(col = 10)
	private String jzdwg;
	// 持股类型
	@TaxExcelPos(col = 12, isCode = true)
	private String cglx;
	// 持股比例
	@TaxExcelPos(col = 14)
	private String cgbl;
	// 权益份额起始日期
	@TaxExcelPos(col = 15)
	private DZFDate ddgfrq;

	public String getCggdmc() {
		return cggdmc;
	}

	public void setCggdmc(String cggdmc) {
		this.cggdmc = cggdmc;
	}

	public String getCggdmcyw() {
		return cggdmcyw;
	}

	public void setCggdmcyw(String cggdmcyw) {
		this.cggdmcyw = cggdmcyw;
	}

	public String getJzd() {
		return jzd;
	}

	public void setJzd(String jzd) {
		this.jzd = jzd;
	}

	public String getJzdwg() {
		return jzdwg;
	}

	public void setJzdwg(String jzdwg) {
		this.jzdwg = jzdwg;
	}

	public String getCglx() {
		return cglx;
	}

	public void setCglx(String cglx) {
		this.cglx = cglx;
	}

	public String getCgbl() {
		return cgbl;
	}

	public void setCgbl(String cgbl) {
		this.cgbl = cgbl;
	}

	public DZFDate getDdgfrq() {
		return ddgfrq;
	}

	public void setDdgfrq(DZFDate ddgfrq) {
		this.ddgfrq = ddgfrq;
	}

}
