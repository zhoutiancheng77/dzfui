package com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.incometax.a;


// 居民企业参股外国企业信息报告表sb10412006VO
public class ResidentParticipationForeignReport {
	// 企业信息 
	private ResidentEnterpriseInfo data1;
	// 持有外国企业10%以上股份或有表决权股份的其他股东情况
	private OtherShareholderInfo[] data2;
	// 中国居民个人担任外国企业高管或董事情况
	private ResidentAsForeignExecutivesInfo[] data3;
	// 报告人收购外国企业股份情况
	private AcquisitionForeignEnterprisesSharesInfo[] data4;
	// 报告人处置外国企业股份情况
	private DisposalForeignEnterprisesSharesInfo[] data5;

	public ResidentEnterpriseInfo getData1() {
		return data1;
	}

	public void setData1(ResidentEnterpriseInfo data1) {
		this.data1 = data1;
	}

	public OtherShareholderInfo[] getData2() {
		return data2;
	}

	public void setData2(OtherShareholderInfo[] data2) {
		this.data2 = data2;
	}

	public ResidentAsForeignExecutivesInfo[] getData3() {
		return data3;
	}

	public void setData3(ResidentAsForeignExecutivesInfo[] data3) {
		this.data3 = data3;
	}

	public AcquisitionForeignEnterprisesSharesInfo[] getData4() {
		return data4;
	}

	public void setData4(AcquisitionForeignEnterprisesSharesInfo[] data4) {
		this.data4 = data4;
	}

	public DisposalForeignEnterprisesSharesInfo[] getData5() {
		return data5;
	}

	public void setData5(DisposalForeignEnterprisesSharesInfo[] data5) {
		this.data5 = data5;
	}

}
