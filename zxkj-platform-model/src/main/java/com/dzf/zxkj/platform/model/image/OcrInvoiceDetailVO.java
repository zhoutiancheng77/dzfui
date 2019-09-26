package com.dzf.zxkj.platform.model.image;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;

public class OcrInvoiceDetailVO extends SuperVO {

	/**
	 * 接口发票信息体
	 */
	private static final long serialVersionUID = 1891318496451771598L;

	private String invname;// 货物或应税劳务名称
	private String invtype;// 规格型号
	private String itemunit;// 单位
	private String itemamount;// 数量
	private String itemprice;// 单价
	private String itemmny;// 金额
	private String itemtaxrate;// 税率
	private String itemtaxmny;// 税额
	private DZFDateTime ts;// 时间戳
	private String ocr_id;// ocr信息id
	private String pk_invoice_detail;// 主键
	private String pk_invoice;// 发票主键
	@JsonProperty("corpId")
	private String pk_corp;// 会计公司
	private Integer dr;// 删除标志
	private String txrqq;// 通行日期起
	private String txrqz;// 通行日期止
	
	private Integer rowno;  //行号
	private String pk_category_keyword;//票据类别分类规则表主键
	private String pk_billcategory;//票据类别主键
	private String categorycode;
	private String categoryname;
	private String pk_inventory;//存货辅助主键  不存库

	public String getCategorycode() {
		return categorycode;
	}

	public void setCategorycode(String categorycode) {
		this.categorycode = categorycode;
	}

	public String getCategoryname() {
		return categoryname;
	}

	public void setCategoryname(String categoryname) {
		this.categoryname = categoryname;
	}

	public Integer getRowno() {
		return rowno;
	}

	public void setRowno(Integer rowno) {
		this.rowno = rowno;
	}

	public String getPk_category_keyword() {
		return pk_category_keyword;
	}

	public void setPk_category_keyword(String pk_category_keyword) {
		this.pk_category_keyword = pk_category_keyword;
	}


	public String getPk_billcategory() {
		return pk_billcategory;
	}

	public void setPk_billcategory(String pk_billcategory) {
		this.pk_billcategory = pk_billcategory;
	}

	public String getInvname() {
		return invname == null ?"":invname;
	}

	public void setInvname(String invname) {
		this.invname = invname;
	}

	public String getInvtype() {
		return invtype;
	}

	public void setInvtype(String invtype) {
		this.invtype = invtype;
	}

	public String getItemunit() {
		return itemunit;
	}

	public void setItemunit(String itemunit) {
		this.itemunit = itemunit;
	}

	public String getItemamount() {
		return itemamount;
	}

	public void setItemamount(String itemamount) {
		this.itemamount = itemamount;
	}

	public String getItemprice() {
		return itemprice;
	}

	public void setItemprice(String itemprice) {
		this.itemprice = itemprice;
	}

	public String getItemtaxrate() {
		return itemtaxrate;
	}

	public void setItemtaxrate(String itemtaxrate) {
		this.itemtaxrate = itemtaxrate;
	}

	public String getItemmny() {
		return itemmny;
	}

	public void setItemmny(String itemmny) {
		this.itemmny = itemmny;
	}

	public String getItemtaxmny() {
		return itemtaxmny;
	}

	public void setItemtaxmny(String itemtaxmny) {
		this.itemtaxmny = itemtaxmny;
	}

	public DZFDateTime getTs() {
		return ts;
	}

	public void setTs(DZFDateTime ts) {
		this.ts = ts;
	}

	public String getOcr_id() {
		return ocr_id;
	}

	public void setOcr_id(String ocr_id) {
		this.ocr_id = ocr_id;
	}

	public String getPk_invoice_detail() {
		return pk_invoice_detail;
	}

	public void setPk_invoice_detail(String pk_invoice_detail) {
		this.pk_invoice_detail = pk_invoice_detail;
	}

	public String getPk_invoice() {
		return pk_invoice;
	}

	public void setPk_invoice(String pk_invoice) {
		this.pk_invoice = pk_invoice;
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
	
	public String getPk_inventory() {
		return pk_inventory;
	}

	public void setPk_inventory(String pk_inventory) {
		this.pk_inventory = pk_inventory;
	}
	
	public String getTxrqq() {
		return txrqq;
	}

	public String getTxrqz() {
		return txrqz;
	}

	public void setTxrqq(String txrqq) {
		this.txrqq = txrqq;
	}

	public void setTxrqz(String txrqz) {
		this.txrqz = txrqz;
	}

	@Override
	public String getPKFieldName() {
		return "pk_invoice_detail";
	}

	@Override
	public String getParentPKFieldName() {
		return "pk_invoice";
	}

	@Override
	public String getTableName() {
		return "ynt_interface_invoice_detail";
	}

}
