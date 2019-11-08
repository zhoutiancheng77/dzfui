package com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.vattax.ordinary;

import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.TaxExcelPos;

//增值税纳税申报表附列资料（三）（服务、不动产和无形资产扣除项目明细）  sb10101004vo_01
@TaxExcelPos(reportID = "10101004", reportname = "增值税纳税申报表附列资料（三）")
public class TaxReturnAttach3 {
	// 17%税率的项目
	// 期初余额
	@TaxExcelPos(row = 7, col = 3)
	private DZFDouble yxdcznfwqcye;
	// 期末余额
	@TaxExcelPos(row = 7, col = 7, isTotal = true)
	private DZFDouble yxdcznfwqmye;
	// 免税销售额
	@TaxExcelPos(row = 7, col = 2)
	private DZFDouble yxdcznfwhje;
	// 应税服务扣除项目_本期应扣除金额
	@TaxExcelPos(row = 7, col = 5)
	private DZFDouble yxdcznfwbqykcje;
	// 应税服务扣除项目_本期实际扣除金额
	@TaxExcelPos(row = 7, col = 6)
	private DZFDouble yxdcznfwbqsjkcje;
	// 本期发生额
	@TaxExcelPos(row = 7, col = 4)
	private DZFDouble yxdcznfwbqfse;

	// 11%税率的项目
	// 期初余额
	@TaxExcelPos(row = 8, col = 3)
	private DZFDouble eslysfwqcye;
	// 期末余额
	@TaxExcelPos(row = 8, col = 7, isTotal = true)
	private DZFDouble eslysfwqmye;
	// 免税销售额
	@TaxExcelPos(row = 8, col = 2)
	private DZFDouble eslysfwhje;
	// 应税服务扣除项目_本期应扣除金额
	@TaxExcelPos(row = 8, col = 5)
	private DZFDouble eslysfwbqykcje;
	// 应税服务扣除项目_本期实际扣除金额
	@TaxExcelPos(row = 8, col = 6)
	private DZFDouble eslysfwbqsjkcje;
	// 本期发生额
	@TaxExcelPos(row = 8, col = 4)
	private DZFDouble eslysfwbqfse;

	// 6%税率的项目（不含金融商品转让）
	// 期初余额
	@TaxExcelPos(row = 9, col = 3)
	private DZFDouble sslysfwqcye;
	// 期末余额
	@TaxExcelPos(row = 9, col = 7, isTotal = true)
	private DZFDouble sslysfwqmye;
	// 免税销售额
	@TaxExcelPos(row = 9, col = 2)
	private DZFDouble sslysfwhje;
	// 应税服务扣除项目_本期应扣除金额
	@TaxExcelPos(row = 9, col = 5)
	private DZFDouble sslysfwbqykcje;
	// 应税服务扣除项目_本期实际扣除金额
	@TaxExcelPos(row = 9, col = 6)
	private DZFDouble sslysfwbqsjkcje;
	// 本期发生额
	@TaxExcelPos(row = 9, col = 4)
	private DZFDouble sslysfwbqfse;

	// 6%税率的金融商品转让项目
	// 期初余额
	@TaxExcelPos(row = 10, col = 3)
	private DZFDouble sslysfwqcyenew;
	// 期末余额
	@TaxExcelPos(row = 10, col = 7, isTotal = true)
	private DZFDouble sslysfwqmyenew;
	// 免税销售额
	@TaxExcelPos(row = 10, col = 2)
	private DZFDouble sslysfwhjenew;
	// 应税服务扣除项目_本期应扣除金额
	@TaxExcelPos(row = 10, col = 5)
	private DZFDouble sslysfwbqykcjenew;
	// 应税服务扣除项目_本期实际扣除金额
	@TaxExcelPos(row = 10, col = 6)
	private DZFDouble sslysfwbqsjkcjenew;
	// 本期发生额
	@TaxExcelPos(row = 10, col = 4)
	private DZFDouble sslysfwbqfsenew;

