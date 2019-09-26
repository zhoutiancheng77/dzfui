package com.dzf.zxkj.platform.model.tax;

import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDateTime;

/**
 * 税目信息关联信息表
 * @author zpm
 *
 */
public class TaxitemRelationVO extends SuperVO {
	
	private String pk_taxrelation;
	private String corptype;//科目方案
	private String subj_code;
	private String pk_taxitem;
	private String pk_corp;
    public String chargedeptname;// 公司性质
	private DZFDateTime ts;
	private Integer dr;
	//是否凭证显示。
	private DZFBoolean shuimushowpz;//是否显示在凭证上.
	
	public String getPk_taxrelation() {
		return pk_taxrelation;
	}
	public void setPk_taxrelation(String pk_taxrelation) {
		this.pk_taxrelation = pk_taxrelation;
	}
	public String getCorptype() {
		return corptype;
	}
	public void setCorptype(String corptype) {
		this.corptype = corptype;
	}
	public String getSubj_code() {
		return subj_code;
	}
	public void setSubj_code(String subj_code) {
		this.subj_code = subj_code;
	}
	public String getPk_taxitem() {
		return pk_taxitem;
	}
	public void setPk_taxitem(String pk_taxitem) {
		this.pk_taxitem = pk_taxitem;
	}
	public String getPk_corp() {
		return pk_corp;
	}
	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
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
	public String getChargedeptname() {
		return chargedeptname;
	}
	public void setChargedeptname(String chargedeptname) {
		this.chargedeptname = chargedeptname;
	}
	public DZFBoolean getShuimushowpz() {
		return shuimushowpz;
	}

	public void setShuimushowpz(DZFBoolean shuimushowpz) {
		this.shuimushowpz = shuimushowpz;
	}
	@Override
	public String getParentPKFieldName() {
		return null;
	}
	@Override
	public String getPKFieldName() {
		return "pk_taxrelation";
	}
	@Override
	public String getTableName() {
		return "ynt_taxrelation";
	}
}
