package com.dzf.zxkj.platform.model.piaotong;

import com.dzf.zxkj.common.model.SuperVO;

public class PiaoTongResBVO extends SuperVO<PiaoTongResBVO> {
	
	private String goodsSerialNo;//商品行序号
	private String goodsName;//商品名称
	private String quantity;//商品数量
	private String invoiceAmount;//商品金额
	private String unitPrice;//商品单价
	private String meteringUnit;//单位
	private String specificationModel;//规格型号
	private String includeTaxFlag;//含税价标志
	private String deductionAmount;//扣除额
	private String taxRateAmount;//税额
	private String taxRateValue;//税率
	private String taxClassificationCode;//税商品编码
	private String customCode;//自行编码
	private String preferentialPolicyFlag;//优惠政策标识
	private String zeroTaxFla;//零税率标识
	private String vatSpecialManage;//增值税特殊管理
	private String itemType;//发票行性质
	public String getGoodsSerialNo() {
		return goodsSerialNo;
	}
	public String getGoodsName() {
		return goodsName;
	}
	public String getQuantity() {
		return quantity;
	}
	public String getInvoiceAmount() {
		return invoiceAmount;
	}
	public String getUnitPrice() {
		return unitPrice;
	}
	public String getMeteringUnit() {
		return meteringUnit;
	}
	public String getSpecificationModel() {
		return specificationModel;
	}
	public String getIncludeTaxFlag() {
		return includeTaxFlag;
	}
	public String getDeductionAmount() {
		return deductionAmount;
	}
	public String getTaxRateAmount() {
		return taxRateAmount;
	}
	public String getTaxRateValue() {
		return taxRateValue;
	}
	public String getTaxClassificationCode() {
		return taxClassificationCode;
	}
	public String getCustomCode() {
		return customCode;
	}
	public String getPreferentialPolicyFlag() {
		return preferentialPolicyFlag;
	}
	public String getZeroTaxFla() {
		return zeroTaxFla;
	}
	public String getVatSpecialManage() {
		return vatSpecialManage;
	}
	public String getItemType() {
		return itemType;
	}
	public void setGoodsSerialNo(String goodsSerialNo) {
		this.goodsSerialNo = goodsSerialNo;
	}
	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}
	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}
	public void setInvoiceAmount(String invoiceAmount) {
		this.invoiceAmount = invoiceAmount;
	}
	public void setUnitPrice(String unitPrice) {
		this.unitPrice = unitPrice;
	}
	public void setMeteringUnit(String meteringUnit) {
		this.meteringUnit = meteringUnit;
	}
	public void setSpecificationModel(String specificationModel) {
		this.specificationModel = specificationModel;
	}
	public void setIncludeTaxFlag(String includeTaxFlag) {
		this.includeTaxFlag = includeTaxFlag;
	}
	public void setDeductionAmount(String deductionAmount) {
		this.deductionAmount = deductionAmount;
	}
	public void setTaxRateAmount(String taxRateAmount) {
		this.taxRateAmount = taxRateAmount;
	}
	public void setTaxRateValue(String taxRateValue) {
		this.taxRateValue = taxRateValue;
	}
	public void setTaxClassificationCode(String taxClassificationCode) {
		this.taxClassificationCode = taxClassificationCode;
	}
	public void setCustomCode(String customCode) {
		this.customCode = customCode;
	}
	public void setPreferentialPolicyFlag(String preferentialPolicyFlag) {
		this.preferentialPolicyFlag = preferentialPolicyFlag;
	}
	public void setZeroTaxFla(String zeroTaxFla) {
		this.zeroTaxFla = zeroTaxFla;
	}
	public void setVatSpecialManage(String vatSpecialManage) {
		this.vatSpecialManage = vatSpecialManage;
	}
	public void setItemType(String itemType) {
		this.itemType = itemType;
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