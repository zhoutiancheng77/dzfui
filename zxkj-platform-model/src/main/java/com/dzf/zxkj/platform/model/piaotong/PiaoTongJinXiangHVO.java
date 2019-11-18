package com.dzf.zxkj.platform.model.piaotong;

import java.util.List;

import com.dzf.zxkj.common.model.SuperVO;

/**
 * 票通进项采集数据 主表
 * @author wangzhn
 *
 */
public class PiaoTongJinXiangHVO extends SuperVO {
	
	private String nextRequestTime;//下次请求时间
	
	/*******************增值税发票01/04/10/11*********************/
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
	
	private String imagePath;//图片路径
	
	//冗余字段
	private String deductibleStatus;//认证状态
	private String deductibleDate;//认证日期
	private String deductiblePeriod;//认证所属期
	
	
	/********************货运运输业增值税专用发票02*******************/
	private String carrierName;//承运人名称
	private String carrierNum;//承运人识别号
	private String consigneeName;//收货人名称
	private String consigneeNum;//收货人识别号
	private String consignerName;//发货人名称
	private String consignerNum;//发货人识别号
	private String draweeName;//收票方名称
	private String draweeNum;//收票方识别号
	private String cargoInfo;//运输货物信息
	private String startPlace;//起运地、经由、到达地
	private String taxRate;//税率
	private String taxDiskNum;//税控盘号
	private String carTypeNum;//车种车号
	private String carTon;//车船吨位
	private String taxAuthorities;//主管税务机关
	private String competentTaxName;//主管税务名称
	private String costProject;//费用项目
	private String money;//金额
	/********************机动车销售统一发票 03***********************/
	private String machineNum;//机器编码
	private String salesTaxpayerNum;//销方纳税人识别号  --与salesTaxNum同义
	private String vehicleType;//车辆类型
	private String factoryType;//厂牌型号
	private String productionPlace;//产地
	private String certificateNum;//合格证号
	private String inspectionNum;//商检单号
	private String engineNum;//发动机号
	private String vehicleIdentityNum;//车辆识别代号/车架号码
	private String importCardNum;//进口证明书号
	private String sellerName;//销货单位名称
	private String phoneNum;//电话
	private String account;//账号
	private String address;//地址
	private String depositBank;//开户银行
//	private String taxRate;//增值税税率或征收率
	private String taxAuthoritiesNum;//主管税务机关代码
	private String taxPaiedProofNum;//完税凭证号码
	private String ton;//吨位
	private String limitPerson;//限乘人数
//	private String competentTaxName;//主管税务机关名称
	
