package com.dzf.zxkj.platform.model.report;

import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;

/**
 * 业务活动vo
 * 
 * @author zhangj
 * 
 */
public class YwHdVO extends SuperVO {

	private String xm;
	private String hs;

	private DZFDouble monfxdx;
	private DZFDouble monxdx;
	private DZFDouble monhj;
	private DZFDouble yearfxdx;
	private DZFDouble yearxdx;
	private DZFDouble yearhj;

	private String gs;
	private String titlePeriod;
	
	
	public String getXm() {
		return xm;
	}

	public void setXm(String xm) {
		this.xm = xm;
	}

	public String getHs() {
		return hs;
	}

	public void setHs(String hs) {
		this.hs = hs;
	}

	public DZFDouble getMonfxdx() {
		return monfxdx;
	}

	public void setMonfxdx(DZFDouble monfxdx) {
		this.monfxdx = monfxdx;
	}

	public DZFDouble getMonxdx() {
		return monxdx;
	}

	public void setMonxdx(DZFDouble monxdx) {
		this.monxdx = monxdx;
	}

	public DZFDouble getMonhj() {
		return monhj;
	}

	public void setMonhj(DZFDouble monhj) {
		this.monhj = monhj;
	}

	public DZFDouble getYearfxdx() {
		return yearfxdx;
	}

	public void setYearfxdx(DZFDouble yearfxdx) {
		this.yearfxdx = yearfxdx;
	}

	public DZFDouble getYearxdx() {
		return yearxdx;
	}

	public void setYearxdx(DZFDouble yearxdx) {
		this.yearxdx = yearxdx;
	}

	public DZFDouble getYearhj() {
		return yearhj;
	}

	public void setYearhj(DZFDouble yearhj) {
		this.yearhj = yearhj;
	}

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

}
