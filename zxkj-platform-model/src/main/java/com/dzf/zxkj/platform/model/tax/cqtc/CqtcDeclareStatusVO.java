package com.dzf.zxkj.platform.model.tax.cqtc;

import com.dzf.zxkj.common.model.SuperVO;
import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("rawtypes")
public class CqtcDeclareStatusVO extends SuperVO {
	
	private static final long serialVersionUID = -4401221799240291153L;
	
	public String pk_taxkphz;//主键

	public String openId;//
	
	public String pk_corp;
	
	@JsonProperty("vscode")
	public String vsoccrecode;//纳税人识别号
	
	@JsonProperty("period")
	public String yearmonth;//申报期间
	
	public String sb_zlbh;//申报种类编号
	
	public Integer sbzt_dm;//申报状态代码
	
	//时间起	
	private String periodfrom;
	//时间止	
	private String periodto;
	
	public String getPeriodfrom() {
		return periodfrom;
	}

	public void setPeriodfrom(String periodfrom) {
		this.periodfrom = periodfrom;
	}

	public String getPeriodto() {
		return periodto;
	}

	public void setPeriodto(String periodto) {
		this.periodto = periodto;
	}

	public String getSb_zlbh() {
		return sb_zlbh;
	}

	public Integer getSbzt_dm() {
		return sbzt_dm;
	}

	public void setSbzt_dm(Integer sbzt_dm) {
		this.sbzt_dm = sbzt_dm;
	}

	public void setSb_zlbh(String sb_zlbh) {
		this.sb_zlbh = sb_zlbh;
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

	public String getYearmonth() {
		return yearmonth;
	}

	public void setYearmonth(String yearmonth) {
		this.yearmonth = yearmonth;
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
