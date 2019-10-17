package com.dzf.zxkj.platform.model.report;

import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;

public class YyFpVO extends SuperVO {

	private String xm1;

	private String hc1;

	private DZFDouble je1;

	private String formula1;// 公式

	private String xm2;

	private String hc2;

	private DZFDouble je2;

	private String formula2;// 公式2

	private String gs;
	private String titlePeriod;

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

	public String getFormula1() {
		return formula1;
	}

	public void setFormula1(String formula1) {
		this.formula1 = formula1;
	}

	public String getFormula2() {
		return formula2;
	}

	public void setFormula2(String formula2) {
		this.formula2 = formula2;
	}

	public String getXm1() {
		return xm1;
	}

	public void setXm1(String xm1) {
		this.xm1 = xm1;
	}

	public String getHc1() {
		return hc1;
	}

	public void setHc1(String hc1) {
		this.hc1 = hc1;
	}

	public DZFDouble getJe1() {
		return je1;
	}

	public void setJe1(DZFDouble je1) {
		this.je1 = je1;
	}

	public String getXm2() {
		return xm2;
	}

	public void setXm2(String xm2) {
		this.xm2 = xm2;
	}

	public String getHc2() {
		return hc2;
	}

	public void setHc2(String hc2) {
		this.hc2 = hc2;
	}

	public DZFDouble getJe2() {
		return je2;
	}

	public void setJe2(DZFDouble je2) {
		this.je2 = je2;
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
