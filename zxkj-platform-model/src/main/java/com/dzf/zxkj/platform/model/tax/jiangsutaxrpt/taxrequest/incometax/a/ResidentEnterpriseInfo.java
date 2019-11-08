package com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.incometax.a;

import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.TaxExcelPos;

// 企业信息 data1
@TaxExcelPos(reportID = "10412006", reportname = "居民企业参股外国企业信息报告表")
public class ResidentEnterpriseInfo {
	// 企业名称
	@TaxExcelPos(row = 3, col = 3)
	private String qymc;
	// 纳税人识别号
	@TaxExcelPos(row = 3, col = 11)
	private String nsrsbh;
	// 外国企业中文名称
	@TaxExcelPos(row = 5, col = 3)
	private String wgqymc;
	// 外国企业中文成立地
	@TaxExcelPos(row = 6, col = 3)
	private String cld;
	// 外国企业外文名称
	@TaxExcelPos(row = 5, col = 11)
	private String wgqymcyw;
	// 外国企业外文成立地
	@TaxExcelPos(row = 6, col = 11)
	private String cldwg;
	// 所属国纳税识别号
	@TaxExcelPos(row = 5, col = 15)
	private String szgnsrsbh;
	// 主营业务类型
	@TaxExcelPos(row = 6, col = 15)
	private String zyywlx;
	// 报告人持股比例
	@TaxExcelPos(row = 7, col = 3)
	private DZFDouble bbrcgbl;

	public String getQymc() {
		return qymc;
	}

	public void setQymc(String qymc) {
		this.qymc = qymc;
	}

	public String getNsrsbh() {
		return nsrsbh;
	}

	public void setNsrsbh(String nsrsbh) {
		this.nsrsbh = nsrsbh;
	}

	public String getWgqymc() {
		return wgqymc;
	}

	public void setWgqymc(String wgqymc) {
		this.wgqymc = wgqymc;
	}

	public String getCld() {
		return cld;
	}

	public void setCld(String cld) {
		this.cld = cld;
	}

	public String getWgqymcyw() {
		return wgqymcyw;
	}

	public void setWgqymcyw(String wgqymcyw) {
		this.wgqymcyw = wgqymcyw;
	}

	public String getCldwg() {
		return cldwg;
	}

	public void setCldwg(String cldwg) {
		this.cldwg = cldwg;
	}

	public String getSzgnsrsbh() {
		return szgnsrsbh;
	}

	public void setSzgnsrsbh(String szgnsrsbh) {
		this.szgnsrsbh = szgnsrsbh;
	}

	public String getZyywlx() {
		return zyywlx;
	}

	public void setZyywlx(String zyywlx) {
		this.zyywlx = zyywlx;
	}

	public DZFDouble getBbrcgbl() {
		return bbrcgbl;
	}

	public void setBbrcgbl(DZFDouble bbrcgbl) {
		this.bbrcgbl = bbrcgbl;
	}

}
