package com.dzf.zxkj.platform.model.report;

import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 现金流量明细
 * 
 * @author zhangj
 * 
 */
public class XjllMxvo extends SuperVO {

	private String xm;
	private String xmcode;
	private String pzh;
	private DZFDate dopedate;
	private String kmcode;
	private String kmmc;
	private DZFDouble jffs;
	private DZFDouble ddfs;
	private String currname;
	private String pk_curreny;// 币种
	@JsonProperty("pzid")
	private String pk_tzpz_h;
	private String zy;
	private String code;
	private String name;

	private String gs;

	private String period;// 期间

	// 打印时 标题显示的区间区间
	private String titlePeriod;

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

	public String getXmcode() {
		return xmcode;
	}

	public void setXmcode(String xmcode) {
		this.xmcode = xmcode;
	}

	public String getXm() {
		return xm;
	}

	public void setXm(String xm) {
		this.xm = xm;
	}

	public String getZy() {
		return zy;
	}

	public void setZy(String zy) {
		this.zy = zy;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPk_tzpz_h() {
		return pk_tzpz_h;
	}

	public void setPk_tzpz_h(String pk_tzpz_h) {
		this.pk_tzpz_h = pk_tzpz_h;
	}

	public String getCurrname() {
		return currname;
	}

	public void setCurrname(String currname) {
		this.currname = currname;
	}

	public String getPzh() {
		return pzh;
	}

	public void setPzh(String pzh) {
		this.pzh = pzh;
	}

	public DZFDate getDopedate() {
		return dopedate;
	}

	public void setDopedate(DZFDate dopedate) {
		this.dopedate = dopedate;
	}

	public String getKmcode() {
		return kmcode;
	}

	public void setKmcode(String kmcode) {
		this.kmcode = kmcode;
	}

	public String getKmmc() {
		return kmmc;
	}

	public void setKmmc(String kmmc) {
		this.kmmc = kmmc;
	}

	public DZFDouble getJffs() {
		return jffs;
	}

	public void setJffs(DZFDouble jffs) {
		this.jffs = jffs;
	}

	public DZFDouble getDdfs() {
		return ddfs;
	}

	public void setDdfs(DZFDouble ddfs) {
		this.ddfs = ddfs;
	}

	public String getPk_curreny() {
		return pk_curreny;
	}

	public void setPk_curreny(String pk_curreny) {
		this.pk_curreny = pk_curreny;
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

}
