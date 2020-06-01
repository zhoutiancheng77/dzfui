package com.dzf.zxkj.app.model.resp.bean;


import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DailyBean extends SuperVO {

	@JsonProperty("date")
	private String prevue;
	private String repcontent;
	@JsonProperty("djsl")
	private Integer billsl;//上传单据数量
	@JsonProperty("pzsl")
	private Integer accountbillsl;//上传凭证数量
	@JsonProperty("xjhz")
	private String hzcashmny;
	@JsonProperty("xjjfje")
	private DZFDouble cashcreditmny;//现金借方金额
	@JsonProperty("xjdfje")
	private DZFDouble cashdebitmny;//现金贷方金额
	@JsonProperty("xjye")
	private DZFDouble cashmny;//现金余额
	@JsonProperty("yhhz")
	private String hzbankmny;
	@JsonProperty("yhjfje")
	private DZFDouble bankcreditmny;//银行借方金额
	@JsonProperty("yhdfje")
	private DZFDouble bankdebitmny;//银行贷方金额
	@JsonProperty("yhye")
	private DZFDouble bankmny;//银行余额
	
	public String getHzcashmny() {
		return hzcashmny;
	}
	public void setHzcashmny(String hzcashmny) {
		this.hzcashmny = hzcashmny;
	}
	public String getHzbankmny() {
		return hzbankmny;
	}
	public void setHzbankmny(String hzbankmny) {
		this.hzbankmny = hzbankmny;
	}
	public DZFDouble getCashmny() {
		return cashmny;
	}
	public void setCashmny(DZFDouble cashmny) {
		this.cashmny = cashmny;
	}
	public DZFDouble getBankmny() {
		return bankmny;
	}
	public void setBankmny(DZFDouble bankmny) {
		this.bankmny = bankmny;
	}
	public Integer getBillsl() {
		return billsl;
	}
	public void setBillsl(Integer billsl) {
		this.billsl = billsl;
	}
	public Integer getAccountbillsl() {
		return accountbillsl;
	}
	public void setAccountbillsl(Integer accountbillsl) {
		this.accountbillsl = accountbillsl;
	}
	public DZFDouble getCashcreditmny() {
		return cashcreditmny;
	}
	public void setCashcreditmny(DZFDouble cashcreditmny) {
		this.cashcreditmny = cashcreditmny;
	}
	public DZFDouble getCashdebitmny() {
		return cashdebitmny;
	}
	public void setCashdebitmny(DZFDouble cashdebitmny) {
		this.cashdebitmny = cashdebitmny;
	}
	public DZFDouble getBankcreditmny() {
		return bankcreditmny;
	}
	public void setBankcreditmny(DZFDouble bankcreditmny) {
		this.bankcreditmny = bankcreditmny;
	}
	public DZFDouble getBankdebitmny() {
		return bankdebitmny;
	}
	public void setBankdebitmny(DZFDouble bankdebitmny) {
		this.bankdebitmny = bankdebitmny;
	}
	public String getPrevue() {
		return prevue;
	}
	public void setPrevue(String prevue) {
		this.prevue = prevue;
	}
	public String getRepcontent() {
		return repcontent;
	}
	public void setRepcontent(String repcontent) {
		this.repcontent = repcontent;
	}
	@Override
	public String getParentPKFieldName() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getPKFieldName() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
