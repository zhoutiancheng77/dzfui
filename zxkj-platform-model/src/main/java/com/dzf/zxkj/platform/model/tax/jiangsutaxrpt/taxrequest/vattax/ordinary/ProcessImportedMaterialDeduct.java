package com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.vattax.ordinary;

import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.TaxExcelPos;

//生产企业进料加工抵扣明细表 sb10101015vo_01
@TaxExcelPos(reportID = "10101015", reportname = "生产企业进料加工抵扣明细表", rowBegin = 6, rowEnd = 25, col = 1)
public class ProcessImportedMaterialDeduct {
	// 序号
	@TaxExcelPos(col = 0)
	private String sbxh;

	// 进料加工贸易免税证明或免税核销证明编号
	@TaxExcelPos(col = 1)
	private String jljgmszmbh;

	// 证明出具年月
	@TaxExcelPos(col = 2)
	private String zmcjny;

	// 应抵扣税额
	@TaxExcelPos(col = 3)
	private DZFDouble ydkse;

	// 不予抵扣抵减额
	@TaxExcelPos(col = 4)
	private DZFDouble bydkdje;

	// 备注
	@TaxExcelPos(col = 5)
	private String bz;

	public String getSbxh() {
		return sbxh;
	}

	public void setSbxh(String sbxh) {
		this.sbxh = sbxh;
	}

	public String getJljgmszmbh() {
		return jljgmszmbh;
	}

	public void setJljgmszmbh(String jljgmszmbh) {
		this.jljgmszmbh = jljgmszmbh;
	}

	public String getZmcjny() {
		return zmcjny;
	}

	public void setZmcjny(String zmcjny) {
		this.zmcjny = zmcjny;
	}

	public DZFDouble getYdkse() {
		return ydkse;
	}

	public void setYdkse(DZFDouble ydkse) {
		this.ydkse = ydkse;
	}

	public DZFDouble getBydkdje() {
		return bydkdje;
	}

	public void setBydkdje(DZFDouble bydkdje) {
		this.bydkdje = bydkdje;
	}

	public String getBz() {
		return bz;
	}

	public void setBz(String bz) {
		this.bz = bz;
	}

}
