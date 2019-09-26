package com.dzf.zxkj.platform.model.zcgl;


import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDouble;

public class ZcdzVO extends SuperVO {

	private static final long serialVersionUID = -6669901048007509246L;

	// 期间
	private String titlePeriod;
	
	
	private String period;
	
	private String gs;
	
	private String zcsx;
	private String zclb;
	private String zckm;
	private String zzkmbh;
	private String zzkmmc;
	private String qj;
	private DZFDouble zcje;
	private DZFDouble zzje;
	

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

	public String getQj() {
		return qj;
	}

	public void setQj(String qj) {
		this.qj = qj;
	}

	public String getZcsx() {
		return zcsx;
	}

	public void setZcsx(String zcsx) {
		this.zcsx = zcsx;
	}

	public String getZclb() {
		return zclb;
	}

	public void setZclb(String zclb) {
		this.zclb = zclb;
	}

	public String getZckm() {
		return zckm;
	}

	public void setZckm(String zckm) {
		this.zckm = zckm;
	}

	public String getZzkmbh() {
		return zzkmbh;
	}

	public void setZzkmbh(String zzkmbh) {
		this.zzkmbh = zzkmbh;
	}

	public String getZzkmmc() {
		return zzkmmc;
	}

	public void setZzkmmc(String zzkmmc) {
		this.zzkmmc = zzkmmc;
	}

	public DZFDouble getZcje() {
		return zcje;
	}

	public void setZcje(DZFDouble zcje) {
		this.zcje = zcje;
	}

	public DZFDouble getZzje() {
		return zzje;
	}

	public void setZzje(DZFDouble zzje) {
		this.zzje = zzje;
	}

	@Override
	public String getPKFieldName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getParentPKFieldName() {
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
	
	
}
