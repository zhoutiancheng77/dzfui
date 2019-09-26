package com.dzf.zxkj.platform.model.report;

import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDouble;

/**
 * 资产负债表
 * 
 */
public class ZcFzBVO extends SuperVO {

	//打印时  标题显示的区间区间
	public String titlePeriod;
	
	public String period;//期间
	// 公司
	public String gs;
	// 资产
	public String zc;

	// 行次1
	public String hc1;

	// 期末余额1
	public DZFDouble qmye1;

	// 年初余额
	public DZFDouble ncye1;

	// 负债和所有者权益(或股东权益）
	public String fzhsyzqy;

	// 行次
	public String hc2;

	// 期末余额
	public DZFDouble qmye2;

	// 年初余额
	public DZFDouble ncye2;
	
	public String pk_corp;
	
	public DZFBoolean isseven;
	
	private String zcconkms;
	
	private String fzconkms;

	//期初
	public DZFDouble qcye1;
	public DZFDouble qcye2;
	
	public Integer colspan;//合并的行数
	
	public String hc1_id;//系统行次(不可随意更改)
	
	public String hc2_id;//系统行次(不可随意更改)
	@Override
	public String getPKFieldName() {
		return null;
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return null;
	}

	public String getFzhsyzqy() {
		return fzhsyzqy;
	}

	public void setFzhsyzqy(String fzhsyzqy) {
		this.fzhsyzqy = fzhsyzqy;
	}

	public String getHc1() {
		return hc1;
	}

	public void setHc1(String hc1) {
		this.hc1 = hc1;
	}

	public String getHc2() {
		return hc2;
	}

	public void setHc2(String hc2) {
		this.hc2 = hc2;
	}

	public DZFDouble getNcye1() {
		return ncye1;
	}

	public void setNcye1(DZFDouble ncye1) {
		this.ncye1 = ncye1;
	}

	public DZFDouble getNcye2() {
		return ncye2;
	}

	public void setNcye2(DZFDouble ncye2) {
		this.ncye2 = ncye2;
	}

	public DZFDouble getQmye1() {
		return qmye1;
	}

	public void setQmye1(DZFDouble qmye1) {
		this.qmye1 = qmye1;
	}

	public DZFDouble getQmye2() {
		return qmye2;
	}

	public void setQmye2(DZFDouble qmye2) {
		this.qmye2 = qmye2;
	}

	public String getZc() {
		return zc;
	}

	public void setZc(String zc) {
		this.zc = zc;
	}

	public String getTitlePeriod() {
		return titlePeriod;
	}

	public void setTitlePeriod(String titlePeriod) {
		this.titlePeriod = titlePeriod;
	}

	public String getGs() {
		return gs;
	}

	public void setGs(String gs) {
		this.gs = gs;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public DZFBoolean getIsseven() {
		return isseven;
	}

	public void setIsseven(DZFBoolean isseven) {
		this.isseven = isseven;
	}

	public String getZcconkms() {
		return zcconkms;
	}

	public void setZcconkms(String zcconkms) {
		this.zcconkms = zcconkms;
	}

	public String getFzconkms() {
		return fzconkms;
	}

	public void setFzconkms(String fzconkms) {
		this.fzconkms = fzconkms;
	}

	public DZFDouble getQcye1() {
		return qcye1;
	}

	public void setQcye1(DZFDouble qcye1) {
		this.qcye1 = qcye1;
	}

	public DZFDouble getQcye2() {
		return qcye2;
	}

	public void setQcye2(DZFDouble qcye2) {
		this.qcye2 = qcye2;
	}

	public Integer getColspan() {
		return colspan;
	}

	public void setColspan(Integer colspan) {
		this.colspan = colspan;
	}

	public String getHc1_id() {
		return hc1_id;
	}

	public void setHc1_id(String hc1_id) {
		this.hc1_id = hc1_id;
	}

	public String getHc2_id() {
		return hc2_id;
	}

	public void setHc2_id(String hc2_id) {
		this.hc2_id = hc2_id;
	}
	
	
	
}
