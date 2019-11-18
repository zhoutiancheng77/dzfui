package com.dzf.zxkj.platform.model.piaotong;

import java.util.List;
import com.dzf.zxkj.common.model.SuperVO;

public class PiaoTongResHVO extends SuperVO<PiaoTongResBVO> {
	
	private String invoiceReqSerialNo;//发票请求流水号
	private String sellerEnterpriseName;//销货方纳税人名称
	private String sellerTaxpayerNum;//销货方纳税人识别号
	private String invoiceCode;//发票代码
	private String invoiceNo;//发票号码
	private String invoiceTime;//开票日期
	private String invoiceType;//开票类型
	private String amount;//开票合计金额
	private String noTaxAmount;//合计不含税金额
	private String taxAmount;//开票合计税额
	private String buyerTaxpayerNum;//购货方纳税人识别号
	private String buyerName;//购货方纳税人名称
	private String buyerBankName;//购货方银行
	private String buyerBankAccount;//购货方银行账号
	private String buyerAddress;//购货方地址
	private String buyerTel;//购货方电话
	private String buyerProvince;//购货方省份
	private String buyerPhone;//购货方手机
	private String buyerEmail;//购货方邮箱
	private String originalInvoiceNo;//原发票号码
	private String originalInvoiceCode;//原发票代码
	private String machineCode;//机器编号
	private String drawerName;//开票员
	private String takerName;//收款员
	private String reviewerName;//复核人
	private String invoiceKindCode;//发票种类代码
	private String sellerAddress;//销售方地址
	private String sellerTel;//销售方电话
	private String sellerBankName;//销售方银行
	private String sellerBankAccount;//销售方银行账号
	private String extensionNum;//分机号
	private String businessPlatformCode;//电商平台编码
	private String agentInvoiceFlag;//代开标志
	private String specialRedFlag;//特殊冲红标志
	private String redReason;//冲红原因
	private String taxClassificationCodeVersion;//编码表版本号
	private String taxControlCode;//税控码
	private String qrCode;//二维码
	private String remark;//备注
	private String cipherText;//防伪密文
	private String securityCode;//校验码
	private String specialInvoiceKind;//特殊票种
	private String includeTaxValueFlag;//含税税率标识
	private String buyFlag;//收购标志
	
	private List<PiaoTongResBVO> itemList;//开票项目列表信息
	
