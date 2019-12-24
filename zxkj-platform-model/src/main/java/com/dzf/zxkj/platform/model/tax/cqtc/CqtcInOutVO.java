package com.dzf.zxkj.platform.model.tax.cqtc;

import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.model.SuperVO;
import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("rawtypes")
public class CqtcInOutVO extends SuperVO {
	
	private static final long serialVersionUID = -4401221799240291153L;
	
	public String pk_taxkphz;//主键

	public String openId;//
	
	public String pk_corp;
	
	@JsonProperty("vscode")
	public String vsoccrecode;//纳税人识别号
	
	@JsonProperty("period")
	public String yearmonth;//申报期间

	public DZFDate periodfrom;//区间开始时间
	
	public DZFDate periodto;//区间结束时间
	
	public String outamount;//销项金额
	
	public String outtax;//销项税额
	
	public String inamount;//进项金额
	
	public String intax;//进项税额
	
	public Integer innum;//进项份数
	
	public Integer dr;
	
	public DZFDateTime ts;
	
	public String getYearmonth() {
		return yearmonth;
	}

	public void setYearmonth(String yearmonth) {
		this.yearmonth = yearmonth;
	}

	public String getPk_taxkphz() {
		return pk_taxkphz;
	}

	public void setPk_taxkphz(String pk_taxkphz) {
		this.pk_taxkphz = pk_taxkphz;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getVsoccrecode() {
		return vsoccrecode;
	}

	public void setVsoccrecode(String vsoccrecode) {
		this.vsoccrecode = vsoccrecode;
	}

	public DZFDate getPeriodfrom() {
		return periodfrom;
	}

	public void setPeriodfrom(DZFDate periodfrom) {
		this.periodfrom = periodfrom;
	}

	public DZFDate getPeriodto() {
		return periodto;
	}

	public void setPeriodto(DZFDate periodto) {
		this.periodto = periodto;
	}

	public String getOutamount() {
		return outamount;
	}

	public void setOutamount(String outamount) {
		this.outamount = outamount;
	}

	public String getOuttax() {
		return outtax;
	}

	public void setOuttax(String outtax) {
		this.outtax = outtax;
	}

	public String getInamount() {
		return inamount;
	}

	public void setInamount(String inamount) {
		this.inamount = inamount;
	}

	public String getIntax() {
		return intax;
	}

	public void setIntax(String intax) {
		this.intax = intax;
	}

	public Integer getInnum() {
		return innum;
	}

	public void setInnum(Integer innum) {
		this.innum = innum;
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

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getPKFieldName() {
		return "pk_taxkphz";
	}

	@Override
	public String getTableName() {
		return " cqtc_taxkphz";
	}

}
