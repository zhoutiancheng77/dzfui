package com.dzf.zxkj.platform.model.zncs;


import com.dzf.zxkj.common.model.SuperVO;

/**
 * 票据VO
 * @author mfz
 *
 */
public class CheckOcrInvoiceVO extends SuperVO {
	private String pk_invoice;//票据主键
	private String id;
	private String webid;//图片id
	private String errordesc;//问题描述
	private String ocraddress;//图片路径
	private String categoryname;//分类名称
	private String billtitle;//票据名称




	public String getId() { return id; }

	public void setId(String id) { this.id = id; }

	public String getBilltitle() {
		return billtitle;
	}

	public void setBilltitle(String billtitle) {
		this.billtitle = billtitle;
	}

	public String getPk_invoice() {
		return pk_invoice;
	}

	public void setPk_invoice(String pk_invoice) {
		this.pk_invoice = pk_invoice;
	}

	public String getWebid() {
		return webid;
	}

	public void setWebid(String webid) {
		this.webid = webid;
	}

	public String getErrordesc() {
		return errordesc;
	}

	public void setErrordesc(String errordesc) {
		this.errordesc = errordesc;
	}

	public String getOcraddress() {
		return ocraddress;
	}

	public void setOcraddress(String ocraddress) {
		this.ocraddress = ocraddress;
	}

	public String getCategoryname() {
		return categoryname;
	}

	public void setCategoryname(String categoryname) {
		this.categoryname = categoryname;
	}

	@Override
	public String getPKFieldName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getParentPKFieldName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
