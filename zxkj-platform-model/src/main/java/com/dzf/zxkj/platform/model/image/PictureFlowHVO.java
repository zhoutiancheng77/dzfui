package com.dzf.zxkj.platform.model.image;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 图片流转流程配置主表
 * @author wangzhn
 *
 */
public class PictureFlowHVO extends SuperVO {

	@JsonProperty("ppfpk")
	private String pk_pictureflow;//图片流程主表主键
	@JsonProperty("ppfid")
	private String picflowid;//图片流程主表编号
	private String coperatorid;//操作人
	@JsonProperty("ddate")
	private DZFDate doperatedate;//操作时间
	@JsonProperty("corpid")
	private String pk_corp;//公司
	private String memo;//备注
	private Integer dr;
	private DZFDateTime ts;//时间戳
	
	public String getPk_pictureflow() {
		return pk_pictureflow;
	}

	public void setPk_pictureflow(String pk_pictureflow) {
		this.pk_pictureflow = pk_pictureflow;
	}

	public String getPicflowid() {
		return picflowid;
	}

	public void setPicflowid(String picflowid) {
		this.picflowid = picflowid;
	}

	public String getCoperatorid() {
		return coperatorid;
	}

	public void setCoperatorid(String coperatorid) {
		this.coperatorid = coperatorid;
	}

	public DZFDate getDoperatedate() {
		return doperatedate;
	}

	public void setDoperatedate(DZFDate doperatedate) {
		this.doperatedate = doperatedate;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
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
		return "pk_pictureflow";
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return "ynt_pictureflow";
	}

}
