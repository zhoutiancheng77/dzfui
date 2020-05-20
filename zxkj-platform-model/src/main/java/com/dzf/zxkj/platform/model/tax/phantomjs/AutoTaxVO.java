package com.dzf.zxkj.platform.model.tax.phantomjs;

import com.dzf.zxkj.common.model.SuperVO;

public class AutoTaxVO extends SuperVO {

	private String url;

	private String token;
	private String clientid;
	private String clientpk_corp;
	private String clientuserid;
	private String logindate;
	private String corpid;
	private String corpname;
	private String ccounty;
	private String pk_taxreport;
	private String sbcode;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getClientid() {
		return clientid;
	}

	public void setClientid(String clientid) {
		this.clientid = clientid;
	}

	public String getClientpk_corp() {
		return clientpk_corp;
	}

	public void setClientpk_corp(String clientpk_corp) {
		this.clientpk_corp = clientpk_corp;
	}

	public String getClientuserid() {
		return clientuserid;
	}

	public void setClientuserid(String clientuserid) {
		this.clientuserid = clientuserid;
	}

	public String getLogindate() {
		return logindate;
	}

	public void setLogindate(String logindate) {
		this.logindate = logindate;
	}

	public String getCorpid() {
		return corpid;
	}

	public void setCorpid(String corpid) {
		this.corpid = corpid;
	}

	public String getCorpname() {
		return corpname;
	}

	public void setCorpname(String corpname) {
		this.corpname = corpname;
	}

	public String getCcounty() {
		return ccounty;
	}

	public void setCcounty(String ccounty) {
		this.ccounty = ccounty;
	}

	public String getPk_taxreport() {
		return pk_taxreport;
	}

	public void setPk_taxreport(String pk_taxreport) {
		this.pk_taxreport = pk_taxreport;
	}

	public String getSbcode() {
		return sbcode;
	}

	public void setSbcode(String sbcode) {
		this.sbcode = sbcode;
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