	// 5%征收率的项目
	// 期初余额
	@TaxExcelPos(row = 11, col = 3)
	private DZFDouble sslysfw5qcyenew;
	// 期末余额
	@TaxExcelPos(row = 11, col = 7, isTotal = true)
	private DZFDouble sslysfw5qmyenew;
	// 免税销售额
	@TaxExcelPos(row = 11, col = 2)
	private DZFDouble sslysfw5hjenew;
	// 应税服务扣除项目_本期应扣除金额
	@TaxExcelPos(row = 11, col = 5)
	private DZFDouble sslysfw5bqykcjenew;
	// 应税服务扣除项目_本期实际扣除金额
	@TaxExcelPos(row = 11, col = 6)
	private DZFDouble sslysfw5bqsjkcjenew;
	// 本期发生额
	@TaxExcelPos(row = 11, col = 4)
	private DZFDouble sslysfw5bqfsenew;

	// 3%征收率的项目
	// 期初余额
	@TaxExcelPos(row = 12, col = 3)
	private DZFDouble tslysfwqcye;
	// 期末余额
	@TaxExcelPos(row = 12, col = 7, isTotal = true)
	private DZFDouble tslysfwqmye;
	// 免税销售额
	@TaxExcelPos(row = 12, col = 2)
	private DZFDouble tslysfwhje;
	// 应税服务扣除项目_本期应扣除金额
	@TaxExcelPos(row = 12, col = 5)
	private DZFDouble tslysfwbqykcje;
	// 应税服务扣除项目_本期实际扣除金额
	@TaxExcelPos(row = 12, col = 6)
	private DZFDouble tslysfwbqsjkcje;
	// 本期发生额
	@TaxExcelPos(row = 12, col = 4)
	private DZFDouble tslysfwbqfse;

	// 免抵退税的项目
	// 期初余额
	@TaxExcelPos(row = 13, col = 3)
	private DZFDouble mdtsdysfwqcye;
	// 期末余额
	@TaxExcelPos(row = 13, col = 7, isTotal = true)
	private DZFDouble mdtsdysfwqmye;
	// 免税销售额
	@TaxExcelPos(row = 13, col = 2)
	private DZFDouble mdtsdysfwhje;
	// 应税服务扣除项目_本期应扣除金额
	@TaxExcelPos(row = 13, col = 5)
	private DZFDouble mdtsdysfwbqykcje;
	// 应税服务扣除项目_本期实际扣除金额
	@TaxExcelPos(row = 13, col = 6)
	private DZFDouble mdtsdysfwbqsjkcje;
	// 本期发生额
	@TaxExcelPos(row = 13, col = 4)
	private DZFDouble mdtsdysfwbqfse;

	// 免税的项目
	// 期初余额
	@TaxExcelPos(row = 14, col = 3)
	private DZFDouble msdysfwqcye;
	// 期末余额
	@TaxExcelPos(row = 14, col = 7, isTotal = true)
	private DZFDouble msdysfwqmye;
	// 免税销售额
	@TaxExcelPos(row = 14, col = 2)
	private DZFDouble msdysfwhje;
	// 应税服务扣除项目_本期应扣除金额
	@TaxExcelPos(row = 14, col = 5)
	private DZFDouble msdysfwbqykcje;
	// 应税服务扣除项目_本期实际扣除金额
	@TaxExcelPos(row = 14, col = 6)
	private DZFDouble msdysfwbqsjkcje;
	// 本期发生额
	@TaxExcelPos(row = 14, col = 4)
	private DZFDouble msdysfwbqfse;

	public DZFDouble getYxdcznfwqcye() {
		return yxdcznfwqcye;
	}

	public void setYxdcznfwqcye(DZFDouble yxdcznfwqcye) {
		this.yxdcznfwqcye = yxdcznfwqcye;
	}

	public DZFDouble getYxdcznfwqmye() {
		return yxdcznfwqmye;
	}

	public void setYxdcznfwqmye(DZFDouble yxdcznfwqmye) {
		this.yxdcznfwqmye = yxdcznfwqmye;
	}

