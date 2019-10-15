package com.dzf.zxkj.platform.model.report;


import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;

/**
 * 权益变动表
 * 
 * @author zhangj
 *
 */
public class QyBdVO extends SuperVO {

	private String xm;

	private String hc;

	private DZFDouble sq_je;// 上年数

	private DZFDouble bn_je;// 本年数
	
	private String formula ;//公式
	
	private String gs;
	
	private String titlePeriod;
	
	public String getFormula() {
		return formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}

	public String getGs() {
		return gs;
	}

	public void setGs(String gs) {
		this.gs = gs;
	}

	public String getTitlePeriod() {
		return titlePeriod;
	}

	public void setTitlePeriod(String titlePeriod) {
		this.titlePeriod = titlePeriod;
	}

	public String getXm() {
		return xm;
	}

	public void setXm(String xm) {
		this.xm = xm;
	}

	public String getHc() {
		return hc;
	}

	public void setHc(String hc) {
		this.hc = hc;
	}

	public DZFDouble getSq_je() {
		return sq_je;
	}

	public void setSq_je(DZFDouble sq_je) {
		this.sq_je = sq_je;
	}

	public DZFDouble getBn_je() {
		return bn_je;
	}

	public void setBn_je(DZFDouble bn_je) {
		this.bn_je = bn_je;
	}

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

}
