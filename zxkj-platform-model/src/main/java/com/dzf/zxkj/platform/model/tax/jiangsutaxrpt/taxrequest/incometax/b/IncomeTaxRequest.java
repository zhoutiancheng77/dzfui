package com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.incometax.b;

public class IncomeTaxRequest {
	// 所得税月(季)度纳税申报表(A类）
	private IncomeTax sb10413001VO;
	// 居民企业参股外国企业信息报告表
	private ResidentParticipationForeignReport sb10413002VO;

	public IncomeTax getSb10413001VO() {
		return sb10413001VO;
	}

	public void setSb10413001VO(IncomeTax sb10413001vo) {
		sb10413001VO = sb10413001vo;
	}

	public ResidentParticipationForeignReport getSb10413002VO() {
		return sb10413002VO;
	}

	public void setSb10413002VO(ResidentParticipationForeignReport sb10413002vo) {
		sb10413002VO = sb10413002vo;
	}

}
