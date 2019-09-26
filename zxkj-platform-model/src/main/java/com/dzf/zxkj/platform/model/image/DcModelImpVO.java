package com.dzf.zxkj.platform.model.image;

import com.dzf.zxkj.common.model.SuperVO;

public class DcModelImpVO extends SuperVO implements Comparable<DcModelImpVO>{
	
	private String busitypetempcode;
	private String busitypetempname;
	private String vspstylecode;
	private String vspstylename;;
	private String szstylecode;
	private String szstylename;
	private String zy;
	private String kmbm;
	private String kmmc;
	private String direction;
	private String vfield;
	private String isdefault;
	private String pk_trade;//行业主键
	private String tradename;//行业名称 
	private String keywords;//关键字
	private String accountschemaname;//科目方案名称
	private String pk_trade_accountschema;//行业科目方案
	private String chargedeptname;//公司性质 [小规模纳税人，一般纳税人]
	private String vnote;//备注
	public String getBusitypetempcode() {
		return busitypetempcode;
	}
	public void setBusitypetempcode(String busitypetempcode) {
		this.busitypetempcode = busitypetempcode;
	}
	public String getBusitypetempname() {
		return busitypetempname;
	}
	public void setBusitypetempname(String busitypetempname) {
		this.busitypetempname = busitypetempname;
	}
	public String getVspstylecode() {
		return vspstylecode;
	}
	public void setVspstylecode(String vspstylecode) {
		this.vspstylecode = vspstylecode;
	}
	public String getVspstylename() {
		return vspstylename;
	}
	public void setVspstylename(String vspstylename) {
		this.vspstylename = vspstylename;
	}
	public String getSzstylecode() {
		return szstylecode;
	}
	public void setSzstylecode(String szstylecode) {
		this.szstylecode = szstylecode;
	}
	public String getSzstylename() {
		return szstylename;
	}
	public void setSzstylename(String szstylename) {
		this.szstylename = szstylename;
	}
	public String getZy() {
		return zy;
	}
	public void setZy(String zy) {
		this.zy = zy;
	}
	public String getKmbm() {
		return kmbm;
	}
	public void setKmbm(String kmbm) {
		this.kmbm = kmbm;
	}
	public String getKmmc() {
		return kmmc;
	}
	public void setKmmc(String kmmc) {
		this.kmmc = kmmc;
	}
	public String getDirection() {
		return direction;
	}
	public void setDirection(String direction) {
		this.direction = direction;
	}
	public String getVfield() {
		return vfield;
	}
	public void setVfield(String vfield) {
		this.vfield = vfield;
	}
	public String getIsdefault() {
		return isdefault;
	}
	public void setIsdefault(String isdefault) {
		this.isdefault = isdefault;
	}
	public String getPk_trade() {
		return pk_trade;
	}
	public void setPk_trade(String pk_trade) {
		this.pk_trade = pk_trade;
	}
	public String getKeywords() {
		return keywords;
	}
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}
	public String getTradename() {
		return tradename;
	}
	public void setTradename(String tradename) {
		this.tradename = tradename;
	}
	public String getAccountschemaname() {
		return accountschemaname;
	}
	public void setAccountschemaname(String accountschemaname) {
		this.accountschemaname = accountschemaname;
	}
	public String getPk_trade_accountschema() {
		return pk_trade_accountschema;
	}
	public void setPk_trade_accountschema(String pk_trade_accountschema) {
		this.pk_trade_accountschema = pk_trade_accountschema;
	}
	public String getChargedeptname() {
		return chargedeptname;
	}
	public void setChargedeptname(String chargedeptname) {
		this.chargedeptname = chargedeptname;
	}
	public String getVnote() {
		return vnote;
	}
	public void setVnote(String vnote) {
		this.vnote = vnote;
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
	@Override
	public int compareTo(DcModelImpVO o) {
		int self = 0;
		if("借方".equals(this.getDirection()) 
				|| "借".equals(this.getDirection())){
			self = 0;
		}else{
			self = 1;
		}
		int other = 0;
		if("借方".equals(o.getDirection()) 
				|| "借".equals(o.getDirection())){
			other = 0;
		}else{
			other = 1;
		}
		//
		return self-other;
	}
}