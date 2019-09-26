package com.dzf.zxkj.platform.model.image;

import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 图片流转流程配置明细表
 * @author wangzhn
 *
 */
public class PictureFlowBVO extends SuperVO {
	
	@JsonProperty("ppfbpk")
	private String pk_pictureflow_b;//图片流程子表主键
	@JsonProperty("ppfpk")
	private String pk_pictureflow;//图片流程主表主键
	@JsonProperty("ppfbno")
	private Integer picsubflowno;//图片流程子表编码
	@JsonProperty("corpid")
	private String pk_corp;//公司
	
	private String flowclass;//流程class(含包名)
	private String flowmethod;//流程方法
	private String flowservice;//流程接口名
	
	private Integer dr;
	private DZFDateTime ts;

	public String getPk_pictureflow_b() {
		return pk_pictureflow_b;
	}

	public void setPk_pictureflow_b(String pk_pictureflow_b) {
		this.pk_pictureflow_b = pk_pictureflow_b;
	}

	public String getPk_pictureflow() {
		return pk_pictureflow;
	}

	public void setPk_pictureflow(String pk_pictureflow) {
		this.pk_pictureflow = pk_pictureflow;
	}

	public Integer getPicsubflowno() {
		return picsubflowno;
	}

	public void setPicsubflowno(Integer picsubflowno) {
		this.picsubflowno = picsubflowno;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getFlowclass() {
		return flowclass;
	}

	public void setFlowclass(String flowclass) {
		this.flowclass = flowclass;
	}

	public String getFlowmethod() {
		return flowmethod;
	}

	public void setFlowmethod(String flowmethod) {
		this.flowmethod = flowmethod;
	}

	public String getFlowservice() {
		return flowservice;
	}

	public void setFlowservice(String flowservice) {
		this.flowservice = flowservice;
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
		return "pk_pictureflow_b";
	}

	@Override
	public String getParentPKFieldName() {
		return "pk_pictureflow";
	}

	@Override
	public String getTableName() {
		return "ynt_pictureflow_b";
	}

}
