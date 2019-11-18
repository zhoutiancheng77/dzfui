package com.dzf.zxkj.platform.model.zncs;

import com.dzf.zxkj.common.model.SuperVO;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 票据VO
 * @author mfz
 *
 */
public class InvoiceCategoryVO extends SuperVO {
	private String pk_invoice;//票据主键
	private String pk_billcategory;//公司级分类主键
	private String categoryname;//公司级分类名称
	private String categorycode;//公司级分类编码
	private String billtitle;//票据显示在页面上的名字
	private String ntotaltax;//价税合计
	private String nmny;//金额合计
	private String ntaxnmny;//税额合计
	private String taxrate;//税额
	private String errordesc;//错误描述
	private String errordesc2;//错误描述2
	private Integer rowcount;//摘要行数
	private String vpurchname;
	private String vpurchtaxno;
	private String vsalename;
	private String vsaletaxno;
	private String webid;
	private String pk_image_library;
	private String dinvoicedate;//开票日期
	private String istate;
    private String truthindent;
    
    
	
	public String getTruthindent() {
		return truthindent;
	}

	public void setTruthindent(String truthindent) {
		this.truthindent = truthindent;
	}

	public String getIstate() {
		return istate;
	}

	public void setIstate(String istate) {
		this.istate = istate;
	}

	public String getDinvoicedate() {
		return dinvoicedate;
	}

	public void setDinvoicedate(String dinvoicedate) {
		this.dinvoicedate = dinvoicedate;
	}

	public String getPk_image_library() {
		return pk_image_library;
	}

	public void setPk_image_library(String pk_image_library) {
		this.pk_image_library = pk_image_library;
	}

	public String getWebid() {
		return webid;
	}

	public void setWebid(String webid) {
		this.webid = webid;
	}

	public String getVpurchname() {
		return vpurchname;
	}

	public void setVpurchname(String vpurchname) {
		this.vpurchname = vpurchname;
	}

	public String getVpurchtaxno() {
		return vpurchtaxno;
	}

	public void setVpurchtaxno(String vpurchtaxno) {
		this.vpurchtaxno = vpurchtaxno;
	}

	public String getVsalename() {
		return vsalename;
	}

	public void setVsalename(String vsalename) {
		this.vsalename = vsalename;
	}

	public String getVsaletaxno() {
		return vsaletaxno;
	}

	public void setVsaletaxno(String vsaletaxno) {
		this.vsaletaxno = vsaletaxno;
	}

	@JsonProperty("groupid")
	private String pk_image_group;//图片组ID
	
	public Integer getRowcount() {
		return rowcount;
	}

	public void setRowcount(Integer rowcount) {
		this.rowcount = rowcount;
	}

	public String getPk_image_group() {
		return pk_image_group;
	}

	public void setPk_image_group(String pk_image_group) {
		this.pk_image_group = pk_image_group;
	}

	public String getErrordesc2() {
		return errordesc2;
	}

	public void setErrordesc2(String errordesc2) {
		this.errordesc2 = errordesc2;
	}

	public String getErrordesc() {
		return errordesc;
	}

	public void setErrordesc(String errordesc) {
		this.errordesc = errordesc;
	}

	public String getNtotaltax() {
		return ntotaltax;
	}

	public void setNtotaltax(String ntotaltax) {
		this.ntotaltax = ntotaltax;
	}

	public String getNmny() {
		return nmny;
	}

	public void setNmny(String nmny) {
		this.nmny = nmny;
	}

	public String getNtaxnmny() {
		return ntaxnmny;
	}

	public void setNtaxnmny(String ntaxnmny) {
		this.ntaxnmny = ntaxnmny;
	}

	public String getTaxrate() {
		return taxrate;
	}

	public void setTaxrate(String taxrate) {
		this.taxrate = taxrate;
	}

	public String getPk_invoice() {
		return pk_invoice;
	}

	public void setPk_invoice(String pk_invoice) {
		this.pk_invoice = pk_invoice;
	}

	public String getPk_billcategory() {
		return pk_billcategory;
	}

	public void setPk_billcategory(String pk_billcategory) {
		this.pk_billcategory = pk_billcategory;
	}

	public String getCategoryname() {
		return categoryname;
	}

	public void setCategoryname(String categoryname) {
		this.categoryname = categoryname;
	}

	public String getCategorycode() {
		return categorycode;
	}

	public void setCategorycode(String categorycode) {
		this.categorycode = categorycode;
	}

	public String getBilltitle() {
		return billtitle;
	}

	public void setBilltitle(String billtitle) {
		this.billtitle = billtitle;
	}

	@Override
	public String getPKFieldName() {
		return "pk_invoice";
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return "ynt_interface_invoice";
	}

}
