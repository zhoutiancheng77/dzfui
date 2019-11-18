package com.dzf.zxkj.platform.model.piaotong;

import com.dzf.zxkj.common.model.SuperVO;

/**
 * 请求返回的内容
 * @author wangzhn
 *
 */
public class PiaoTongJinXiangInvoiceVO extends SuperVO {

	private String invoiceCode;//发票代码
	
	private String invoiceNum;//发票号码
	
	private String buyerName;//购方名称
	
	private String buyerTaxNum;//购方纳税人识别号
	
	private String salesName;//销方名称
	
	private String salesTaxNum;//销方纳税人识别号
	
	private String billingDate;//开票日期
	
	private String totalTaxAmount;//总税额
	
	private String totalAmount;//价税合计
	
	private String totalMoney;//合计金额
	
	private String invoiceTicketType;//票种代码
	
	private String invoiceStatus;//发票异常状态  0-正常 1-失控 2-作废 3-冲红 4-异常
	
	private String deductibleStatus;//认证状态  0-未认证，1-已认证
	
	private String deductibleDate;//认证日期   未认证为空
	
	private String deductiblePeriod;//认证所属期  未认证为空，格式为：yyyy-MM
	
	private String imagePath;//图片路径
	
	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public String getInvoiceCode() {
		return invoiceCode;
	}

	public String getInvoiceNum() {
		return invoiceNum;
	}

	public String getBuyerName() {
		return buyerName;
	}

	public String getBuyerTaxNum() {
		return buyerTaxNum;
	}

	public String getSalesName() {
		return salesName;
	}

	public String getSalesTaxNum() {
		return salesTaxNum;
	}

	public String getBillingDate() {
		return billingDate;
	}

	public String getTotalTaxAmount() {
		return totalTaxAmount;
	}

	public String getTotalAmount() {
		return totalAmount;
	}

	public String getTotalMoney() {
		return totalMoney;
	}

	public String getInvoiceTicketType() {
		return invoiceTicketType;
	}

	public String getInvoiceStatus() {
		return invoiceStatus;
	}

	public String getDeductibleStatus() {
		return deductibleStatus;
	}

	public String getDeductibleDate() {
		return deductibleDate;
	}

	public String getDeductiblePeriod() {
		return deductiblePeriod;
	}

	public void setInvoiceCode(String invoiceCode) {
		this.invoiceCode = invoiceCode;
	}

	public void setInvoiceNum(String invoiceNum) {
		this.invoiceNum = invoiceNum;
	}

	public void setBuyerName(String buyerName) {
		this.buyerName = buyerName;
	}

	public void setBuyerTaxNum(String buyerTaxNum) {
		this.buyerTaxNum = buyerTaxNum;
	}

	public void setSalesName(String salesName) {
		this.salesName = salesName;
	}

	public void setSalesTaxNum(String salesTaxNum) {
		this.salesTaxNum = salesTaxNum;
	}

	public void setBillingDate(String billingDate) {
		this.billingDate = billingDate;
	}

	public void setTotalTaxAmount(String totalTaxAmount) {
		this.totalTaxAmount = totalTaxAmount;
	}

	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}

	public void setTotalMoney(String totalMoney) {
		this.totalMoney = totalMoney;
	}

	public void setInvoiceTicketType(String invoiceTicketType) {
		this.invoiceTicketType = invoiceTicketType;
	}

	public void setInvoiceStatus(String invoiceStatus) {
		this.invoiceStatus = invoiceStatus;
	}

	public void setDeductibleStatus(String deductibleStatus) {
		this.deductibleStatus = deductibleStatus;
	}

	public void setDeductibleDate(String deductibleDate) {
		this.deductibleDate = deductibleDate;
	}

	public void setDeductiblePeriod(String deductiblePeriod) {
		this.deductiblePeriod = deductiblePeriod;
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
