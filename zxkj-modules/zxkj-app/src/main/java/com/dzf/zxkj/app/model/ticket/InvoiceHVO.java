package com.dzf.zxkj.app.model.ticket;


import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;

/**
 * 百旺发票信息
 * @author zhangj
 *
 */
public class InvoiceHVO extends SuperVO {
	public static final String TABLE_NAME = "ynt_bw_invoice_h";

	public static final String PK_FIELD = "pk_bw_invoice_h";

	private String pk_bw_invoice_h;//主表主键
	private String pk_corp ;//公司
	
	private String pk_image_group;//图片组PK
	private DZFBoolean ishasimg;//是否生成图片
	private DZFBoolean istogl;//是否生成凭证
	private String pdfpath;//pdf路径信息
	private String imagepth;//图片路径信息
	
	//基本信息
	private String invoicetype;// 开票类型",
	private String redinvoicecode;// 原发票代码",
	private String redinvoicenumber;// 原发票号码",
	private String invoicenumber;// 发票号码",
	private String invoicecode;// 发票代码",
	private String machinenumber;// 机器编码",
	private String checkcode;// 校验码",
	private String taxcode;// 密码区",
	private String invoicedate;// 开票日期",
	private String issuername;// 开票人",
	private String payeename;// 收款人",
	private String auditorname;// 复核人",
	private String invoicetypecode;// 发票类型代码",
	private String remark;// 备注",
	private String downloadlink;// 版式文件下载地址"
	private String qrcode;//二维码

	//购买方信息
	private String purchaserunitname;// 购买方名称",
	private String purchaserunittaxid;// 购买方税号",
	private String purchaserunitaddress;// 购买方地址",
	private String purchaserunitphone;// 购买方电话",
	private String purchaserunitbankname;// 购买方开户行名称",
	private String purchaserunitbankaccount;// 购买方开户行账号"

	//销售方信息
	private String salesunitname;// 销方单位名称",
	private String salesunittaxid;// 销方单位税号",
	private String salesunitaddress;// 销方单位地址",
	private String salesunitphone;// 销方单位电话",
	private String salesunitbankname;// 销方单位开户行名称",
	private String salesunitbankacount;// 销方单位开户行账户"

	//金额信息
	private DZFDouble totalamount;// 合计金额",
	private DZFDouble totaltaxamount;// 合计税额",
	private DZFDouble totalpricetax;// 价税合计",
	private String totalpricetaxinwords;//汉字
	
	private DZFDateTime ts;
	private Integer dr;
	
	public String getPdfpath() {
		return pdfpath;
	}

	public void setPdfpath(String pdfpath) {
		this.pdfpath = pdfpath;
	}

	public String getImagepth() {
		return imagepth;
	}

	public void setImagepth(String imagepth) {
		this.imagepth = imagepth;
	}

	public String getQrcode() {
		return qrcode;
	}

	public void setQrcode(String qrcode) {
		this.qrcode = qrcode;
	}

	public String getTotalpricetaxinwords() {
		return totalpricetaxinwords;
	}

	public void setTotalpricetaxinwords(String totalpricetaxinwords) {
		this.totalpricetaxinwords = totalpricetaxinwords;
	}

	public String getPk_image_group() {
		return pk_image_group;
	}

	public void setPk_image_group(String pk_image_group) {
		this.pk_image_group = pk_image_group;
	}

	public DZFBoolean getIshasimg() {
		return ishasimg;
	}

	public void setIshasimg(DZFBoolean ishasimg) {
		this.ishasimg = ishasimg;
	}

	public DZFBoolean getIstogl() {
		return istogl;
	}

	public void setIstogl(DZFBoolean istogl) {
		this.istogl = istogl;
	}

	public String getPk_bw_invoice_h() {
		return pk_bw_invoice_h;
	}

