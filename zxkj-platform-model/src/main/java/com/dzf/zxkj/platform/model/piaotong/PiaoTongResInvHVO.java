package com.dzf.zxkj.platform.model.piaotong;

import com.dzf.zxkj.common.model.SuperVO;

import java.util.List;

public class PiaoTongResInvHVO extends SuperVO<PiaoTongResInvBVO> {
	
	private String invoiceReqSerialNo;//发票请求流水号
	private String invoiceOperationCode;//发票操作码 10 开具蓝票 20 开具红票 30作废蓝票 40 作废红票
	private String sellerTaxpayerNum;//销方 纳税人 识 别号
	private String sellerName;//销售方名称
	private String sellerAddress;//销售方地址
	private String sellerTel;//销售方电话
	private String sellerBankName;//销售方开户行
	private String sellerBankAccount;//销 售 方 银 行 账 号
	
	private String buyerName;//购买方名称
	private String buyerTaxpayerNum;//购 买 方 纳 税 人 识别号
	private String buyerAddress;//购买方地址
	private String buyerTel;//购买方电话
	private String buyerBankName;//购买方开户行
	private String buyerBankAccount;//购 买 方 银 行 账 号
	
	private String drawerName;//开票人名称
	private String casherName;//收款人名称
	private String reviewerName;//复核人名称
	
	private String noTaxAmount;//不含税金额
	private String taxAmount;//税额
	private String amountWithTax;//价税合计
	
	private String remark;//备注
	
	private String tradeNo;//订单号

	private String invoiceKindCode;//发票种类代码 10： 增值税电子普通发票  04： 增值税普通发票 01： 增值税专用发票 41： 增值税普通发票（ 卷票）
	private String specialInvoiceKind;//特殊票种标识
	
	private String invoiceType;//开票类型 1 蓝票 2 红票
	private String agentInvoiceFlag;//代开标志 0 自开
	
	private String taxRateFlag;//税率标识0 正常票 2 差额票
	
	private String extensionNum;//分机号
	private String machineCode;//机器编号
	private String diskType;//设备类型JSP;金税盘 SKP:税控盘
	
	private String invoiceStatus;//0:未开票， 1:开票成功， 2:开 票失败， 3:开票中
	
	private String invoiceDate;//开票日期
	
	private String invoiceIssueFailReason;//开票失败原因 开票失败原因， 开具失败时该值必填
	private String invoiceOperationFailReason;//操作失败原因 操作失败原因， 作废失败时该值必填
	
	private String invoiceCode;//发票代码
	private String invoiceNo;//发票号码
	
	private String checkCode;//校验码
	private String qrCode;//二维码
	
	private String downloadUrl;//发票下载 Url
	
	private String redFlag;//冲红标志
	private String invoiceRedReason;//冲红原因
	
	private String oldInvoiceCode;//原发票代码
	private String oldInvoiceNo;//原发票号码
	
	private String destroyFlag;//作废标志 0 未作废 1 已作废 2 作废失败 3 作废中
	private String invoiceDestroySerialNo;//作废 请求流 水 号
	private String invoiceDestroyReason;//作废原因
	private String invoiceDestroyerName;//作废人
	private String invoiceDestroyTime;//作废时间
	private String invoiceQrcodeNo;//二维码编号
	
//	private String detailedListFlag;//清单标识
//	private String detailedListItemName;//清单项目名称
	
	private List<PiaoTongResInvBVO> itemList;//开票项目列表信息
	
