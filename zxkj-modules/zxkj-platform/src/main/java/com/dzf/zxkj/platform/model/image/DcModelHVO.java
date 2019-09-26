package com.dzf.zxkj.platform.model.image;

import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DcModelHVO extends SuperVO<DcModelBVO> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String pk_model_h;//主键
	private String pk_corp;//公司主键
	private String accountschemaname;//科目方案名称
	@JsonProperty("hykmid")
	private String pk_trade_accountschema;//行业科目方案
	private String vspstylecode;//发票code
	private String vspstylename;//发票name
	//企业性质
	private String chargedeptname;//公司性质 [小规模纳税人，一般纳税人]
	private String szstylecode;//结算方式编码
	private String szstylename;//结算方式名称
	
	@JsonProperty("busitempcode")
	private String busitypetempcode;//业务类型名称
	@JsonProperty("busitempname")
	private String busitypetempname;//业务类型名称
	
	private String vdef1;
	private String vdef2;
	private String vdef3;
	private DZFDouble ndef1;
	private DZFDouble ndef2;
	private DZFDouble ndef3;
	private String vnote;//备注
	private Integer dr;
	private DZFDateTime ts;
	//拼音简写
	private String shortpinyin;
	//是否常用
	private DZFBoolean isdefault;
	
	private String pk_trade;//行业主键
	
	private String tradename;//行业名称 
	
	private String keywords;//关键字
	
	public String getBusitypetempname() {
		return busitypetempname;
	}
	public void setBusitypetempname(String busitypetempname) {
		this.busitypetempname = busitypetempname;
	}
	public String getPk_trade_accountschema() {
		return pk_trade_accountschema;
	}
	public void setPk_trade_accountschema(String pk_trade_accountschema) {
		this.pk_trade_accountschema = pk_trade_accountschema;
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
	public String getChargedeptname() {
		return chargedeptname;
	}
	public void setChargedeptname(String chargedeptname) {
		this.chargedeptname = chargedeptname;
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
	public String getBusitypetempcode() {
		return busitypetempcode;
	}
	public void setBusitypetempcode(String busitypetempcode) {
		this.busitypetempcode = busitypetempcode;
	}
	public Integer getDr() {
		return dr;
	}
	public void setDr(Integer dr) {
		this.dr = dr;
	}
	public DZFDateTime getTs() {
		return ts;
	}
	public void setTs(DZFDateTime ts) {
		this.ts = ts;
	}
	public String getPk_model_h() {
		return pk_model_h;
	}
	public void setPk_model_h(String pk_model_h) {
		this.pk_model_h = pk_model_h;
	}
	public String getVnote() {
		return vnote;
	}
	public void setVnote(String vnote) {
		this.vnote = vnote;
	}
	public String getAccountschemaname() {
		return accountschemaname;
	}
	public void setAccountschemaname(String accountschemaname) {
		this.accountschemaname = accountschemaname;
	}
	
	public String getVdef1() {
		return vdef1;
	}
	public void setVdef1(String vdef1) {
		this.vdef1 = vdef1;
	}
	public String getVdef2() {
		return vdef2;
	}
	public void setVdef2(String vdef2) {
		this.vdef2 = vdef2;
	}
	public String getVdef3() {
		return vdef3;
	}
	public void setVdef3(String vdef3) {
		this.vdef3 = vdef3;
	}
	public DZFDouble getNdef1() {
		return ndef1;
	}
	public void setNdef1(DZFDouble ndef1) {
		this.ndef1 = ndef1;
	}
	public DZFDouble getNdef2() {
		return ndef2;
	}
	public void setNdef2(DZFDouble ndef2) {
		this.ndef2 = ndef2;
	}
	public DZFDouble getNdef3() {
		return ndef3;
	}
	public void setNdef3(DZFDouble ndef3) {
		this.ndef3 = ndef3;
	}
	public String getPk_corp() {
		return pk_corp;
	}
	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}
	public String getShortpinyin() {
		return shortpinyin;
	}
	public void setShortpinyin(String shortpinyin) {
		this.shortpinyin = shortpinyin;
	}
	public DZFBoolean getIsdefault() {
		return isdefault;
	}
	public void setIsdefault(DZFBoolean isdefault) {
		this.isdefault = isdefault;
	}
	public String getPk_trade() {
		return pk_trade;
	}
	public void setPk_trade(String pk_trade) {
		this.pk_trade = pk_trade;
	}
	public String getTradename() {
		return tradename;
	}
	public void setTradename(String tradename) {
		this.tradename = tradename;
	}
	public String getKeywords() {
		return keywords;
	}
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}
	@Override
	public String getParentPKFieldName() {
		return null;
	}
	@Override
	public String getPKFieldName() {
		return "pk_model_h";
	}
	@Override
	public String getTableName() {
		return "ynt_dcmodel_h";
	}
}
