package com.dzf.zxkj.platform.model.zcgl;

import com.dzf.zxkj.common.model.SuperVO;

public class ZjhzbReportVO extends SuperVO {

	//资产类别主键
	private String pk_assetcategory;
	
	//资产属性
	private Integer assetproperty;
	
	//资产编码
	private String catecode;

	//资产类别
	private String catename;
	
	//资产原值
	private String assetmny;
	
	//本期折旧额
	private String originalvalue;
	
	//累计折旧额--卡片
	private String depreciation;
	
	//累加折旧额--明细
	private String depreciationmny;
	
	//资产净值--明细
	private String assetnetmny;
	
	//资产净值--卡片
	private String assetnetvalue;
	
	//类别级次
	private Integer catelevel;
	
	
	public String getDepreciationmny() {
		return depreciationmny;
	}

	public void setDepreciationmny(String depreciationmny) {
		this.depreciationmny = depreciationmny;
	}

	public String getAssetnetmny() {
		return assetnetmny;
	}

	public void setAssetnetmny(String assetnetmny) {
		this.assetnetmny = assetnetmny;
	}

	public Integer getCatelevel() {
		return catelevel;
	}

	public void setCatelevel(Integer catelevel) {
		this.catelevel = catelevel;
	}

	public String getPk_assetcategory() {
		return pk_assetcategory;
	}

	public void setPk_assetcategory(String pk_assetcategory) {
		this.pk_assetcategory = pk_assetcategory;
	}

	public String getCatecode() {
		return catecode;
	}

	public void setCatecode(String catecode) {
		this.catecode = catecode;
	}
	
	public Integer getAssetproperty() {
		return assetproperty;
	}

	public void setAssetproperty(Integer assetproperty) {
		this.assetproperty = assetproperty;
	}

	public String getCatename() {
		return catename;
	}

	public void setCatename(String catename) {
		this.catename = catename;
	}

	public String getAssetmny() {
		return assetmny;
	}

	public void setAssetmny(String assetmny) {
		this.assetmny = assetmny;
	}

	public String getOriginalvalue() {
		return originalvalue;
	}

	public void setOriginalvalue(String originalvalue) {
		this.originalvalue = originalvalue;
	}

	@Override
	public String getPKFieldName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getParentPKFieldName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getDepreciation() {
		return depreciation;
	}

	public void setDepreciation(String depreciation) {
		this.depreciation = depreciation;
	}

	public String getAssetnetvalue() {
		return assetnetvalue;
	}

	public void setAssetnetvalue(String assetnetvalue) {
		this.assetnetvalue = assetnetvalue;
	}

	
}