	public DZFDouble getYxdcznfwhje() {
		return yxdcznfwhje;
	}

	public void setYxdcznfwhje(DZFDouble yxdcznfwhje) {
		this.yxdcznfwhje = yxdcznfwhje;
	}

	public DZFDouble getYxdcznfwbqykcje() {
		return yxdcznfwbqykcje;
	}

	public void setYxdcznfwbqykcje(DZFDouble yxdcznfwbqykcje) {
		this.yxdcznfwbqykcje = yxdcznfwbqykcje;
	}

	public DZFDouble getYxdcznfwbqsjkcje() {
		return yxdcznfwbqsjkcje;
	}

	public void setYxdcznfwbqsjkcje(DZFDouble yxdcznfwbqsjkcje) {
		this.yxdcznfwbqsjkcje = yxdcznfwbqsjkcje;
	}

	public DZFDouble getYxdcznfwbqfse() {
		return yxdcznfwbqfse;
	}

	public void setYxdcznfwbqfse(DZFDouble yxdcznfwbqfse) {
		this.yxdcznfwbqfse = yxdcznfwbqfse;
	}

	public DZFDouble getEslysfwqcye() {
		return eslysfwqcye;
	}

	public void setEslysfwqcye(DZFDouble eslysfwqcye) {
		this.eslysfwqcye = eslysfwqcye;
	}

	public DZFDouble getEslysfwqmye() {
		return eslysfwqmye;
	}

	public void setEslysfwqmye(DZFDouble eslysfwqmye) {
		this.eslysfwqmye = eslysfwqmye;
	}

	public DZFDouble getEslysfwhje() {
		return eslysfwhje;
	}

	public void setEslysfwhje(DZFDouble eslysfwhje) {
		this.eslysfwhje = eslysfwhje;
	}

	public DZFDouble getEslysfwbqykcje() {
		return eslysfwbqykcje;
	}

	public void setEslysfwbqykcje(DZFDouble eslysfwbqykcje) {
		this.eslysfwbqykcje = eslysfwbqykcje;
	}

	public DZFDouble getEslysfwbqsjkcje() {
		return eslysfwbqsjkcje;
	}

	public void setEslysfwbqsjkcje(DZFDouble eslysfwbqsjkcje) {
		this.eslysfwbqsjkcje = eslysfwbqsjkcje;
	}

	public DZFDouble getEslysfwbqfse() {
		return eslysfwbqfse;
	}

	public void setEslysfwbqfse(DZFDouble eslysfwbqfse) {
		this.eslysfwbqfse = eslysfwbqfse;
	}

	public DZFDouble getSslysfwqcye() {
		return sslysfwqcye;
	}

	public void setSslysfwqcye(DZFDouble sslysfwqcye) {
		this.sslysfwqcye = sslysfwqcye;
	}

	public DZFDouble getSslysfwqmye() {
		return sslysfwqmye;
	}

	public void setSslysfwqmye(DZFDouble sslysfwqmye) {
		this.sslysfwqmye = sslysfwqmye;
	}

	public DZFDouble getSslysfwhje() {
		return sslysfwhje;
	}

	public void setSslysfwhje(DZFDouble sslysfwhje) {
		this.sslysfwhje = sslysfwhje;
	}

	public DZFDouble getSslysfwbqykcje() {
		return sslysfwbqykcje;
	}

	public void setSslysfwbqykcje(DZFDouble sslysfwbqykcje) {
		this.sslysfwbqykcje = sslysfwbqykcje;
	}

	public DZFDouble getSslysfwbqsjkcje() {
		return sslysfwbqsjkcje;
	}

	public void setSslysfwbqsjkcje(DZFDouble sslysfwbqsjkcje) {
		this.sslysfwbqsjkcje = sslysfwbqsjkcje;
	}

	public DZFDouble getSslysfwbqfse() {
		return sslysfwbqfse;
	}

	public void setSslysfwbqfse(DZFDouble sslysfwbqfse) {
		this.sslysfwbqfse = sslysfwbqfse;
	}

