package com.dzf.zxkj.platform.model.zcgl;

import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CorpAssetCategoryVO extends SuperVO {

	public static final String TABLE_NAME = "ynt_category_corp";
	public static final String PK_FIELD = "pk_assetcategory_corp";

	private String pk_assetcategory_corp;

	private DZFDateTime ts;
	@JsonProperty("name")
	private String catename;
	@JsonProperty("memo")
	private String memo;
	private Integer dr;
	@JsonProperty("code")
	private String catecode;
	@JsonProperty("id")
	private String pk_assetcategory;
	@JsonProperty("corp")
	private String pk_corp;

	@JsonProperty("ulimit")
	private Integer uselimit;// 预计使用年限
	@JsonProperty("zjfs")
	private Integer zjtype;// 折旧方式
	@JsonProperty("czl")
	private DZFDouble salvageratio;// 残值率

	public String getPk_assetcategory_corp() {
		return pk_assetcategory_corp;
	}

	public void setPk_assetcategory_corp(String pk_assetcategory_corp) {
		this.pk_assetcategory_corp = pk_assetcategory_corp;
	}

	public DZFDateTime getTs() {
		return ts;
	}

	public void setTs(DZFDateTime ts) {
		this.ts = ts;
	}

	public String getCatename() {
		return catename;
	}

	public void setCatename(String catename) {
		this.catename = catename;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public Integer getDr() {
		return dr;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
	}

	public String getCatecode() {
		return catecode;
	}

	public void setCatecode(String catecode) {
		this.catecode = catecode;
	}

	public String getPk_assetcategory() {
		return pk_assetcategory;
	}

	public void setPk_assetcategory(String pk_assetcategory) {
		this.pk_assetcategory = pk_assetcategory;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public Integer getUselimit() {
		return uselimit;
	}

	public void setUselimit(Integer uselimit) {
		this.uselimit = uselimit;
	}

	public Integer getZjtype() {
		return zjtype;
	}

	public void setZjtype(Integer zjtype) {
		this.zjtype = zjtype;
	}

	public DZFDouble getSalvageratio() {
		return salvageratio;
	}

	public void setSalvageratio(DZFDouble salvageratio) {
		this.salvageratio = salvageratio;
	}

	@Override
	public String getPKFieldName() {
		return PK_FIELD;
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return TABLE_NAME;
	}

}
