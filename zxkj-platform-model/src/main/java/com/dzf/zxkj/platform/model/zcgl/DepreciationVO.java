package com.dzf.zxkj.platform.model.zcgl;

import com.dzf.zxkj.common.model.SuperVO;

@SuppressWarnings("serial")
public class DepreciationVO extends SuperVO {
	private String pk_assetdepreciation;
	private String coperatorid;
	private String coperatedate;
	private Integer dr;
	private String pk_assetcard;
	private String businessdate;
	private Double originalvalue;
	private String istogl;
	private String pk_voucher;
	private String issettle;
	private String pk_corp;
	private Double assetmny;
	private Double depreciationmny;
	private Double assetnetmny;

	public String getPk_assetdepreciation() {
		return pk_assetdepreciation;
	}

	public void setPk_assetdepreciation(String pk_assetdepreciation) {
		this.pk_assetdepreciation = pk_assetdepreciation;
	}

	public String getCoperatorid() {
		return coperatorid;
	}

	public void setCoperatorid(String coperatorid) {
		this.coperatorid = coperatorid;
	}

	public String getCoperatedate() {
		return coperatedate;
	}

	public void setCoperatedate(String coperatedate) {
		this.coperatedate = coperatedate;
	}

	public Integer getDr() {
		return dr;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
	}

	public String getPk_assetcard() {
		return pk_assetcard;
	}

	public void setPk_assetcard(String pk_assetcard) {
		this.pk_assetcard = pk_assetcard;
	}

	public String getBusinessdate() {
		return businessdate;
	}

	public void setBusinessdate(String businessdate) {
		this.businessdate = businessdate;
	}

	public Double getOriginalvalue() {
		return originalvalue;
	}

	public void setOriginalvalue(Double originalvalue) {
		this.originalvalue = originalvalue;
	}

	public String getIstogl() {
		return istogl;
	}

	public void setIstogl(String istogl) {
		this.istogl = istogl;
	}

	public String getPk_voucher() {
		return pk_voucher;
	}

	public void setPk_voucher(String pk_voucher) {
		this.pk_voucher = pk_voucher;
	}

	public String getIssettle() {
		return issettle;
	}

	public void setIssettle(String issettle) {
		this.issettle = issettle;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public Double getAssetmny() {
		return assetmny;
	}

	public void setAssetmny(Double assetmny) {
		this.assetmny = assetmny;
	}

	public Double getDepreciationmny() {
		return depreciationmny;
	}

	public void setDepreciationmny(Double depreciationmny) {
		this.depreciationmny = depreciationmny;
	}

	public Double getAssetnetmny() {
		return assetnetmny;
	}

	public void setAssetnetmny(Double assetnetmny) {
		this.assetnetmny = assetnetmny;
	}

	@Override
	public String getPKFieldName() {
		// TODO Auto-generated method stub
		return "pk_assetdepreciation";
	}

	@Override
	public String getParentPKFieldName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return "ynt_depreciation";
	}

}
