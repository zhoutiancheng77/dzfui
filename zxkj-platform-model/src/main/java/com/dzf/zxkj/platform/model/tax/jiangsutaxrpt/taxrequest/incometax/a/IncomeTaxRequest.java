package com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.incometax.a;

public class IncomeTaxRequest {
	// A200000所得税月(季)度预缴纳税申报表
	private IncomeTax sb10412001VO;
	// A201010免税收入、减计收入、所得减免等优惠明细表
	private IncomeTaxAttach1 sb10412002VO;
	// A201020固定资产加速折旧(扣除)优惠明细表
	private IncomeTaxAttach2 sb10412003VO;
	// A201030减免所得税优惠明细表
	private IncomeTaxAttach3 sb10412004VO;
	// A202000企业所得税汇总纳税分支机构所得税分配表
	private BranchIncomeTax sb10412005VO;
	// 居民企业参股外国企业信息报告表
	private ResidentParticipationForeignReport[] sb10412006List;
	// 技术成果投资入股企业所得税递延纳税备案表
	private TechnicalAchievementEnterprise sb10412007VO;

	public IncomeTax getSb10412001VO() {
		return sb10412001VO;
	}

	public void setSb10412001VO(IncomeTax sb10412001vo) {
		sb10412001VO = sb10412001vo;
	}

	public IncomeTaxAttach1 getSb10412002VO() {
		return sb10412002VO;
	}

	public void setSb10412002VO(IncomeTaxAttach1 sb10412002vo) {
		sb10412002VO = sb10412002vo;
	}

	public IncomeTaxAttach2 getSb10412003VO() {
		return sb10412003VO;
	}

	public void setSb10412003VO(IncomeTaxAttach2 sb10412003vo) {
		sb10412003VO = sb10412003vo;
	}

	public IncomeTaxAttach3 getSb10412004VO() {
		return sb10412004VO;
	}

	public void setSb10412004VO(IncomeTaxAttach3 sb10412004vo) {
		sb10412004VO = sb10412004vo;
	}

	public BranchIncomeTax getSb10412005VO() {
		return sb10412005VO;
	}

	public void setSb10412005VO(BranchIncomeTax sb10412005vo) {
		sb10412005VO = sb10412005vo;
	}

	public ResidentParticipationForeignReport[] getSb10412006List() {
		return sb10412006List;
	}

	public void setSb10412006List(
			ResidentParticipationForeignReport[] sb10412006List) {
		this.sb10412006List = sb10412006List;
	}

	public TechnicalAchievementEnterprise getSb10412007VO() {
		return sb10412007VO;
	}

	public void setSb10412007VO(TechnicalAchievementEnterprise sb10412007vo) {
		sb10412007VO = sb10412007vo;
	}

}
