package com.dzf.zxkj.base.query;


import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.model.SuperVO;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 往来账龄查询条件
 * 
 * @author liubj
 *
 */
public class AgeReportQueryVO extends SuperVO {

	@JsonProperty("corp")
	private String pk_corp;// 公司
	// 截止日期
	@JsonProperty("enddate")
	private DZFDate end_date;
	// 辅助核算类别
	private Integer fzlb;
	private String auaccount_type;
	// 辅助核算明细
	@JsonProperty("fzxm")
	private String auaccount_detail;
	// 账龄类型
	@JsonProperty("zllx")
	private Integer age_type;
	// 科目编码
	@JsonProperty("kmbm")
	private String account_code;
	private boolean fzhs;
	private boolean parent;
	
	private String pk_age;

	//账期单位天数
	@JsonProperty("unit")
	private Integer age_unit;
	
	// 建账日期
	private DZFDate jz_date;
	
	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public DZFDate getEnd_date() {
		return end_date;
	}

	public void setEnd_date(DZFDate end_date) {
		this.end_date = end_date;
	}

	public Integer getFzlb() {
		return fzlb;
	}

	public void setFzlb(Integer fzlb) {
		this.fzlb = fzlb;
	}

	public String getAuaccount_type() {
		return auaccount_type;
	}

	public void setAuaccount_type(String auaccount_type) {
		this.auaccount_type = auaccount_type;
	}

	public String getAuaccount_detail() {
		return auaccount_detail;
	}

	public void setAuaccount_detail(String auaccount_detail) {
		this.auaccount_detail = auaccount_detail;
	}

	public Integer getAge_type() {
		return age_type;
	}

	public void setAge_type(Integer age_type) {
		this.age_type = age_type;
	}

	public String getAccount_code() {
		return account_code;
	}

	public void setAccount_code(String account_code) {
		this.account_code = account_code;
	}

	public DZFDate getJz_date() {
		return jz_date;
	}

	public void setJz_date(DZFDate jz_date) {
		this.jz_date = jz_date;
	}

	public boolean isFzhs() {
		return fzhs;
	}

	public void setFzhs(boolean fzhs) {
		this.fzhs = fzhs;
	}

	public boolean isParent() {
		return parent;
	}

	public void setParent(boolean parent) {
		this.parent = parent;
	}

	public String getPk_age() {
		return pk_age;
	}

	public void setPk_age(String pk_age) {
		this.pk_age = pk_age;
	}

	public Integer getAge_unit() {
		return age_unit;
	}

	public void setAge_unit(Integer age_unit) {
		this.age_unit = age_unit;
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
