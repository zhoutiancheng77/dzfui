package com.dzf.zxkj.platform.model.zncs;

import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.model.SuperVO;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 智能财税
 * 自定义凭证模板主表
 * @author mfz
 *
 */
@SuppressWarnings({ "serial", "rawtypes" })
public class VouchertempletHVO extends SuperVO {
	
	@JsonProperty("id")
	private String pk_vouchertemplet_h;//主键
	@JsonProperty("corpid")
	private String pk_corp;	
	
	@JsonProperty("baseid")
	private String pk_basecategory;//基础票据类别主键
	@JsonProperty("categoryid")
	private String pk_category;//公司级类别主键
	@JsonProperty("order")
	private Integer orderno;//顺序号
	@JsonProperty("words")
	private String keywords;//关键字名称
	@JsonProperty("name")
	private String templetname;//模板名称
	private Integer dr;
	private DZFDateTime ts;

	public String getPk_basecategory() {
		return pk_basecategory;
	}

	public void setPk_basecategory(String pk_basecategory) {
		this.pk_basecategory = pk_basecategory;
	}

	public String getPk_vouchertemplet_h() {
		return pk_vouchertemplet_h;
	}

	public void setPk_vouchertemplet_h(String pk_vouchertemplet_h) {
		this.pk_vouchertemplet_h = pk_vouchertemplet_h;
	}

	public String getPk_category() {
		return pk_category;
	}

	public void setPk_category(String pk_category) {
		this.pk_category = pk_category;
	}

	public Integer getOrderno() {
		return orderno;
	}

	public void setOrderno(Integer orderno) {
		this.orderno = orderno;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public String getTempletname() {
		return templetname;
	}

	public void setTempletname(String templetname) {
		this.templetname = templetname;
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
		return "pk_vouchertemplet_h";
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return "ynt_vouchertemplet_h";
	}
	
	
}
