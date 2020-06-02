package com.dzf.zxkj.app.model.org;


import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.model.SuperVO;

public class TempSvOrgVO extends SuperVO {

	private String pk_temp_corp;
	private String pk_temp_svorg;
	private String pk_svorg;
	private String pk_account;
	private String bsign;
	private String bconfig;
	private DZFDateTime ts;
	private Integer dr;
	private String pk_corpk;//小企业公司id
	private String chargedeptname;//公司性质
	
	private String corpcode;
	private String corpname;
	private String industry;
	private String corptype;
	
	
	
	public String getChargedeptname() {
		return chargedeptname;
	}
	public void setChargedeptname(String chargedeptname) {
		this.chargedeptname = chargedeptname;
	}
	public String getPk_corpk() {
		return pk_corpk;
	}
	public void setPk_corpk(String pk_corpk) {
		this.pk_corpk = pk_corpk;
	}
	public String getCorpcode() {
		return corpcode;
	}
	public void setCorpcode(String corpcode) {
		this.corpcode = corpcode;
	}
	
	public String getCorpname() {
		return corpname;
	}
	public void setCorpname(String corpname) {
		this.corpname = corpname;
	}
	public String getIndustry() {
		return industry;
	}
	public void setIndustry(String industry) {
		this.industry = industry;
	}
	public String getCorptype() {
		return corptype;
	}
	public void setCorptype(String corptype) {
		this.corptype = corptype;
	}
	
	public String getBsign() {
		return bsign;
	}
	public void setBsign(String bsign) {
		this.bsign = bsign;
	}
	public String getBconfig() {
		return bconfig;
	}
	public void setBconfig(String bconfig) {
		this.bconfig = bconfig;
	}
	public String getPk_account() {
		return pk_account;
	}
	public void setPk_account(String pk_account) {
		this.pk_account = pk_account;
	}
	public String getPk_temp_corp() {
		return pk_temp_corp;
	}
	public void setPk_temp_corp(String pk_temp_corp) {
		this.pk_temp_corp = pk_temp_corp;
	}
	public String getPk_temp_svorg() {
		return pk_temp_svorg;
	}
	public void setPk_temp_svorg(String pk_temp_svorg) {
		this.pk_temp_svorg = pk_temp_svorg;
	}
	public String getPk_svorg() {
		return pk_svorg;
	}
	public void setPk_svorg(String pk_svorg) {
		this.pk_svorg = pk_svorg;
	}
	public DZFDateTime getTs() {
		return ts;
	}
	public void setTs(DZFDateTime ts) {
		this.ts = ts;
	}
	public Integer getDr() {
		return dr;
	}
	public void setDr(Integer dr) {
		this.dr = dr;
	}
	@Override
	public String getParentPKFieldName() {
		return "pk_temp_corp";
	}
	@Override
	public String getPKFieldName() {
		return "pk_temp_svorg";
	}
	@Override
	public String getTableName() {
		return "app_temp_svorg";
	}
}