	public DZFDouble getSslysfwqcyenew() {
		return sslysfwqcyenew;
	}

	public void setSslysfwqcyenew(DZFDouble sslysfwqcyenew) {
		this.sslysfwqcyenew = sslysfwqcyenew;
	}

	public DZFDouble getSslysfwqmyenew() {
		return sslysfwqmyenew;
	}

	public void setSslysfwqmyenew(DZFDouble sslysfwqmyenew) {
		this.sslysfwqmyenew = sslysfwqmyenew;
	}

	public DZFDouble getSslysfwhjenew() {
		return sslysfwhjenew;
	}

	public void setSslysfwhjenew(DZFDouble sslysfwhjenew) {
		this.sslysfwhjenew = sslysfwhjenew;
	}

	public DZFDouble getSslysfwbqykcjenew() {
		return sslysfwbqykcjenew;
	}

	public void setSslysfwbqykcjenew(DZFDouble sslysfwbqykcjenew) {
		this.sslysfwbqykcjenew = sslysfwbqykcjenew;
	}

	public DZFDouble getSslysfwbqsjkcjenew() {
		return sslysfwbqsjkcjenew;
	}

	public void setSslysfwbqsjkcjenew(DZFDouble sslysfwbqsjkcjenew) {
		this.sslysfwbqsjkcjenew = sslysfwbqsjkcjenew;
	}

	public DZFDouble getSslysfwbqfsenew() {
		return sslysfwbqfsenew;
	}

	public void setSslysfwbqfsenew(DZFDouble sslysfwbqfsenew) {
		this.sslysfwbqfsenew = sslysfwbqfsenew;
	}

	public DZFDouble getSslysfw5qcyenew() {
		return sslysfw5qcyenew;
	}

	public void setSslysfw5qcyenew(DZFDouble sslysfw5qcyenew) {
		this.sslysfw5qcyenew = sslysfw5qcyenew;
	}

	public DZFDouble getSslysfw5qmyenew() {
		return sslysfw5qmyenew;
	}

	public void setSslysfw5qmyenew(DZFDouble sslysfw5qmyenew) {
		this.sslysfw5qmyenew = sslysfw5qmyenew;
	}

	public DZFDouble getSslysfw5hjenew() {
		return sslysfw5hjenew;
	}

	public void setSslysfw5hjenew(DZFDouble sslysfw5hjenew) {
		this.sslysfw5hjenew = sslysfw5hjenew;
	}

	public DZFDouble getSslysfw5bqykcjenew() {
		return sslysfw5bqykcjenew;
	}

	public void setSslysfw5bqykcjenew(DZFDouble sslysfw5bqykcjenew) {
		this.sslysfw5bqykcjenew = sslysfw5bqykcjenew;
	}

	public DZFDouble getSslysfw5bqsjkcjenew() {
		return sslysfw5bqsjkcjenew;
	}

	public void setSslysfw5bqsjkcjenew(DZFDouble sslysfw5bqsjkcjenew) {
		this.sslysfw5bqsjkcjenew = sslysfw5bqsjkcjenew;
	}

	public DZFDouble getSslysfw5bqfsenew() {
		return sslysfw5bqfsenew;
	}

	public void setSslysfw5bqfsenew(DZFDouble sslysfw5bqfsenew) {
		this.sslysfw5bqfsenew = sslysfw5bqfsenew;
	}

	public DZFDouble getTslysfwqcye() {
		return tslysfwqcye;
	}

	public void setTslysfwqcye(DZFDouble tslysfwqcye) {
		this.tslysfwqcye = tslysfwqcye;
	}

	public DZFDouble getTslysfwqmye() {
		return tslysfwqmye;
	}

	public void setTslysfwqmye(DZFDouble tslysfwqmye) {
		this.tslysfwqmye = tslysfwqmye;
	}

	public DZFDouble getTslysfwhje() {
		return tslysfwhje;
	}

	public void setTslysfwhje(DZFDouble tslysfwhje) {
		this.tslysfwhje = tslysfwhje;
	}

