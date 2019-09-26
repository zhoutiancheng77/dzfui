package com.dzf.zxkj.platform.model.tax;

import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDateTime;

public class TaxReportEntVO extends SuperVO {


	//主键
	private String pk_taxreportent;
	//报表主键
	private String pk_taxreport;
	//公司	
	private String pk_corp;
	//代账公司
	private String pk_corp_account;
	//填报周期	
	private Integer periodtype;
	//单据状态
	private Integer vbillstatus;
	//企业主确认内容
	private String entmanagerinfo;
	//发送企业主确认时间
	private String entdate;
	//时间戳	
	private DZFDateTime ts;
	//删除标志	
	private Integer dr;
	
	
	
	
	

	public TaxReportEntVO() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getParentPKFieldName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPKFieldName() {
		// TODO Auto-generated method stub
		return "pk_taxreportent";
	}

	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return "ynt_taxreportent";
	}

	public String getPk_taxreportent() {
		return pk_taxreportent;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public Integer getPeriodtype() {
		return periodtype;
	}

	public Integer getVbillstatus() {
		return vbillstatus;
	}

	public DZFDateTime getTs() {
		return ts;
	}

	public Integer getDr() {
		return dr;
	}

	public void setPk_taxreportent(String pk_taxreportent) {
		this.pk_taxreportent = pk_taxreportent;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public void setPeriodtype(Integer periodtype) {
		this.periodtype = periodtype;
	}


	public void setVbillstatus(Integer vbillstatus) {
		this.vbillstatus = vbillstatus;
	}


	public void setTs(DZFDateTime ts) {
		this.ts = ts;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
	}

	public String getEntmanagerinfo() {
		return entmanagerinfo;
	}

	public void setEntmanagerinfo(String entmanagerinfo) {
		this.entmanagerinfo = entmanagerinfo;
	}

	public String getEntdate() {
		return entdate;
	}

	public void setEntdate(String entdate) {
		this.entdate = entdate;
	}

	public String getPk_corp_account() {
		return pk_corp_account;
	}

	public void setPk_corp_account(String pk_corp_account) {
		this.pk_corp_account = pk_corp_account;
	}

	public String getPk_taxreport() {
		return pk_taxreport;
	}

	public void setPk_taxreport(String pk_taxreport) {
		this.pk_taxreport = pk_taxreport;
	}

}
