package com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.vattax.ordinary;

import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.TaxExcelPos;

//代扣代缴税收通用缴款书抵扣清单 sb10101007vo_01
@TaxExcelPos(reportID = "10101007", reportname = "代扣代缴税收通用缴款书抵扣清单", rowBegin = 5, rowEnd = 25, col = 0)
public class WithholdTaxDeductList {
	// 税额
	@TaxExcelPos(col = 5)
	private DZFDouble se;

	// 代扣代缴凭证编号
	@TaxExcelPos(col = 4)
	private String dkdjpzbh;

	// 代扣代缴项目
	@TaxExcelPos(col = 3)
	private String dkdjxm;

	// 征收机关名称
	@TaxExcelPos(col = 2)
	private String zsjgmc;

	// 扣缴人名称
	@TaxExcelPos(col = 1)
	private String kjrmc;

	// 扣缴人纳税人识别号
	@TaxExcelPos(col = 0)
	private String kjrnsrsbh;

	public DZFDouble getSe() {
		return se;
	}

	public void setSe(DZFDouble se) {
		this.se = se;
	}

	public String getDkdjpzbh() {
		return dkdjpzbh;
	}

	public void setDkdjpzbh(String dkdjpzbh) {
		this.dkdjpzbh = dkdjpzbh;
	}

	public String getDkdjxm() {
		return dkdjxm;
	}

	public void setDkdjxm(String dkdjxm) {
		this.dkdjxm = dkdjxm;
	}

	public String getZsjgmc() {
		return zsjgmc;
	}

	public void setZsjgmc(String zsjgmc) {
		this.zsjgmc = zsjgmc;
	}

	public String getKjrmc() {
		return kjrmc;
	}

	public void setKjrmc(String kjrmc) {
		this.kjrmc = kjrmc;
	}

	public String getKjrnsrsbh() {
		return kjrnsrsbh;
	}

	public void setKjrnsrsbh(String kjrnsrsbh) {
		this.kjrnsrsbh = kjrnsrsbh;
	}

}
