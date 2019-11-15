package com.dzf.zxkj.platform.model.zncs;

import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;

/**
 * 存货销售信息vo
 * @author yinyx1
 *
 */
public class InventorySaleInfoVO extends SuperVO {
	
    private DZFDouble saleNumber;//销售数量
    
    private DZFDouble salePrice;//销售价格
    
    private String pk_corp;
    
    private String name;//名称
    
    private String spec;//规格
    
    private String unit;//计量单位
	
    private String period;
    
    
	
	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSpec() {
		return spec;
	}

	public void setSpec(String spec) {
		this.spec = spec;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public DZFDouble getSaleNumber() {
		return saleNumber;
	}

	public void setSaleNumber(DZFDouble saleNumber) {
		this.saleNumber = saleNumber;
	}

	public DZFDouble getSalePrice() {
		return salePrice;
	}

	public void setSalePrice(DZFDouble salePrice) {
		this.salePrice = salePrice;
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
