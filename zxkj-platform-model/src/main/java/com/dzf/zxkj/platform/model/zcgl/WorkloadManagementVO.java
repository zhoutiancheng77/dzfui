package com.dzf.zxkj.platform.model.zcgl;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.fasterxml.jackson.annotation.JsonProperty;

public class WorkloadManagementVO extends SuperVO {
	
	private String pk_workloadmanagement;//工作量主键
	private String pk_assetcard;//卡片主键
	private String pk_corp;
	private String doperatedate;//期间
	private String accountdate;//卡片使用日期
	private DZFDouble bygzl;//本月工作量
	private DZFDouble syljgzl;//上月累计工作量
	private DZFDouble gzzl;//工作总量
	private String gzldw;//工作量单位
	private DZFDouble ljgzl;//累计工作量
	private DZFDouble sygzl; //剩余工作量
	private DZFBoolean isjtzj;//是否计提折旧
	private DZFBoolean isperiodbegin;//是否期初卡片
	@JsonProperty("asname")
	private String assetname;//资产名称
	@JsonProperty("zcbm")
	private String zccode;//资产编码
	@JsonProperty("ascode")
	private String assetcode;//卡片编号
	private Integer dr;
	private DZFDateTime ts;
	private String zjdate;
	
	
	
	
	

	
	public String getZjdate() {
		return zjdate;
	}
	public void setZjdate(String zjdate) {
		this.zjdate = zjdate;
	}
	public DZFBoolean getIsperiodbegin() {
		return isperiodbegin;
	}
	public void setIsperiodbegin(DZFBoolean isperiodbegin) {
		this.isperiodbegin = isperiodbegin;
	}
	public String getAccountdate() {
		return accountdate;
	}
	public void setAccountdate(String accountdate) {
		this.accountdate = accountdate;
	}
	public String getPk_workloadmanagement() {
		return pk_workloadmanagement;
	}
	public void setPk_workloadmanagement(String pk_workloadmanagement) {
		this.pk_workloadmanagement = pk_workloadmanagement;
	}
	public String getPk_assetcard() {
		return pk_assetcard;
	}
	public void setPk_assetcard(String pk_assetcard) {
		this.pk_assetcard = pk_assetcard;
	}
	
	public String getPk_corp() {
		return pk_corp;
	}
	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}
	public String getDoperatedate() {
		return doperatedate;
	}
	public void setDoperatedate(String doperatedate) {
		this.doperatedate = doperatedate;
	}
	
	public DZFBoolean getIsjtzj() {
		return isjtzj;
	}
	public void setIsjtzj(DZFBoolean isjtzj) {
		this.isjtzj = isjtzj;
	}
	public DZFDouble getBygzl() {
		return bygzl;
	}
	public void setBygzl(DZFDouble bygzl) {
		this.bygzl = bygzl;
	}
	public DZFDouble getSyljgzl() {
		return syljgzl;
	}
	public void setSyljgzl(DZFDouble syljgzl) {
		this.syljgzl = syljgzl;
	}
	public DZFDouble getGzzl() {
		return gzzl;
	}
	public void setGzzl(DZFDouble gzzl) {
		this.gzzl = gzzl;
	}
	public String getGzldw() {
		return gzldw;
	}
	public void setGzldw(String gzldw) {
		this.gzldw = gzldw;
	}
	public DZFDouble getLjgzl() {
		return ljgzl;
	}
	public void setLjgzl(DZFDouble ljgzl) {
		this.ljgzl = ljgzl;
	}
	public DZFDouble getSygzl() {
		return sygzl;
	}
	public void setSygzl(DZFDouble sygzl) {
		this.sygzl = sygzl;
	}
	public String getAssetname() {
		return assetname;
	}
	public void setAssetname(String assetname) {
		this.assetname = assetname;
	}
	public String getZccode() {
		return zccode;
	}
	public void setZccode(String zccode) {
		this.zccode = zccode;
	}
	public String getAssetcode() {
		return assetcode;
	}
	public void setAssetcode(String assetcode) {
		this.assetcode = assetcode;
	}
	public Integer getDr() {
		return dr;
	}
	public void setDr(Integer dr) {
		this.dr = dr;
	}
	public DZFDateTime getTs() {
		return ts;
	}
	public void setTs(DZFDateTime ts) {
		this.ts = ts;
	}
	@Override
	public String getPKFieldName() {
		// TODO Auto-generated method stub
		return "pk_workloadmanagement";
	}
	@Override
	public String getParentPKFieldName() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return "ynt_workloadManagement";
	}
	
}