	public void setPk_bw_invoice_h(String pk_bw_invoice_h) {
		this.pk_bw_invoice_h = pk_bw_invoice_h;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public DZFDateTime getTs() {
		return ts;
	}

	public void setTs(DZFDateTime ts) {
		this.ts = ts;
	}

	public Integer getDr() {
		return dr;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
	}

	public String getInvoicetype() {
		return invoicetype;
	}

	public void setInvoicetype(String invoicetype) {
		this.invoicetype = invoicetype;
	}

	public String getRedinvoicecode() {
		return redinvoicecode;
	}

	public void setRedinvoicecode(String redinvoicecode) {
		this.redinvoicecode = redinvoicecode;
	}

	public String getRedinvoicenumber() {
		return redinvoicenumber;
	}

	public void setRedinvoicenumber(String redinvoicenumber) {
		this.redinvoicenumber = redinvoicenumber;
	}

	public String getInvoicenumber() {
		return invoicenumber;
	}

	public void setInvoicenumber(String invoicenumber) {
		this.invoicenumber = invoicenumber;
	}

	public String getInvoicecode() {
		return invoicecode;
	}

	public void setInvoicecode(String invoicecode) {
		this.invoicecode = invoicecode;
	}

	public String getMachinenumber() {
		return machinenumber;
	}

	public void setMachinenumber(String machinenumber) {
		this.machinenumber = machinenumber;
	}

	public String getCheckcode() {
		return checkcode;
	}

	public void setCheckcode(String checkcode) {
		this.checkcode = checkcode;
	}

	public String getTaxcode() {
		return taxcode;
	}

	public void setTaxcode(String taxcode) {
		this.taxcode = taxcode;
	}

	public String getInvoicedate() {
		return invoicedate;
	}

	public void setInvoicedate(String invoicedate) {
		this.invoicedate = invoicedate;
	}

	public String getIssuername() {
		return issuername;
	}

	public void setIssuername(String issuername) {
		this.issuername = issuername;
	}

	public String getPayeename() {
		return payeename;
	}

	public void setPayeename(String payeename) {
		this.payeename = payeename;
	}

	public String getAuditorname() {
		return auditorname;
	}

	public void setAuditorname(String auditorname) {
		this.auditorname = auditorname;
	}

	public String getInvoicetypecode() {
		return invoicetypecode;
	}

	public void setInvoicetypecode(String invoicetypecode) {
		this.invoicetypecode = invoicetypecode;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getDownloadlink() {
		return downloadlink;
	}

	public void setDownloadlink(String downloadlink) {
		this.downloadlink = downloadlink;
	}

	public String getPurchaserunitname() {
		return purchaserunitname;
	}

	public void setPurchaserunitname(String purchaserunitname) {
		this.purchaserunitname = purchaserunitname;
	}

	public String getPurchaserunittaxid() {
		return purchaserunittaxid;
	}

	public void setPurchaserunittaxid(String purchaserunittaxid) {
		this.purchaserunittaxid = purchaserunittaxid;
	}

	public String getPurchaserunitaddress() {
		return purchaserunitaddress;
	}

	public void setPurchaserunitaddress(String purchaserunitaddress) {
		this.purchaserunitaddress = purchaserunitaddress;
	}

	public String getPurchaserunitphone() {
		return purchaserunitphone;
	}

	public void setPurchaserunitphone(String purchaserunitphone) {
		this.purchaserunitphone = purchaserunitphone;
	}

	public String getPurchaserunitbankname() {
		return purchaserunitbankname;
	}

	public void setPurchaserunitbankname(String purchaserunitbankname) {
		this.purchaserunitbankname = purchaserunitbankname;
	}

	public String getPurchaserunitbankaccount() {
		return purchaserunitbankaccount;
	}

	public void setPurchaserunitbankaccount(String purchaserunitbankaccount) {
		this.purchaserunitbankaccount = purchaserunitbankaccount;
	}

	public String getSalesunitname() {
		return salesunitname;
	}

	public void setSalesunitname(String salesunitname) {
		this.salesunitname = salesunitname;
	}

	public String getSalesunittaxid() {
		return salesunittaxid;
	}

	public void setSalesunittaxid(String salesunittaxid) {
		this.salesunittaxid = salesunittaxid;
	}

	public String getSalesunitaddress() {
		return salesunitaddress;
	}

	public void setSalesunitaddress(String salesunitaddress) {
		this.salesunitaddress = salesunitaddress;
	}

	public String getSalesunitphone() {
		return salesunitphone;
	}

	public void setSalesunitphone(String salesunitphone) {
		this.salesunitphone = salesunitphone;
	}

	public String getSalesunitbankname() {
		return salesunitbankname;
	}

	public void setSalesunitbankname(String salesunitbankname) {
		this.salesunitbankname = salesunitbankname;
	}

	public String getSalesunitbankacount() {
		return salesunitbankacount;
	}

	public void setSalesunitbankacount(String salesunitbankacount) {
		this.salesunitbankacount = salesunitbankacount;
	}

	public DZFDouble getTotalamount() {
		return totalamount;
	}

	public void setTotalamount(DZFDouble totalamount) {
		this.totalamount = totalamount;
	}

	public DZFDouble getTotaltaxamount() {
		return totaltaxamount;
	}

	public void setTotaltaxamount(DZFDouble totaltaxamount) {
		this.totaltaxamount = totaltaxamount;
	}

	public DZFDouble getTotalpricetax() {
		return totalpricetax;
	}

	public void setTotalpricetax(DZFDouble totalpricetax) {
		this.totalpricetax = totalpricetax;
	}

	@Override
	public String getPKFieldName() {
		return PK_FIELD;
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return TABLE_NAME;
	}

}
