package com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.incometax.a;

import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.TaxExcelPos;

@TaxExcelPos(reportID = "10412007", reportname = "技术成果投资入股企业所得税递延纳税备案表")
public class TechnicalAchievementSum {
	// 公允价值
	@TaxExcelPos(row = 14, col = 4)
	private DZFDouble gyjzhj;
	// 计税基础
	@TaxExcelPos(row = 14, col = 5)
	private DZFDouble jsjchj;
	// 递延所得
	@TaxExcelPos(row = 14, col = 7)
	private DZFDouble dysdhj;

	public DZFDouble getGyjzhj() {
		return gyjzhj;
	}

	public void setGyjzhj(DZFDouble gyjzhj) {
		this.gyjzhj = gyjzhj;
	}

	public DZFDouble getJsjchj() {
		return jsjchj;
	}

	public void setJsjchj(DZFDouble jsjchj) {
		this.jsjchj = jsjchj;
	}

	public DZFDouble getDysdhj() {
		return dysdhj;
	}

	public void setDysdhj(DZFDouble dysdhj) {
		this.dysdhj = dysdhj;
	}

}
