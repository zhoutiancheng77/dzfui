package com.dzf.zxkj.platform.model.tax.phantomjs;

import com.dzf.zxkj.common.model.SuperVO;

public class AutoTaxVO extends SuperVO {

	private String url;
	private String corp;
	private String corp_id;
	private String ccounty;
	private String pk_taxreport;
	private String sbcode;
	private String userid;
	private String logindate;
	private String ssoserver ="n";
	//cookie
	private String dzfsso = null;
	private String dzfcorp = null;
	private String dzfuid = null;
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getCorp() {
		return corp;
	}

	public void setCorp(String corp) {
		this.corp = corp;
	}

	public String getCorp_id() {
		return corp_id;
	}

	public void setCorp_id(String corp_id) {
		this.corp_id = corp_id;
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

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getLogindate() {
		return logindate;
	}

	public void setLogindate(String logindate) {
		this.logindate = logindate;
	}

	public String getSsoserver() {
		return ssoserver;
	}

	public void setSsoserver(String ssoserver) {
		this.ssoserver = ssoserver;
	}

	public String getDzfsso() {
		return dzfsso;
	}

	public void setDzfsso(String dzfsso) {
		this.dzfsso = dzfsso;
	}

	public String getDzfcorp() {
		return dzfcorp;
	}

	public void setDzfcorp(String dzfcorp) {
		this.dzfcorp = dzfcorp;
	}

	public String getDzfuid() {
		return dzfuid;
	}

	public void setDzfuid(String dzfuid) {
		this.dzfuid = dzfuid;
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
