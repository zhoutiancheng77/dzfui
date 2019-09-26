package com.dzf.zxkj.platform.model.zncs;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 智能财税
 * 关键字入账规则子表1(关键字组合)
 * @author mfz
 *
 */
@SuppressWarnings({ "serial", "rawtypes" })
public class AccsetKeywordBVO1 extends SuperVO {
	
	@JsonProperty("id")
	private String pk_accset_keyword_b1;//主键
	@JsonProperty("pid")
	private String pk_accset_keyword;//主表主键
	@JsonProperty("corpid")
	private String pk_corp;	
	@JsonProperty("words")
	private String pk_keywords;//关键字组主键
	@JsonProperty("wnames")
	private String keywordnames;//关键字名称
	private Integer dr;
	private DZFDateTime ts;
	
	public String getKeywordnames() {
		return keywordnames;
	}

	public void setKeywordnames(String keywordnames) {
		this.keywordnames = keywordnames;
	}

	public String getPk_accset_keyword() {
		return pk_accset_keyword;
	}

	public void setPk_accset_keyword(String pk_accset_keyword) {
		this.pk_accset_keyword = pk_accset_keyword;
	}

	public String getPk_accset_keyword_b1() {
		return pk_accset_keyword_b1;
	}

	public void setPk_accset_keyword_b1(String pk_accset_keyword_b1) {
		this.pk_accset_keyword_b1 = pk_accset_keyword_b1;
	}

	public String getPk_keywords() {
		return pk_keywords;
	}

	public void setPk_keywords(String pk_keywords) {
		this.pk_keywords = pk_keywords;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
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
	public String getPKFieldName() {
		return "pk_accset_keyword_b1";
	}

	@Override
	public String getParentPKFieldName() {
		return "pk_accset_keyword";
	}

	@Override
	public String getTableName() {
		return "ynt_accset_keyword_b1";
	}
	
	
}
