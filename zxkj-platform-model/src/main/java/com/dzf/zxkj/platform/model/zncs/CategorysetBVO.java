package com.dzf.zxkj.platform.model.zncs;


import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.model.SuperVO;
import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings({ "serial", "rawtypes" })
public class CategorysetBVO extends SuperVO {
	
	@JsonProperty("id")
	private String pk_categoryset_fzhs;//主键
	@JsonProperty("pid")
	private String pk_categoryset;//主表主键
	private Integer dr;
	private DZFDateTime ts;
	private String pk_corp;
	@JsonProperty("da")
	private String pk_auacount_h;//辅助核算档案主键
	@JsonProperty("daz")
	private String pk_auacount_b;//辅助核算值主键

	public String getPk_categoryset() {
		return pk_categoryset;
	}

	public void setPk_categoryset(String pk_categoryset) {
		this.pk_categoryset = pk_categoryset;
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

	public String getPk_categoryset_fzhs() {
		return pk_categoryset_fzhs;
	}

	public void setPk_categoryset_fzhs(String pk_categoryset_fzhs) {
		this.pk_categoryset_fzhs = pk_categoryset_fzhs;
	}

	public String getPk_auacount_h() {
		return pk_auacount_h;
	}

	public void setPk_auacount_h(String pk_auacount_h) {
		this.pk_auacount_h = pk_auacount_h;
	}

	public String getPk_auacount_b() {
		return pk_auacount_b;
	}

	public void setPk_auacount_b(String pk_auacount_b) {
		this.pk_auacount_b = pk_auacount_b;
	}

	@Override
	public String getPKFieldName() {
		return "pk_categoryset_fzhs";
	}

	@Override
	public String getParentPKFieldName() {
		return "pk_categoryset";
	}

	@Override
	public String getTableName() {
		return "ynt_categoryset_fzhs";
	}
	
	
}