	public String getInvoiceReqSerialNo() {
		return invoiceReqSerialNo;
	}
	public void setInvoiceReqSerialNo(String invoiceReqSerialNo) {
		this.invoiceReqSerialNo = invoiceReqSerialNo;
	}
	public String getInvoiceOperationCode() {
		return invoiceOperationCode;
	}
	public void setInvoiceOperationCode(String invoiceOperationCode) {
		this.invoiceOperationCode = invoiceOperationCode;
	}
	public String getSellerTaxpayerNum() {
		return sellerTaxpayerNum;
	}
	public void setSellerTaxpayerNum(String sellerTaxpayerNum) {
		this.sellerTaxpayerNum = sellerTaxpayerNum;
	}
	public String getSellerName() {
		return sellerName;
	}
	public void setSellerName(String sellerName) {
		this.sellerName = sellerName;
	}
	public String getSellerAddress() {
		return sellerAddress;
	}
	public void setSellerAddress(String sellerAddress) {
		this.sellerAddress = sellerAddress;
	}
	public String getSellerTel() {
		return sellerTel;
	}
	public void setSellerTel(String sellerTel) {
		this.sellerTel = sellerTel;
	}
	public String getSellerBankName() {
		return sellerBankName;
	}
	public void setSellerBankName(String sellerBankName) {
		this.sellerBankName = sellerBankName;
	}
	public String getSellerBankAccount() {
		return sellerBankAccount;
	}
	public void setSellerBankAccount(String sellerBankAccount) {
		this.sellerBankAccount = sellerBankAccount;
	}
	public String getBuyerName() {
		return buyerName;
	}
	public void setBuyerName(String buyerName) {
		this.buyerName = buyerName;
	}
	public String getBuyerTaxpayerNum() {
		return buyerTaxpayerNum;
	}
	public void setBuyerTaxpayerNum(String buyerTaxpayerNum) {
		this.buyerTaxpayerNum = buyerTaxpayerNum;
	}
	public String getBuyerAddress() {
		return buyerAddress;
	}
	public void setBuyerAddress(String buyerAddress) {
		this.buyerAddress = buyerAddress;
	}
	public String getBuyerTel() {
		return buyerTel;
	}
	public void setBuyerTel(String buyerTel) {
		this.buyerTel = buyerTel;
	}
	public String getBuyerBankName() {
		return buyerBankName;
	}
	public void setBuyerBankName(String buyerBankName) {
		this.buyerBankName = buyerBankName;
	}
	public String getBuyerBankAccount() {
		return buyerBankAccount;
	}
	public void setBuyerBankAccount(String buyerBankAccount) {
		this.buyerBankAccount = buyerBankAccount;
	}
	public String getDrawerName() {
		return drawerName;
	}
	public void setDrawerName(String drawerName) {
		this.drawerName = drawerName;
	}
	public String getCasherName() {
		return casherName;
	}
	public void setCasherName(String casherName) {
		this.casherName = casherName;
	}
	public String getReviewerName() {
		return reviewerName;
	}
	public void setReviewerName(String reviewerName) {
		this.reviewerName = reviewerName;
	}
	public String getNoTaxAmount() {
		return noTaxAmount;
	}
	public void setNoTaxAmount(String noTaxAmount) {
		this.noTaxAmount = noTaxAmount;
	}
	public String getTaxAmount() {
		return taxAmount;
	}
	public void setTaxAmount(String taxAmount) {
		this.taxAmount = taxAmount;
	}
	public String getAmountWithTax() {
		return amountWithTax;
	}
	public void setAmountWithTax(String amountWithTax) {
		this.amountWithTax = amountWithTax;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getTradeNo() {
		return tradeNo;
	}
	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}
	public String getInvoiceKindCode() {
		return invoiceKindCode;
	}
	public void setInvoiceKindCode(String invoiceKindCode) {
		this.invoiceKindCode = invoiceKindCode;
	}
	public String getSpecialInvoiceKind() {
		return specialInvoiceKind;
	}
	public void setSpecialInvoiceKind(String specialInvoiceKind) {
		this.specialInvoiceKind = specialInvoiceKind;
	}
	public String getInvoiceType() {
		return invoiceType;
	}
	public void setInvoiceType(String invoiceType) {
		this.invoiceType = invoiceType;
	}
	public String getAgentInvoiceFlag() {
		return agentInvoiceFlag;
	}
	public void setAgentInvoiceFlag(String agentInvoiceFlag) {
		this.agentInvoiceFlag = agentInvoiceFlag;
	}
	public String getTaxRateFlag() {
		return taxRateFlag;
	}
	public void setTaxRateFlag(String taxRateFlag) {
		this.taxRateFlag = taxRateFlag;
	}
	public String getExtensionNum() {
		return extensionNum;
	}
	public void setExtensionNum(String extensionNum) {
		this.extensionNum = extensionNum;
	}
	public String getMachineCode() {
		return machineCode;
	}
	public void setMachineCode(String machineCode) {
		this.machineCode = machineCode;
	}
	public String getDiskType() {
		return diskType;
	}
	public void setDiskType(String diskType) {
		this.diskType = diskType;
	}
	public String getInvoiceStatus() {
		return invoiceStatus;
	}
	public void setInvoiceStatus(String invoiceStatus) {
		this.invoiceStatus = invoiceStatus;
	}
	public String getInvoiceDate() {
		return invoiceDate;
	}
	public void setInvoiceDate(String invoiceDate) {
		this.invoiceDate = invoiceDate;
	}
	public String getInvoiceIssueFailReason() {
		return invoiceIssueFailReason;
	}
	public void setInvoiceIssueFailReason(String invoiceIssueFailReason) {
		this.invoiceIssueFailReason = invoiceIssueFailReason;
	}
	public String getInvoiceOperationFailReason() {
		return invoiceOperationFailReason;
	}
	public void setInvoiceOperationFailReason(String invoiceOperationFailReason) {
		this.invoiceOperationFailReason = invoiceOperationFailReason;
	}
	public String getInvoiceCode() {
		return invoiceCode;
	}
	public void setInvoiceCode(String invoiceCode) {
		this.invoiceCode = invoiceCode;
	}
	public String getInvoiceNo() {
		return invoiceNo;
	}
	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}
	public String getCheckCode() {
		return checkCode;
	}
	public void setCheckCode(String checkCode) {
		this.checkCode = checkCode;
	}
	public String getQrCode() {
		return qrCode;
	}
	public void setQrCode(String qrCode) {
		this.qrCode = qrCode;
	}
	public String getDownloadUrl() {
		return downloadUrl;
	}
	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}
	public String getRedFlag() {
		return redFlag;
	}
	public void setRedFlag(String redFlag) {
		this.redFlag = redFlag;
	}
	public String getInvoiceRedReason() {
		return invoiceRedReason;
	}
	public void setInvoiceRedReason(String invoiceRedReason) {
		this.invoiceRedReason = invoiceRedReason;
	}
	public String getOldInvoiceCode() {
		return oldInvoiceCode;
	}
	public void setOldInvoiceCode(String oldInvoiceCode) {
		this.oldInvoiceCode = oldInvoiceCode;
	}
	public String getOldInvoiceNo() {
		return oldInvoiceNo;
	}
	public void setOldInvoiceNo(String oldInvoiceNo) {
		this.oldInvoiceNo = oldInvoiceNo;
	}
	public String getDestroyFlag() {
		return destroyFlag;
	}
	public void setDestroyFlag(String destroyFlag) {
		this.destroyFlag = destroyFlag;
	}
	public String getInvoiceDestroySerialNo() {
		return invoiceDestroySerialNo;
	}
	public void setInvoiceDestroySerialNo(String invoiceDestroySerialNo) {
		this.invoiceDestroySerialNo = invoiceDestroySerialNo;
	}
	public String getInvoiceDestroyReason() {
		return invoiceDestroyReason;
	}
	public void setInvoiceDestroyReason(String invoiceDestroyReason) {
		this.invoiceDestroyReason = invoiceDestroyReason;
	}
	public String getInvoiceDestroyerName() {
		return invoiceDestroyerName;
	}
	public void setInvoiceDestroyerName(String invoiceDestroyerName) {
		this.invoiceDestroyerName = invoiceDestroyerName;
	}
	public String getInvoiceDestroyTime() {
		return invoiceDestroyTime;
	}
	public void setInvoiceDestroyTime(String invoiceDestroyTime) {
		this.invoiceDestroyTime = invoiceDestroyTime;
	}
	public String getInvoiceQrcodeNo() {
		return invoiceQrcodeNo;
	}
	public void setInvoiceQrcodeNo(String invoiceQrcodeNo) {
		this.invoiceQrcodeNo = invoiceQrcodeNo;
	}
	public List<PiaoTongResInvBVO> getItemList() {
		return itemList;
	}
	public void setItemList(List<PiaoTongResInvBVO> itemList) {
		this.itemList = itemList;
	}
	@Override
	public String getParentPKFieldName() {
		return null;
	}
	@Override
	public String getPKFieldName() {
		return null;
	}
	@Override
	public String getTableName() {
		return null;
	}
	
}