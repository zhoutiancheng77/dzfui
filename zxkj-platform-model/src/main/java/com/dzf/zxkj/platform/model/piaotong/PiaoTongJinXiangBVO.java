package com.dzf.zxkj.platform.model.piaotong;

import com.dzf.zxkj.common.model.SuperVO;

/**
 * 票通进项采集数据 子表
 * @author wangzhn
 *
 */
public class PiaoTongJinXiangBVO extends SuperVO {
	
	private String name;//货物或应税劳务名称
	
	private String specification;//规格型号
	
	private String unit;//单位
	
	private String quantity;//数量
	
	private String unitPrice;//单价
	
	private String money;//金额
	
	private String taxRate;//税率
	
	private String taxAmount;//税额
	
	public String getName() {
		return name;
	}

	public String getSpecification() {
		return specification;
	}

	public String getUnit() {
		return unit;
	}

	public String getQuantity() {
		return quantity;
	}

	public String getUnitPrice() {
		return unitPrice;
	}

	public String getMoney() {
		return money;
	}

	public String getTaxRate() {
		return taxRate;
	}

	public String getTaxAmount() {
		return taxAmount;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSpecification(String specification) {
		this.specification = specification;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public void setUnitPrice(String unitPrice) {
		this.unitPrice = unitPrice;
	}

	public void setMoney(String money) {
		this.money = money;
	}

	public void setTaxRate(String taxRate) {
		this.taxRate = taxRate;
	}

	public void setTaxAmount(String taxAmount) {
		this.taxAmount = taxAmount;
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
