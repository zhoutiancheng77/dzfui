package com.dzf.zxkj.platform.model.piaotong;

import java.util.List;
import com.dzf.zxkj.common.model.SuperVO;

/**
 * 票通进项采集数据
 * @author wangzhn
 *
 */
public class PiaoTongJinXiangDataVO extends SuperVO {
	
	/******************认证token****************/
	private String token;
	
	/******************概要信息*****************/
	private Integer totalCount;//发票采集总数量
	
	private Integer totalPageNum;//分页总数
	
	private Integer currentPageNum;//当前页数
	
	private String nextRequestTime;//下次请求时间

	private List<PiaoTongJinXiangInvoiceVO> invoiceList;//采集到的发票集合
	
	/******************明细信息*****************/
	private String salesContactWay;//销方地址电话
	
	private String salesBankAccount;//销方开户行及账号
	
	private String buyerContactWay;//购方地址电话
	
	private String buyerBankAccount;//购方开户行及账号
	
	private String checkCode;//校验码
	
	private String machineCode;//机器码
	
	private String invoiceCode;//发票代码
	
	private String invoiceNum;//发票号码
	
	private String billingDate;//开票日期
	
	private String buyerName;//购方名称
	
	private String buyerTaxNum;//购方纳税人识别号
	
	private String salesName;//销方名称
	
	private String salesTaxNum;//销方纳税人识别号
	
	private String totalTaxAmount;//合计税额
	
	private String totalAmount;//价税合计
	
	private String totalMoney;//合计金额
	
	private String invoiceTicketType;//票种代码  01-增值税专用发票 02-货运运输业增值税专用发票 03-机动车销售统一发票
	
	private String invoiceStatus;//发票异常状态  0-正常 1-失控 2-作废 3-冲红 4-异常
	
	private String remarks;//备注
	
	private List<PiaoTongJinXiangBVO> invoiceVatDetailsList;//明细
	
	//------------------------------机动车发票 -------------------------------------
	
	private String salestaxpayernum;//销方纳税人识别号
	private String vehicletype;//车辆类型
	private String factorytype;//厂牌型号
	private String taxrate;//税率
	private String sellername;//销货单位名称
	private String phonenum;//电话
	private String account;//账号
	private String address;//地址
	private String depositbank;//开户银行
	
	
	
	
//	private PiaoTongJinXiangHVO invoiceContent;
//	
//	public PiaoTongJinXiangHVO getInvoiceContent() {
//		return invoiceContent;
//	}
//
//	public void setInvoiceContent(PiaoTongJinXiangHVO invoiceContent) {
//		this.invoiceContent = invoiceContent;
//	}

	public String getToken() {
		return token;
	}

	public String getSalestaxpayernum() {
		return salestaxpayernum;
	}

	public void setSalestaxpayernum(String salestaxpayernum) {
		this.salestaxpayernum = salestaxpayernum;
	}

	public String getVehicletype() {
		return vehicletype;
	}

	public void setVehicletype(String vehicletype) {
		this.vehicletype = vehicletype;
	}

	public String getFactorytype() {
		return factorytype;
	}

	public void setFactorytype(String factorytype) {
		this.factorytype = factorytype;
	}

	public String getTaxrate() {
		return taxrate;
	}

	public void setTaxrate(String taxrate) {
		this.taxrate = taxrate;
	}

	public String getSellername() {
		return sellername;
	}

	public void setSellername(String sellername) {
		this.sellername = sellername;
	}

	public String getPhonenum() {
		return phonenum;
	}

	public void setPhonenum(String phonenum) {
		this.phonenum = phonenum;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getDepositbank() {
		return depositbank;
	}

	public void setDepositbank(String depositbank) {
		this.depositbank = depositbank;
	}

	public String getSalesContactWay() {
		return salesContactWay;
	}

	public String getSalesBankAccount() {
		return salesBankAccount;
	}

	public String getBuyerContactWay() {
		return buyerContactWay;
	}

	public String getBuyerBankAccount() {
		return buyerBankAccount;
	}

	public String getCheckCode() {
		return checkCode;
	}

	public String getMachineCode() {
		return machineCode;
	}

	public String getInvoiceCode() {
		return invoiceCode;
	}

	public String getInvoiceNum() {
		return invoiceNum;
	}

	public String getBillingDate() {
		return billingDate;
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

	public String getRemarks() {
		return remarks;
	}

	public List<PiaoTongJinXiangBVO> getInvoiceVatDetailsList() {
		return invoiceVatDetailsList;
	}

	public void setSalesContactWay(String salesContactWay) {
		this.salesContactWay = salesContactWay;
	}

	public void setSalesBankAccount(String salesBankAccount) {
		this.salesBankAccount = salesBankAccount;
	}

	public void setBuyerContactWay(String buyerContactWay) {
		this.buyerContactWay = buyerContactWay;
	}

	public void setBuyerBankAccount(String buyerBankAccount) {
		this.buyerBankAccount = buyerBankAccount;
	}

	public void setCheckCode(String checkCode) {
		this.checkCode = checkCode;
	}

	public void setMachineCode(String machineCode) {
		this.machineCode = machineCode;
	}

	public void setInvoiceCode(String invoiceCode) {
		this.invoiceCode = invoiceCode;
	}

	public void setInvoiceNum(String invoiceNum) {
		this.invoiceNum = invoiceNum;
	}

	public void setBillingDate(String billingDate) {
		this.billingDate = billingDate;
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

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public void setInvoiceVatDetailsList(List<PiaoTongJinXiangBVO> invoiceVatDetailsList) {
		this.invoiceVatDetailsList = invoiceVatDetailsList;
	}

	public Integer getTotalCount() {
		return totalCount;
	}

	public Integer getTotalPageNum() {
		return totalPageNum;
	}

	public Integer getCurrentPageNum() {
		return currentPageNum;
	}

	public List<PiaoTongJinXiangInvoiceVO> getInvoiceList() {
		return invoiceList;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
	}

	public void setTotalPageNum(Integer totalPageNum) {
		this.totalPageNum = totalPageNum;
	}

	public void setCurrentPageNum(Integer currentPageNum) {
		this.currentPageNum = currentPageNum;
	}

	public void setInvoiceList(List<PiaoTongJinXiangInvoiceVO> invoiceList) {
		this.invoiceList = invoiceList;
	}
	public String getNextRequestTime() {
		return nextRequestTime;
	}

	public void setNextRequestTime(String nextRequestTime) {
		this.nextRequestTime = nextRequestTime;
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
