package com.dzf.zxkj.platform.model.report;

import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;

/**
 * 收入支出VO(事业)
 * 
 * @author zhangj
 * 
 */
public class SrzcBVO extends SuperVO<SrzcBVO> {

	private String xm;
	private DZFDouble monnum;
	private DZFDouble yearnum;
	private String gs;
	private String titlePeriod;
	
	public String getXm() {
		return xm;
	}

	public void setXm(String xm) {
		this.xm = xm;
	}

	public DZFDouble getMonnum() {
		return monnum;
	}

	public void setMonnum(DZFDouble monnum) {
		this.monnum = monnum;
	}

	public DZFDouble getYearnum() {
		return yearnum;
	}

	public void setYearnum(DZFDouble yearnum) {
		this.yearnum = yearnum;
	}

	@Override
	public String getParentPKFieldName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPKFieldName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return null;
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

}
