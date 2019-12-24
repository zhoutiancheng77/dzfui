package com.dzf.zxkj.platform.model.tax.cqtc;

import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.model.SuperVO;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CQTaxReportVO extends SuperVO {

	//主键
	private String pk_taxreport;
	//公司	
	private String pk_corp;
	
	public String unitname;//企业名称
	//纳税人识别号
	@JsonProperty("vscode")
	private String nsrsbh;
	//征收项目代码	例如：1:增值税
	private String zsxm_dm;
	//申报种类编号	
	private String sb_zlbh;
	//填报周期	
	private Integer periodtype;
	//税款所属时间起	
	private String periodfrom;
	//税款所属时间止	
	private String periodto;
	//申报状态代码
	private String sbzt_dm;
	//时间戳	
	private DZFDateTime ts;
	//删除标志	
	private Integer dr;
	
	private String openId;//接口参数OPENID
	private String period;//接口参数期间
	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}
	
	public CQTaxReportVO() {
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getPKFieldName() {
		return "pk_taxreport";
	}

	@Override
	public String getTableName() {
		return "ynt_taxreport";
	}

	public String getPk_taxreport() {
		return pk_taxreport;
	}

	public String getPk_corp() {
		return pk_corp;
	}
	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}


	public String getZsxm_dm() {
		return zsxm_dm;
	}

	public Integer getPeriodtype() {
		return periodtype;
	}

	public String getPeriodfrom() {
		return periodfrom;
	}

	public String getPeriodto() {
		return periodto;
	}

	public String getSbzt_dm() {
		return sbzt_dm;
	}

	public DZFDateTime getTs() {
		return ts;
	}

	public Integer getDr() {
		return dr;
	}

	public void setPk_taxreport(String pk_taxreport) {
		this.pk_taxreport = pk_taxreport;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public void setZsxm_dm(String zsxm_dm) {
		this.zsxm_dm = zsxm_dm;
	}

	public void setPeriodtype(Integer periodtype) {
		this.periodtype = periodtype;
	}

	public void setPeriodfrom(String periodfrom) {
		this.periodfrom = periodfrom;
	}

	public void setPeriodto(String periodto) {
		this.periodto = periodto;
	}

	public void setSbzt_dm(String sbzt_dm) {
		this.sbzt_dm = sbzt_dm;
	}

	public void setTs(DZFDateTime ts) {
		this.ts = ts;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
	}

	public String getSb_zlbh() {
		return sb_zlbh;
	}

	public void setSb_zlbh(String sb_zlbh) {
		this.sb_zlbh = sb_zlbh;
	}

	public String getNsrsbh() {
		return nsrsbh;
	}

	public void setNsrsbh(String nsrsbh) {
		this.nsrsbh = nsrsbh;
	}

	public String getUnitname() {
		return unitname;
	}

	public void setUnitname(String unitname) {
		this.unitname = unitname;
	}
	
}
