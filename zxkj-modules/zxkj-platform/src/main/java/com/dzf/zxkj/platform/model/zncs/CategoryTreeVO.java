package com.dzf.zxkj.platform.model.zncs;

import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings({ "rawtypes", "serial" })
public class CategoryTreeVO extends SuperVO {

	@JsonProperty("id")
	private String pk_category;
	@JsonProperty("baseid")
	private String pk_basecategory;
	@JsonProperty("pk_parent")
	private String pk_parentcategory;
	@JsonProperty("text")
	private String categoryname;
	private DZFBoolean isleaf;
	
	public DZFBoolean getIsleaf() {
		return isleaf;
	}
	public void setIsleaf(DZFBoolean isleaf) {
		this.isleaf = isleaf;
	}
	public String getPk_basecategory() {
		return pk_basecategory;
	}
	public void setPk_basecategory(String pk_basecategory) {
		this.pk_basecategory = pk_basecategory;
	}
	public String getCategoryname() {
		return categoryname;
	}
	public void setCategoryname(String categoryname) {
		this.categoryname = categoryname;
	}
	
	public String getPk_category() {
		return pk_category;
	}
	public void setPk_category(String pk_category) {
		this.pk_category = pk_category;
	}
	public String getPk_parentcategory() {
		return pk_parentcategory;
	}
	public void setPk_parentcategory(String pk_parentcategory) {
		this.pk_parentcategory = pk_parentcategory;
	}
	@Override
	public String getPKFieldName() {
		return "pk_category";
	}
	@Override
	public String getParentPKFieldName() {
		return "pk_parentcategory";
	}
	@Override
	public String getTableName() {
		return "ynt_billcategory";
	}
}
