package com.dzf.zxkj.platform.model.zncs;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.platform.model.image.OcrInvoiceVO;

public class BillInfoVO extends SuperVO {
	private OcrInvoiceVO invoicvo ;
	private String imgsourid;
	private String corpId;
	private String imgname;
	private String billid;
	private String istate;
	private String webid;
	private String message;
	
	
	
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getWebid() {
		return webid;
	}

	public void setWebid(String webid) {
		this.webid = webid;
	}

	public String getIstate() {
		return istate;
	}

	public void setIstate(String istate) {
		this.istate = istate;
	}

	public String getBillid() {
		return billid;
	}

	public void setBillid(String billid) {
		this.billid = billid;
	}

	public String getCorpId() {
		return corpId;
	}

	public void setCorpId(String corpId) {
		this.corpId = corpId;
	}

	public OcrInvoiceVO getInvoicvo() {
		return invoicvo;
	}

	public void setInvoicvo(OcrInvoiceVO invoicvo) {
		this.invoicvo = invoicvo;
	}

	public String getImgsourid() {
		return imgsourid;
	}

	public void setImgsourid(String imgsourid) {
		this.imgsourid = imgsourid;
	}

	public String getImgname() {
		return imgname;
	}

	public void setImgname(String imgname) {
		this.imgname = imgname;
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
