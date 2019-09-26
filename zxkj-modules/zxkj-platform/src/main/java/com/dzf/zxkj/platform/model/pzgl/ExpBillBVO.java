package com.dzf.zxkj.platform.model.pzgl;

import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.fasterxml.jackson.annotation.JsonProperty;

/*
 * 报销单VO
 */
public class ExpBillBVO extends SuperVO {
	private String pk_expbill_b;
	private String pk_expbill_h;
	private String pk_corp;
	//接收的报销单ID
	private String bill_id;
	//接收的发票id
	private String invoice_id;
	//开票公司
	private String invoice_corp;
	//发票分类编码
	private String vtypecode;
	//发票分类
	@JsonProperty("fylb")
	private String vtypename;
	//发票分类id
	private String pk_vtype;
	//开票日期
	@JsonProperty("fprq")
	private DZFDate invoice_date;
	//购买方名称
	private String buyer;
	private String picPath;
	//总金额
	private DZFDouble totalmny;
	//税额
	private DZFDouble taxmny;
	//无税金额
	private DZFDouble extaxmny;
	//备注
	private String memo;
	//创建时间
	private DZFDate createdate;
	private Integer dr;
	
	public String getPk_expbill_b() {
		return pk_expbill_b;
	}
	public void setPk_expbill_b(String pk_expbill_b) {
		this.pk_expbill_b = pk_expbill_b;
	}
	public String getPk_expbill_h() {
		return pk_expbill_h;
	}
	public void setPk_expbill_h(String pk_expbill_h) {
		this.pk_expbill_h = pk_expbill_h;
	}
	public String getPk_corp() {
		return pk_corp;
	}
	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}
	public String getBill_id() {
		return bill_id;
	}
	public void setBill_id(String bill_id) {
		this.bill_id = bill_id;
	}
	public String getInvoice_id() {
		return invoice_id;
	}
	public void setInvoice_id(String invoice_id) {
		this.invoice_id = invoice_id;
	}
	public String getInvoice_corp() {
		return invoice_corp;
	}
	public void setInvoice_corp(String invoice_corp) {
		this.invoice_corp = invoice_corp;
	}
	public String getVtypecode() {
		return vtypecode;
	}
	public void setVtypecode(String vtypecode) {
		this.vtypecode = vtypecode;
	}
	public String getVtypename() {
		return vtypename;
	}
	public void setVtypename(String vtypename) {
		this.vtypename = vtypename;
	}
	public String getPk_vtype() {
		return pk_vtype;
	}
	public void setPk_vtype(String pk_vtype) {
		this.pk_vtype = pk_vtype;
	}
	public DZFDate getInvoice_date() {
		return invoice_date;
	}
	public void setInvoice_date(DZFDate invoice_date) {
		this.invoice_date = invoice_date;
	}
	public String getBuyer() {
		return buyer;
	}
	public void setBuyer(String buyer) {
		this.buyer = buyer;
	}
	public String getPicPath() {
		return picPath;
	}
	public void setPicPath(String picPath) {
		this.picPath = picPath;
	}
	public DZFDouble getTotalmny() {
		return totalmny;
	}
	public void setTotalmny(DZFDouble totalmny) {
		this.totalmny = totalmny;
	}
	public DZFDouble getTaxmny() {
		return taxmny;
	}
	public void setTaxmny(DZFDouble taxmny) {
		this.taxmny = taxmny;
	}
	public DZFDouble getExtaxmny() {
		return extaxmny;
	}
	public void setExtaxmny(DZFDouble extaxmny) {
		this.extaxmny = extaxmny;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	
	public DZFDate getCreatedate() {
		return createdate;
	}
	public void setCreatedate(DZFDate createdate) {
		this.createdate = createdate;
	}
	public Integer getDr() {
		return dr;
	}
	public void setDr(Integer dr) {
		this.dr = dr;
	}
	@Override
	public String getParentPKFieldName() {
		return "pk_expbill_h";
	}
	@Override
	public String getPKFieldName() {
		return "pk_expbill_b";
	}
	@Override
	public String getTableName() {
		return "ynt_expbill_b";
	}
}
