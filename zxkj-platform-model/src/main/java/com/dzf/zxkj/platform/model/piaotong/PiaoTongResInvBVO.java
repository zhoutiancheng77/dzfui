package com.dzf.zxkj.platform.model.piaotong;


import com.dzf.zxkj.common.model.SuperVO;

public class PiaoTongResInvBVO extends SuperVO<PiaoTongResInvBVO> {
	
	private String itemNo;//商品行序号
	private String itemProperty;//发票行性质
	private String goodsName;//商品名称
	private String taxClassificationCode;//税收分类编码
	private String selfCode;//自行编码
	private String specificationModel;//规格型号
	private String meteringUnit;//单位
	private String quantity;//数量
	private String unitPrice;//单价
	private String taxIncludeFlag;//含税标识 0 不含税， 1 含税
	private String itemAmount;//项目金额
	private String taxRateValue;//税率
	private String taxRateAmount;//税额
	private String deduction;//扣除额
	private String preferentialPolicyFlag;//优惠政策标识 
	private String zeroTaxFlag;//零税率标识
	private String vatSpecialManage;//增 值 税 特 殊 管 理
	
	public String getItemNo() {
		return itemNo;
	}
	public void setItemNo(String itemNo) {
		this.itemNo = itemNo;
	}
	public String getGoodsName() {
		return goodsName;
	}
	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}
	public String getTaxClassificationCode() {
		return taxClassificationCode;
	}
	public void setTaxClassificationCode(String taxClassificationCode) {
		this.taxClassificationCode = taxClassificationCode;
	}
	public String getSpecificationModel() {
		return specificationModel;
	}
	public void setSpecificationModel(String specificationModel) {
		this.specificationModel = specificationModel;
	}
	public String getMeteringUnit() {
		return meteringUnit;
	}
	public void setMeteringUnit(String meteringUnit) {
		this.meteringUnit = meteringUnit;
	}
	public String getQuantity() {
		return quantity;
	}
	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}
	public String getUnitPrice() {
		return unitPrice;
	}
	public void setUnitPrice(String unitPrice) {
		this.unitPrice = unitPrice;
	}
	public String getItemAmount() {
		return itemAmount;
	}
	public void setItemAmount(String itemAmount) {
		this.itemAmount = itemAmount;
	}

	public String getSelfCode() {
		return selfCode;
	}
	public void setSelfCode(String selfCode) {
		this.selfCode = selfCode;
	}
	public String getTaxIncludeFlag() {
		return taxIncludeFlag;
	}
	public void setTaxIncludeFlag(String taxIncludeFlag) {
		this.taxIncludeFlag = taxIncludeFlag;
	}
	public String getTaxRateValue() {
		return taxRateValue;
	}
	public void setTaxRateValue(String taxRateValue) {
		this.taxRateValue = taxRateValue;
	}
	public String getTaxRateAmount() {
		return taxRateAmount;
	}
	public void setTaxRateAmount(String taxRateAmount) {
		this.taxRateAmount = taxRateAmount;
	}
	public String getDeduction() {
		return deduction;
	}
	public void setDeduction(String deduction) {
		this.deduction = deduction;
	}
	public String getPreferentialPolicyFlag() {
		return preferentialPolicyFlag;
	}
	public void setPreferentialPolicyFlag(String preferentialPolicyFlag) {
		this.preferentialPolicyFlag = preferentialPolicyFlag;
	}
	public String getZeroTaxFlag() {
		return zeroTaxFlag;
	}
	public void setZeroTaxFlag(String zeroTaxFlag) {
		this.zeroTaxFlag = zeroTaxFlag;
	}
	public String getVatSpecialManage() {
		return vatSpecialManage;
	}
	public void setVatSpecialManage(String vatSpecialManage) {
		this.vatSpecialManage = vatSpecialManage;
	}
	public String getItemProperty() {
		return itemProperty;
	}
	public void setItemProperty(String itemProperty) {
		this.itemProperty = itemProperty;
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