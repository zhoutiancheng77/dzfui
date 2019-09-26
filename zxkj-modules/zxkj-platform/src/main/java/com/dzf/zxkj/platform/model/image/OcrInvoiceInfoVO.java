package com.dzf.zxkj.platform.model.image;


import com.dzf.zxkj.base.model.SuperVO;

import java.util.List;

public class OcrInvoiceInfoVO extends SuperVO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String imgname;// 图片名称
	private String webid;// 图片ID
	private String showState;// 图片状态
	private String invoicetype;// 票据类型
	private String keywords;// 关键字
	private String vinvoiceno;// 发票号码
	private String vinvoicecode;// 发票代码
	private String dinvoicedate;// 开票日期
	private String vsalename;// 销售方名称 收款方名称
	private String vsaletaxno;// 销售方纳税号 收款方账号
	private String vpurchname;// 购买方名称 付款方名称
	private String vpurchtaxno;// 购买方纳税号 付款方账号
	private String vmemo;// 备注
	private String nmny;// 金额合计
	private String jym;// 校验码

	private String invname;// 货物名称
	private String invtype;// 规格型号
	private String itemunit;// 单位
	private String itemamount;// 数量
	private String itemprice;// 单价
	private String itemmny;// 金额
	private String itemtaxrate;// 税率
	private String itemtaxmny;// 税额
	private String ntotaltax;// 价税合计 金额

	private String vsaleopenacc;// 银行名称
	private String vpurbankname;// 付款行名称
	private String vsalebankname;// 收款行名称
	private String vsalephoneaddr;// 备注

	private String gcode;// 图片组号
	private String coperatorname;// 上 传 者
	private String doperatedate;// 上传时间
	private List<String[]> colList;
	private List<String> elist;
	
	private String pk_corp;// 会计公司
	private String pk_image_group;// 图片信息组主键
	private String pk_image_library;// 图片信息主键
	private String pk_image_ocrlibrary; // ocr 图片信息主键
	private String pk_invoice; // 识别信息主键

	public String getImgname() {
		return imgname;
	}

	public String getWebid() {
		return webid;
	}

	public String getShowState() {
		return showState;
	}

	public String getInvoicetype() {
		return invoicetype;
	}

	public String getKeywords() {
		return keywords;
	}

	public String getVinvoiceno() {
		return vinvoiceno;
	}

	public String getVinvoicecode() {
		return vinvoicecode;
	}

	public String getDinvoicedate() {
		return dinvoicedate;
	}

	public String getVsalename() {
		return vsalename;
	}

	public String getVsaletaxno() {
		return vsaletaxno;
	}

	public String getVpurchname() {
		return vpurchname;
	}

	public String getVpurchtaxno() {
		return vpurchtaxno;
	}

	public String getVmemo() {
		return vmemo;
	}

	public String getInvname() {
		return invname;
	}

	public String getInvtype() {
		return invtype;
	}

	public String getItemunit() {
		return itemunit;
	}

	public String getItemamount() {
		return itemamount;
	}

	public String getItemprice() {
		return itemprice;
	}

	public String getItemmny() {
		return itemmny;
	}

	public String getItemtaxrate() {
		return itemtaxrate;
	}

	public String getItemtaxmny() {
		return itemtaxmny;
	}

	public String getNtotaltax() {
		return ntotaltax;
	}

	public String getVsaleopenacc() {
		return vsaleopenacc;
	}

	public String getVpurbankname() {
		return vpurbankname;
	}

	public String getVsalebankname() {
		return vsalebankname;
	}

	public String getVsalephoneaddr() {
		return vsalephoneaddr;
	}

	public String getGcode() {
		return gcode;
	}

	public String getCoperatorname() {
		return coperatorname;
	}

	public String getDoperatedate() {
		return doperatedate;
	}

	public void setImgname(String imgname) {
		this.imgname = imgname;
	}

	public void setWebid(String webid) {
		this.webid = webid;
	}

	public void setShowState(String showState) {
		this.showState = showState;
	}

	public void setInvoicetype(String invoicetype) {
		this.invoicetype = invoicetype;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public void setVinvoiceno(String vinvoiceno) {
		this.vinvoiceno = vinvoiceno;
	}

	public void setVinvoicecode(String vinvoicecode) {
		this.vinvoicecode = vinvoicecode;
	}

	public void setDinvoicedate(String dinvoicedate) {
		this.dinvoicedate = dinvoicedate;
	}

	public void setVsalename(String vsalename) {
		this.vsalename = vsalename;
	}

	public void setVsaletaxno(String vsaletaxno) {
		this.vsaletaxno = vsaletaxno;
	}

	public void setVpurchname(String vpurchname) {
		this.vpurchname = vpurchname;
	}

	public void setVpurchtaxno(String vpurchtaxno) {
		this.vpurchtaxno = vpurchtaxno;
	}

	public void setVmemo(String vmemo) {
		this.vmemo = vmemo;
	}

	public void setInvname(String invname) {
		this.invname = invname;
	}

	public void setInvtype(String invtype) {
		this.invtype = invtype;
	}

	public void setItemunit(String itemunit) {
		this.itemunit = itemunit;
	}

	public void setItemamount(String itemamount) {
		this.itemamount = itemamount;
	}

	public void setItemprice(String itemprice) {
		this.itemprice = itemprice;
	}

	public void setItemmny(String itemmny) {
		this.itemmny = itemmny;
	}

	public void setItemtaxrate(String itemtaxrate) {
		this.itemtaxrate = itemtaxrate;
	}

	public void setItemtaxmny(String itemtaxmny) {
		this.itemtaxmny = itemtaxmny;
	}

	public void setNtotaltax(String ntotaltax) {
		this.ntotaltax = ntotaltax;
	}

	public void setVsaleopenacc(String vsaleopenacc) {
		this.vsaleopenacc = vsaleopenacc;
	}

	public void setVpurbankname(String vpurbankname) {
		this.vpurbankname = vpurbankname;
	}

	public void setVsalebankname(String vsalebankname) {
		this.vsalebankname = vsalebankname;
	}

	public void setVsalephoneaddr(String vsalephoneaddr) {
		this.vsalephoneaddr = vsalephoneaddr;
	}

	public void setGcode(String gcode) {
		this.gcode = gcode;
	}

	public void setCoperatorname(String coperatorname) {
		this.coperatorname = coperatorname;
	}

	public void setDoperatedate(String doperatedate) {
		this.doperatedate = doperatedate;
	}

	public List<String[]> getColList() {
		return colList;
	}

	public void setColList(List<String[]> colList) {
		this.colList = colList;
	}
	
	public List<String> getElist() {
		return elist;
	}

	public void setElist(List<String> elist) {
		this.elist = elist;
	}
	
	public String getNmny() {
		return nmny;
	}

	public String getJym() {
		return jym;
	}

	public void setNmny(String nmny) {
		this.nmny = nmny;
	}

	public void setJym(String jym) {
		this.jym = jym;
	}
	
	public String getPk_corp() {
		return pk_corp;
	}

	public String getPk_image_group() {
		return pk_image_group;
	}

	public String getPk_image_library() {
		return pk_image_library;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public void setPk_image_group(String pk_image_group) {
		this.pk_image_group = pk_image_group;
	}

	public void setPk_image_library(String pk_image_library) {
		this.pk_image_library = pk_image_library;
	}
	
	public String getPk_image_ocrlibrary() {
		return pk_image_ocrlibrary;
	}

	public void setPk_image_ocrlibrary(String pk_image_ocrlibrary) {
		this.pk_image_ocrlibrary = pk_image_ocrlibrary;
	}
	
	public String getPk_invoice() {
		return pk_invoice;
	}

	public void setPk_invoice(String pk_invoice) {
		this.pk_invoice = pk_invoice;
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
