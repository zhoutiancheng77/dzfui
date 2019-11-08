package com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.incometax.a;

import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.TaxExcelPos;

// 中国居民个人担任外国企业高管或董事情况
@TaxExcelPos(reportID = "10412006", reportname = "居民企业参股外国企业信息报告表", rowBegin = 16, rowEnd = 18, col = 0)
public class ResidentAsForeignExecutivesInfo {
	// 中国居民个人姓名
	@TaxExcelPos(col = 0)
	private String zgjnczd;
	// 中国境内常驻地
	@TaxExcelPos(col = 2)
	private String sfsbh;
	// 身份证件类型
	@TaxExcelPos(col = 5, isCode = true)
	private String sflx;
	// 身份证件类型
	@TaxExcelPos(col = 5, isName = true)
	private String sflxmc;

	// 身份证件号码
	@TaxExcelPos(col = 9)
	private String nsrsbh;
	// 职务
	@TaxExcelPos(col = 12)
	private String zw;
	// 任职日期起
	@TaxExcelPos(col = 14)
	private DZFDate rzqrq;
	// 任职日期止
	@TaxExcelPos(col = 16)
	private DZFDate rzzrq;

	public String getZgjnczd() {
		return zgjnczd;
	}

	public void setZgjnczd(String zgjnczd) {
		this.zgjnczd = zgjnczd;
	}

	public String getSfsbh() {
		return sfsbh;
	}

	public void setSfsbh(String sfsbh) {
		this.sfsbh = sfsbh;
	}

	public String getSflx() {
		return sflx;
	}

	public void setSflx(String sflx) {
		this.sflx = sflx;
	}

	public String getSflxmc() {
		return sflxmc;
	}

	public void setSflxmc(String sflxmc) {
		this.sflxmc = sflxmc;
	}

	public String getNsrsbh() {
		return nsrsbh;
	}

	public void setNsrsbh(String nsrsbh) {
		this.nsrsbh = nsrsbh;
	}

	public String getZw() {
		return zw;
	}

	public void setZw(String zw) {
		this.zw = zw;
	}

	public DZFDate getRzqrq() {
		return rzqrq;
	}

	public void setRzqrq(DZFDate rzqrq) {
		this.rzqrq = rzqrq;
	}

	public DZFDate getRzzrq() {
		return rzzrq;
	}

	public void setRzzrq(DZFDate rzzrq) {
		this.rzzrq = rzzrq;
	}

}
