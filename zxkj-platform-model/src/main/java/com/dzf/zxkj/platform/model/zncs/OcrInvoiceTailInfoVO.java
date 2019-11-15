package com.dzf.zxkj.platform.model.zncs;


import com.dzf.zxkj.common.model.SuperVO;

public class OcrInvoiceTailInfoVO extends SuperVO {

	private String bspmc;//名称
	private String invspec;//规格
	private String measurename;//型号
	private String fphm;
	private String pk_invoice;
	private String pk_invoice_detail;
	private String categorycode;
	private String pk_billcategory;
	private String unit;
	
	//private DZFBoolean iszd;
	
	
//	public DZFBoolean getIszd() {
//		return iszd;
//	}
//	public void setIszd(DZFBoolean iszd) {
//		this.iszd = iszd;
//	}
	
	public String getCategorycode() {
		return categorycode;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public String getPk_billcategory() {
		return pk_billcategory;
	}
	public void setPk_billcategory(String pk_billcategory) {
		this.pk_billcategory = pk_billcategory;
	}
	public void setCategorycode(String categorycode) {
		this.categorycode = categorycode;
	}
	public String getBspmc() {
		return bspmc;
	}
	public String getPk_invoice() {
		return pk_invoice;
	}
	public void setPk_invoice(String pk_invoice) {
		this.pk_invoice = pk_invoice;
	}
	public String getPk_invoice_detail() {
		return pk_invoice_detail;
	}
	public void setPk_invoice_detail(String pk_invoice_detail) {
		this.pk_invoice_detail = pk_invoice_detail;
	}
	public void setBspmc(String bspmc) {
		this.bspmc = bspmc;
	}
	public String getInvspec() {
		return invspec;
	}
	public void setInvspec(String invspec) {
		this.invspec = invspec;
	}
	public String getMeasurename() {
		return measurename;
	}
	public void setMeasurename(String measurename) {
		this.measurename = measurename;
	}
	public String getFphm() {
		return fphm;
	}
	public void setFphm(String fphm) {
		this.fphm = fphm;
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
