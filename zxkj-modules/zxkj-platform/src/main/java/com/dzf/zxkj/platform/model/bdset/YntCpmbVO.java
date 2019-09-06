package com.dzf.zxkj.platform.model.bdset;

import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Entity;

/**
 * 计提折旧 清理
 * @author Administrator
 *
 */
@Entity
public class YntCpmbVO extends SuperVO<YntCpmbBVO> {

	@JsonProperty("mainid")
	private String pk_corp_assettemplate;   //主表主键
	@JsonProperty("cpid")
	private String pk_corp;
	@JsonProperty("zcsx")
	private Integer assetproperty;       //资产属性
	@JsonProperty("zclbid")
	private String pk_assetcategory;     //资产类别
	@JsonProperty("userid")
	private String coperatorid;
	@JsonProperty("opdate")
	private String doperatedate;
	@JsonProperty("momo")
	private String memo;
	//
	private Integer tempkind;
	private DZFDateTime ts;
	private Integer dr;
	
	//资产类别 显示名称
	@JsonProperty("zclb")
	private String zclbname;//资产类别
	@JsonProperty("usermc")
	private String username;
	
	public String getPk_corp_assettemplate() {
		return pk_corp_assettemplate;
	}
	public void setPk_corp_assettemplate(String pk_corp_assettemplate) {
		this.pk_corp_assettemplate = pk_corp_assettemplate;
	}
	public String getCoperatorid() {
		return coperatorid;
	}
	public void setCoperatorid(String coperatorid) {
		this.coperatorid = coperatorid;
	}
	public String getDoperatedate() {
		return doperatedate;
	}
	public void setDoperatedate(String doperatedate) {
		this.doperatedate = doperatedate;
	}
	public DZFDateTime getTs() {
		return ts;
	}
	public void setTs(DZFDateTime ts) {
		this.ts = ts;
	}
	public Integer getDr() {
		return dr;
	}
	public void setDr(Integer dr) {
		this.dr = dr;
	}
	public String getPk_corp() {
		return pk_corp;
	}
	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}
	public Integer getAssetproperty() {
		return assetproperty;
	}
	public void setAssetproperty(Integer assetproperty) {
		this.assetproperty = assetproperty;
	}
	public String getPk_assetcategory() {
		return pk_assetcategory;
	}
	public void setPk_assetcategory(String pk_assetcategory) {
		this.pk_assetcategory = pk_assetcategory;
	}
	public Integer getTempkind() {
		return tempkind;
	}
	public void setTempkind(Integer tempkind) {
		this.tempkind = tempkind;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public String getZclbname() {
		return zclbname;
	}
	public void setZclbname(String zclbname) {
		this.zclbname = zclbname;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	@Override
	public String getParentPKFieldName() {
		return null;
	}
	@Override
	public String getPKFieldName() {
		return "pk_corp_assettemplate";
	}
	@Override
	public String getTableName() {
		return "ynt_cpmb";
	}
}