	public String getDeductibleStatus() {
		return deductibleStatus;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public String getDeductibleDate() {
		return deductibleDate;
	}

	public String getDeductiblePeriod() {
		return deductiblePeriod;
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

	public List<PiaoTongJinXiangBVO> getInvoiceVatDetailsList() {
		return invoiceVatDetailsList;
	}

	public void setInvoiceVatDetailsList(List<PiaoTongJinXiangBVO> invoiceVatDetailsList) {
		this.invoiceVatDetailsList = invoiceVatDetailsList;
	}

	public String getCarrierName() {
		return carrierName;
	}

	public String getCarrierNum() {
		return carrierNum;
	}

	public String getConsigneeName() {
		return consigneeName;
	}

	public String getConsigneeNum() {
		return consigneeNum;
	}

	public String getConsignerName() {
		return consignerName;
	}

	public String getConsignerNum() {
		return consignerNum;
	}

	public String getDraweeName() {
		return draweeName;
	}

	public String getDraweeNum() {
		return draweeNum;
	}

	public String getCargoInfo() {
		return cargoInfo;
	}

	public String getStartPlace() {
		return startPlace;
	}

	public String getTaxRate() {
		return taxRate;
	}

	public String getTaxDiskNum() {
		return taxDiskNum;
	}

	public String getCarTypeNum() {
		return carTypeNum;
	}

	public String getCarTon() {
		return carTon;
	}

	public String getTaxAuthorities() {
		return taxAuthorities;
	}

	public String getCompetentTaxName() {
		return competentTaxName;
	}

	public String getCostProject() {
		return costProject;
	}

	public String getMoney() {
		return money;
	}

	public String getMachineNum() {
		return machineNum;
	}

	public String getSalesTaxpayerNum() {
		return salesTaxpayerNum;
	}

	public String getVehicleType() {
		return vehicleType;
	}

	public String getFactoryType() {
		return factoryType;
	}

	public String getProductionPlace() {
		return productionPlace;
	}

	public String getCertificateNum() {
		return certificateNum;
	}

	public String getInspectionNum() {
		return inspectionNum;
	}

	public String getEngineNum() {
		return engineNum;
	}

	public String getVehicleIdentityNum() {
		return vehicleIdentityNum;
	}

	public String getImportCardNum() {
		return importCardNum;
	}

	public String getSellerName() {
		return sellerName;
	}

	public String getPhoneNum() {
		return phoneNum;
	}

	public String getAccount() {
		return account;
	}

	public String getAddress() {
		return address;
	}

	public String getDepositBank() {
		return depositBank;
	}

	public String getTaxAuthoritiesNum() {
		return taxAuthoritiesNum;
	}

	public String getTaxPaiedProofNum() {
		return taxPaiedProofNum;
	}

	public String getTon() {
		return ton;
	}

	public String getLimitPerson() {
		return limitPerson;
	}

	public void setCarrierName(String carrierName) {
		this.carrierName = carrierName;
	}

	public void setCarrierNum(String carrierNum) {
		this.carrierNum = carrierNum;
	}

	public void setConsigneeName(String consigneeName) {
		this.consigneeName = consigneeName;
	}

	public void setConsigneeNum(String consigneeNum) {
		this.consigneeNum = consigneeNum;
	}

	public void setConsignerName(String consignerName) {
		this.consignerName = consignerName;
	}

	public void setConsignerNum(String consignerNum) {
		this.consignerNum = consignerNum;
	}

	public void setDraweeName(String draweeName) {
		this.draweeName = draweeName;
	}

	public void setDraweeNum(String draweeNum) {
		this.draweeNum = draweeNum;
	}

	public void setCargoInfo(String cargoInfo) {
		this.cargoInfo = cargoInfo;
	}

	public void setStartPlace(String startPlace) {
		this.startPlace = startPlace;
	}

	public void setTaxRate(String taxRate) {
		this.taxRate = taxRate;
	}

	public void setTaxDiskNum(String taxDiskNum) {
		this.taxDiskNum = taxDiskNum;
	}

	public void setCarTypeNum(String carTypeNum) {
		this.carTypeNum = carTypeNum;
	}

	public void setCarTon(String carTon) {
		this.carTon = carTon;
	}

	public void setTaxAuthorities(String taxAuthorities) {
		this.taxAuthorities = taxAuthorities;
	}

	public void setCompetentTaxName(String competentTaxName) {
		this.competentTaxName = competentTaxName;
	}

	public void setCostProject(String costProject) {
		this.costProject = costProject;
	}

	public void setMoney(String money) {
		this.money = money;
	}

	public void setMachineNum(String machineNum) {
		this.machineNum = machineNum;
	}

	public void setSalesTaxpayerNum(String salesTaxpayerNum) {
		this.salesTaxpayerNum = salesTaxpayerNum;
	}

	public void setVehicleType(String vehicleType) {
		this.vehicleType = vehicleType;
	}

	public void setFactoryType(String factoryType) {
		this.factoryType = factoryType;
	}

	public void setProductionPlace(String productionPlace) {
		this.productionPlace = productionPlace;
	}

	public void setCertificateNum(String certificateNum) {
		this.certificateNum = certificateNum;
	}

	public void setInspectionNum(String inspectionNum) {
		this.inspectionNum = inspectionNum;
	}

	public void setEngineNum(String engineNum) {
		this.engineNum = engineNum;
	}

	public void setVehicleIdentityNum(String vehicleIdentityNum) {
		this.vehicleIdentityNum = vehicleIdentityNum;
	}

	public void setImportCardNum(String importCardNum) {
		this.importCardNum = importCardNum;
	}

	public void setSellerName(String sellerName) {
		this.sellerName = sellerName;
	}

	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setDepositBank(String depositBank) {
		this.depositBank = depositBank;
	}

	public void setTaxAuthoritiesNum(String taxAuthoritiesNum) {
		this.taxAuthoritiesNum = taxAuthoritiesNum;
	}

	public void setTaxPaiedProofNum(String taxPaiedProofNum) {
		this.taxPaiedProofNum = taxPaiedProofNum;
	}

	public void setTon(String ton) {
		this.ton = ton;
	}

	public void setLimitPerson(String limitPerson) {
		this.limitPerson = limitPerson;
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
