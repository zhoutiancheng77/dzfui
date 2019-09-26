package com.dzf.zxkj.platform.model.zcgl;


import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDouble;

/**
 * 资产总账VO
 * @author zhangj
 *
 */
public class ZcZzVO extends SuperVO {
	
	
	private String titlePeriod;
	
	private String period;
	private String gs;
	private String qj ;
	private String zy ;
	private DZFDouble yzjf ;
	private DZFDouble yzdf ;
	private DZFDouble yzye ;
	private DZFDouble ljjf ;
	private DZFDouble ljdf ;
	private DZFDouble ljye ;
	private DZFDouble jzye ;
	private String zclb ;
	private String zclbbm;
	private String zcsx;

	public String getZcsx() {
		return zcsx;
	}

	public void setZcsx(String zcsx) {
		this.zcsx = zcsx;
	}

	public String getZclbbm() {
		return zclbbm;
	}

	public void setZclbbm(String zclbbm) {
		this.zclbbm = zclbbm;
	}

	public String getZclb() {
		return zclb;
	}
	public void setZclb(String zclb) {
		this.zclb = zclb;
	}
	public String getQj() {
		return qj;
	}
	public void setQj(String qj) {
		this.qj = qj;
	}
	public String getZy() {
		return zy;
	}
	public void setZy(String zy) {
		this.zy = zy;
	}
	public DZFDouble getYzjf() {
		return yzjf;
	}
	public void setYzjf(DZFDouble yzjf) {
		this.yzjf = yzjf;
	}
	public DZFDouble getYzdf() {
		return yzdf;
	}
	public void setYzdf(DZFDouble yzdf) {
		this.yzdf = yzdf;
	}
	public DZFDouble getYzye() {
		return yzye;
	}
	public void setYzye(DZFDouble yzye) {
		this.yzye = yzye;
	}
	public DZFDouble getLjjf() {
		return ljjf;
	}
	public void setLjjf(DZFDouble ljjf) {
		this.ljjf = ljjf;
	}
	public DZFDouble getLjdf() {
		return ljdf;
	}
	public void setLjdf(DZFDouble ljdf) {
		this.ljdf = ljdf;
	}
	public DZFDouble getLjye() {
		return ljye;
	}
	public void setLjye(DZFDouble ljye) {
		this.ljye = ljye;
	}
	public DZFDouble getJzye() {
		return jzye;
	}
	public void setJzye(DZFDouble jzye) {
		this.jzye = jzye;
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
	public String getPeriod() {
		return period;
	}
	public void setPeriod(String period) {
		this.period = period;
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