	public List<PiaoTongResBVO> getItemList() {
		return itemList;
	}
	public void setItemList(List<PiaoTongResBVO> itemList) {
		this.itemList = itemList;
	}
	public String getInvoiceReqSerialNo() {
		return invoiceReqSerialNo;
	}
	public String getSellerEnterpriseName() {
		return sellerEnterpriseName;
	}
	public String getSellerTaxpayerNum() {
		return sellerTaxpayerNum;
	}
	public String getInvoiceCode() {
		return invoiceCode;
	}
	public String getInvoiceNo() {
		return invoiceNo;
	}
	public String getInvoiceTime() {
		return invoiceTime;
	}
	public String getInvoiceType() {
		return invoiceType;
	}
	public String getAmount() {
		return amount;
	}
	public String getNoTaxAmount() {
		return noTaxAmount;
	}
	public String getTaxAmount() {
		return taxAmount;
	}
	public String getBuyerTaxpayerNum() {
		return buyerTaxpayerNum;
	}
	public String getBuyerName() {
		return buyerName;
	}
	public String getBuyerBankName() {
		return buyerBankName;
	}
	public String getBuyerBankAccount() {
		return buyerBankAccount;
	}
	public String getBuyerAddress() {
		return buyerAddress;
	}
	public String getBuyerTel() {
		return buyerTel;
	}
	public String getBuyerProvince() {
		return buyerProvince;
	}
	public String getBuyerPhone() {
		return buyerPhone;
	}
	public String getBuyerEmail() {
		return buyerEmail;
	}
	public String getOriginalInvoiceNo() {
		return originalInvoiceNo;
	}
	public String getOriginalInvoiceCode() {
		return originalInvoiceCode;
	}
	public String getMachineCode() {
		return machineCode;
	}
	public String getDrawerName() {
		return drawerName;
	}
	public String getTakerName() {
		return takerName;
	}
	public String getReviewerName() {
		return reviewerName;
	}
	public String getInvoiceKindCode() {
		return invoiceKindCode;
	}
	public String getSellerAddress() {
		return sellerAddress;
	}
	public String getSellerTel() {
		return sellerTel;
	}
	public String getSellerBankName() {
		return sellerBankName;
	}
	public String getSellerBankAccount() {
		return sellerBankAccount;
	}
	public String getExtensionNum() {
		return extensionNum;
	}
	public String getBusinessPlatformCode() {
		return businessPlatformCode;
	}
	public String getAgentInvoiceFlag() {
		return agentInvoiceFlag;
	}
	public String getSpecialRedFlag() {
		return specialRedFlag;
	}
	public String getRedReason() {
		return redReason;
	}
	public String getTaxClassificationCodeVersion() {
		return taxClassificationCodeVersion;
	}
	public String getTaxControlCode() {
		return taxControlCode;
	}
	public String getQrCode() {
		return qrCode;
	}
	public String getRemark() {
		return remark;
	}
	public String getCipherText() {
		return cipherText;
	}
	public String getSecurityCode() {
		return securityCode;
	}
	public String getSpecialInvoiceKind() {
		return specialInvoiceKind;
	}
	public String getIncludeTaxValueFlag() {
		return includeTaxValueFlag;
	}
	public String getBuyFlag() {
		return buyFlag;
	}
	public void setInvoiceReqSerialNo(String invoiceReqSerialNo) {
		this.invoiceReqSerialNo = invoiceReqSerialNo;
	}
	public void setSellerEnterpriseName(String sellerEnterpriseName) {
		this.sellerEnterpriseName = sellerEnterpriseName;
	}
	public void setSellerTaxpayerNum(String sellerTaxpayerNum) {
		this.sellerTaxpayerNum = sellerTaxpayerNum;
	}
	public void setInvoiceCode(String invoiceCode) {
		this.invoiceCode = invoiceCode;
	}
	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}
	public void setInvoiceTime(String invoiceTime) {
		this.invoiceTime = invoiceTime;
	}
	public void setInvoiceType(String invoiceType) {
		this.invoiceType = invoiceType;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public void setNoTaxAmount(String noTaxAmount) {
		this.noTaxAmount = noTaxAmount;
	}
	public void setTaxAmount(String taxAmount) {
		this.taxAmount = taxAmount;
	}
	public void setBuyerTaxpayerNum(String buyerTaxpayerNum) {
		this.buyerTaxpayerNum = buyerTaxpayerNum;
	}
	public void setBuyerName(String buyerName) {
		this.buyerName = buyerName;
	}
	public void setBuyerBankName(String buyerBankName) {
		this.buyerBankName = buyerBankName;
	}
	public void setBuyerBankAccount(String buyerBankAccount) {
		this.buyerBankAccount = buyerBankAccount;
	}
	public void setBuyerAddress(String buyerAddress) {
		this.buyerAddress = buyerAddress;
	}
	public void setBuyerTel(String buyerTel) {
		this.buyerTel = buyerTel;
	}
	public void setBuyerProvince(String buyerProvince) {
		this.buyerProvince = buyerProvince;
	}
	public void setBuyerPhone(String buyerPhone) {
		this.buyerPhone = buyerPhone;
	}
	public void setBuyerEmail(String buyerEmail) {
		this.buyerEmail = buyerEmail;
	}
	public void setOriginalInvoiceNo(String originalInvoiceNo) {
		this.originalInvoiceNo = originalInvoiceNo;
	}
	public void setOriginalInvoiceCode(String originalInvoiceCode) {
		this.originalInvoiceCode = originalInvoiceCode;
	}
	public void setMachineCode(String machineCode) {
		this.machineCode = machineCode;
	}
	public void setDrawerName(String drawerName) {
		this.drawerName = drawerName;
	}
	public void setTakerName(String takerName) {
		this.takerName = takerName;
	}
	public void setReviewerName(String reviewerName) {
		this.reviewerName = reviewerName;
	}
	public void setInvoiceKindCode(String invoiceKindCode) {
		this.invoiceKindCode = invoiceKindCode;
	}
	public void setSellerAddress(String sellerAddress) {
		this.sellerAddress = sellerAddress;
	}
	public void setSellerTel(String sellerTel) {
		this.sellerTel = sellerTel;
	}
	public void setSellerBankName(String sellerBankName) {
		this.sellerBankName = sellerBankName;
	}
	public void setSellerBankAccount(String sellerBankAccount) {
		this.sellerBankAccount = sellerBankAccount;
	}
	public void setExtensionNum(String extensionNum) {
		this.extensionNum = extensionNum;
	}
	public void setBusinessPlatformCode(String businessPlatformCode) {
		this.businessPlatformCode = businessPlatformCode;
	}
	public void setAgentInvoiceFlag(String agentInvoiceFlag) {
		this.agentInvoiceFlag = agentInvoiceFlag;
	}
	public void setSpecialRedFlag(String specialRedFlag) {
		this.specialRedFlag = specialRedFlag;
	}
	public void setRedReason(String redReason) {
		this.redReason = redReason;
	}
	public void setTaxClassificationCodeVersion(String taxClassificationCodeVersion) {
		this.taxClassificationCodeVersion = taxClassificationCodeVersion;
	}
	public void setTaxControlCode(String taxControlCode) {
		this.taxControlCode = taxControlCode;
	}
	public void setQrCode(String qrCode) {
		this.qrCode = qrCode;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public void setCipherText(String cipherText) {
		this.cipherText = cipherText;
	}
	public void setSecurityCode(String securityCode) {
		this.securityCode = securityCode;
	}
	public void setSpecialInvoiceKind(String specialInvoiceKind) {
		this.specialInvoiceKind = specialInvoiceKind;
	}
	public void setIncludeTaxValueFlag(String includeTaxValueFlag) {
		this.includeTaxValueFlag = includeTaxValueFlag;
	}
	public void setBuyFlag(String buyFlag) {
		this.buyFlag = buyFlag;
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