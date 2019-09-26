package com.dzf.zxkj.platform.model.zncs;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 分类树请求参数VO
 * @author mfz
 *
 */
public class BillcategoryQueryVO extends SuperVO {

	private String period;//期间
	private Integer billstate;//状态 0未做账 1已做账、2已作废
	@JsonProperty("id")
	private String pk_category;//公司级树主键
	@JsonProperty("code")
	private String categorycode;//公司数分类编码
	private DZFBoolean isBank;//是否按银行账号分组票
	@JsonProperty("pk_parent")
	private String pk_parentcategory;//父主键为了出来银行账号
	private String pk_corp;//公司主键
	
	private String billtype;//增值税，银行票据，其他票据
	private String invoicetype;//单据类型
	private String billtitle;//票据名称
	private String bntotaltax;// 价税合计开始
	private String entotaltax;// 价税合计结束
	private String truthindent;//真伪
	
	private String remark;//摘要
	
	private String vpurchname;//付款方名称  
	
	private String vsalename;//销售方名称 收款方
	
	private String bdate;//开票开始日期
	
	private String edate;//开票结束日期
	
	private String oldperiod;//老期间
	
	private String pk_bankcode;//点中银行票下的4级及以后有用，记录点的是哪个银行账号下的分类
	
	public String getPk_bankcode() {
		return pk_bankcode;
	}
	public String getInvoicetype() {
		return invoicetype;
	}

	public void setInvoicetype(String invoicetype) {
		this.invoicetype = invoicetype;
	}

	public String getBilltitle() {
		return billtitle;
	}

	public void setBilltitle(String billtitle) {
		this.billtitle = billtitle;
	}

	public String getBntotaltax() {
		return bntotaltax;
	}
	public void setBntotaltax(String bntotaltax) {
		this.bntotaltax = bntotaltax;
	}
	public String getEntotaltax() {
		return entotaltax;
	}
	public void setEntotaltax(String entotaltax) {
		this.entotaltax = entotaltax;
	}
	public String getTruthindent() {
		return truthindent;
	}

	public void setTruthindent(String truthindent) {
		this.truthindent = truthindent;
	}

	public void setPk_bankcode(String pk_bankcode) {
		this.pk_bankcode = pk_bankcode;
	}
	public String getOldperiod() {
		return oldperiod;
	}

	public void setOldperiod(String oldperiod) {
		this.oldperiod = oldperiod;
	}

	public String getBilltype() {
		return billtype;
	}

	public void setBilltype(String billtype) {
		this.billtype = billtype;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getVpurchname() {
		return vpurchname;
	}

	public void setVpurchname(String vpurchname) {
		this.vpurchname = vpurchname;
	}

	public String getVsalename() {
		return vsalename;
	}

	public void setVsalename(String vsalename) {
		this.vsalename = vsalename;
	}

	public String getBdate() {
		return bdate;
	}

	public void setBdate(String bdate) {
		this.bdate = bdate;
	}

	public String getEdate() {
		return edate;
	}

	public void setEdate(String edate) {
		this.edate = edate;
	}

	public String getPk_parentcategory() {
		return pk_parentcategory;
	}

	public void setPk_parentcategory(String pk_parentcategory) {
		this.pk_parentcategory = pk_parentcategory;
	}

	public DZFBoolean getIsBank() {
		return isBank;
	}

	public void setIsBank(DZFBoolean isBank) {
		this.isBank = isBank;
	}

	public String getCategorycode() {
		return categorycode;
	}

	public void setCategorycode(String categorycode) {
		this.categorycode = categorycode;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public Integer getBillstate() {
		return billstate;
	}

	public void setBillstate(Integer billstate) {
		this.billstate = billstate;
	}

	public String getPk_category() {
		return pk_category;
	}

	public void setPk_category(String pk_category) {
		this.pk_category = pk_category;
	}

	@Override
	public String getPKFieldName() {
		return null;
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return null;
	}

}