	public DZFDouble getTslysfwbqykcje() {
		return tslysfwbqykcje;
	}

	public void setTslysfwbqykcje(DZFDouble tslysfwbqykcje) {
		this.tslysfwbqykcje = tslysfwbqykcje;
	}

	public DZFDouble getTslysfwbqsjkcje() {
		return tslysfwbqsjkcje;
	}

	public void setTslysfwbqsjkcje(DZFDouble tslysfwbqsjkcje) {
		this.tslysfwbqsjkcje = tslysfwbqsjkcje;
	}

	public DZFDouble getTslysfwbqfse() {
		return tslysfwbqfse;
	}

	public void setTslysfwbqfse(DZFDouble tslysfwbqfse) {
		this.tslysfwbqfse = tslysfwbqfse;
	}

	public DZFDouble getMdtsdysfwqcye() {
		return mdtsdysfwqcye;
	}

	public void setMdtsdysfwqcye(DZFDouble mdtsdysfwqcye) {
		this.mdtsdysfwqcye = mdtsdysfwqcye;
	}

	public DZFDouble getMdtsdysfwqmye() {
		return mdtsdysfwqmye;
	}

	public void setMdtsdysfwqmye(DZFDouble mdtsdysfwqmye) {
		this.mdtsdysfwqmye = mdtsdysfwqmye;
	}

	public DZFDouble getMdtsdysfwhje() {
		return mdtsdysfwhje;
	}

	public void setMdtsdysfwhje(DZFDouble mdtsdysfwhje) {
		this.mdtsdysfwhje = mdtsdysfwhje;
	}

	public DZFDouble getMdtsdysfwbqykcje() {
		return mdtsdysfwbqykcje;
	}

	public void setMdtsdysfwbqykcje(DZFDouble mdtsdysfwbqykcje) {
		this.mdtsdysfwbqykcje = mdtsdysfwbqykcje;
	}

	public DZFDouble getMdtsdysfwbqsjkcje() {
		return mdtsdysfwbqsjkcje;
	}

	public void setMdtsdysfwbqsjkcje(DZFDouble mdtsdysfwbqsjkcje) {
		this.mdtsdysfwbqsjkcje = mdtsdysfwbqsjkcje;
	}

	public DZFDouble getMdtsdysfwbqfse() {
		return mdtsdysfwbqfse;
	}

	public void setMdtsdysfwbqfse(DZFDouble mdtsdysfwbqfse) {
		this.mdtsdysfwbqfse = mdtsdysfwbqfse;
	}

	public DZFDouble getMsdysfwqcye() {
		return msdysfwqcye;
	}

	public void setMsdysfwqcye(DZFDouble msdysfwqcye) {
		this.msdysfwqcye = msdysfwqcye;
	}

	public DZFDouble getMsdysfwqmye() {
		return msdysfwqmye;
	}

	public void setMsdysfwqmye(DZFDouble msdysfwqmye) {
		this.msdysfwqmye = msdysfwqmye;
	}

	public DZFDouble getMsdysfwhje() {
		return msdysfwhje;
	}

	public void setMsdysfwhje(DZFDouble msdysfwhje) {
		this.msdysfwhje = msdysfwhje;
	}

	public DZFDouble getMsdysfwbqykcje() {
		return msdysfwbqykcje;
	}

	public void setMsdysfwbqykcje(DZFDouble msdysfwbqykcje) {
		this.msdysfwbqykcje = msdysfwbqykcje;
	}

	public DZFDouble getMsdysfwbqsjkcje() {
		return msdysfwbqsjkcje;
	}

	public void setMsdysfwbqsjkcje(DZFDouble msdysfwbqsjkcje) {
		this.msdysfwbqsjkcje = msdysfwbqsjkcje;
	}

	public DZFDouble getMsdysfwbqfse() {
		return msdysfwbqfse;
	}

	public void setMsdysfwbqfse(DZFDouble msdysfwbqfse) {
		this.msdysfwbqfse = msdysfwbqfse;
	}

}
