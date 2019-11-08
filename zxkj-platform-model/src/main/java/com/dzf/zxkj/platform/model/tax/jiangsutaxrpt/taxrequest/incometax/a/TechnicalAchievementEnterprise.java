package com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.incometax.a;

// 技术成果投资入股企业所得税递延纳税备案表
public class TechnicalAchievementEnterprise {
	private TechnicalAchievementDetail[] data1;
	// 合计行
	private TechnicalAchievementSum data2;

	public TechnicalAchievementDetail[] getData1() {
		return data1;
	}

	public void setData1(TechnicalAchievementDetail[] data1) {
		this.data1 = data1;
	}

	public TechnicalAchievementSum getData2() {
		return data2;
	}

	public void setData2(TechnicalAchievementSum data2) {
		this.data2 = data2;
	}

}
