package com.dzf.zxkj.platform.model.tax;

import com.dzf.zxkj.common.model.SuperVO;

/**
 * 完税凭证
 *
 */
public class TaxPaymentVO extends SuperVO {
	// 税款所属期起
	private String skssqq;
	// 缴款日期
	private String jkrq;
	// 税款所属期
	private String skssq;
	// 税款属性名称
	private String sksxmc;
	// 税款种类名称
	private String skzlmc;
	// 税款所属期止
	private String skssqz;
	// 实缴税额
	private String sjse;
	// 实缴税额大写
	private String sjsesumbig;
	// 征收项目名称
	private String zsxmmc;
	// 电子税票号码
	private String sphm;

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return null;
	}

	public String getSkssqq() {
		return skssqq;
	}

	public void setSkssqq(String skssqq) {
		this.skssqq = skssqq;
	}

	public String getJkrq() {
		return jkrq;
	}

	public void setJkrq(String jkrq) {
		this.jkrq = jkrq;
	}

	public String getSkssq() {
		return skssq;
	}

	public void setSkssq(String skssq) {
		this.skssq = skssq;
	}

	public String getSksxmc() {
		return sksxmc;
	}

	public void setSksxmc(String sksxmc) {
		this.sksxmc = sksxmc;
	}

	public String getSkzlmc() {
		return skzlmc;
	}

	public void setSkzlmc(String skzlmc) {
		this.skzlmc = skzlmc;
	}

	public String getSkssqz() {
		return skssqz;
	}

	public void setSkssqz(String skssqz) {
		this.skssqz = skssqz;
	}

	public String getSjse() {
		return sjse;
	}

	public void setSjse(String sjse) {
		this.sjse = sjse;
	}

	public String getSjsesumbig() {
		return sjsesumbig;
	}

	public void setSjsesumbig(String sjsesumbig) {
		this.sjsesumbig = sjsesumbig;
	}

	public String getZsxmmc() {
		return zsxmmc;
	}

	public void setZsxmmc(String zsxmmc) {
		this.zsxmmc = zsxmmc;
	}

	public String getSphm() {
		return sphm;
	}

	public void setSphm(String sphm) {
		this.sphm = sphm;
	}

}
