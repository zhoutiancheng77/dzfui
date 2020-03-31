package com.dzf.zxkj.platform.model.batchprint;


import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.model.SuperVO;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 进度管理的装订与打印
 *
 */
@SuppressWarnings("rawtypes")
public class BandingVO extends SuperVO {
	
	private static final long serialVersionUID = 1L;
	
	//主键
	@JsonProperty("bandingid")
	private String pk_bill_banding;
	
	//客户主键
	@JsonProperty("corpkid")
	private String pk_corp;
	
	@JsonProperty("period")
	private String period;
	
	//凭证是否打印完成
	@JsonProperty("isvoprint")
	private DZFBoolean isvouchprint;
	
	//报表是否打印完成
	@JsonProperty("isreprint")
	private DZFBoolean isreportprint;
	
	//账簿是否打印完成
	@JsonProperty("iskmprint")
	private DZFBoolean iskmprint;
	
	private String modifyid;
	
	private DZFDate modifydatetime;
	
	@JsonProperty("bstatus")
	private Integer bstatus;//  1:完成;2:未完成;
	
	@JsonProperty("dr")
	private Integer dr; // 删除标记

	@JsonProperty("ts")
	private DZFDateTime ts; // 时间戳
	
	public String getModifyid() {
		return modifyid;
	}

	public void setModifyid(String modifyid) {
		this.modifyid = modifyid;
	}

	public DZFDate getModifydatetime() {
		return modifydatetime;
	}

	public void setModifydatetime(DZFDate modifydatetime) {
		this.modifydatetime = modifydatetime;
	}

	
	public Integer getBstatus() {
		return bstatus;
	}

	public void setBstatus(Integer bstatus) {
		this.bstatus = bstatus;
	}
	
	public String getPk_bill_banding() {
		return pk_bill_banding;
	}

	public void setPk_bill_banding(String pk_bill_banding) {
		this.pk_bill_banding = pk_bill_banding;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public DZFBoolean getIsvouchprint() {
		return isvouchprint;
	}

	public void setIsvouchprint(DZFBoolean isvouchprint) {
		this.isvouchprint = isvouchprint;
	}

	public DZFBoolean getIsreportprint() {
		return isreportprint;
	}

	public void setIsreportprint(DZFBoolean isreportprint) {
		this.isreportprint = isreportprint;
	}

	public DZFBoolean getIskmprint() {
		return iskmprint;
	}

	public void setIskmprint(DZFBoolean iskmprint) {
		this.iskmprint = iskmprint;
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
		return "pk_bill_banding";
	}

	@Override
	public String getParentPKFieldName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return "ynt_bill_banding";
	}
	

}
