package com.dzf.zxkj.platform.model.pzgl;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDate;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * 报销单查询条件VO
 */
public class ExpBillParamVO extends SuperVO {

	public ExpBillParamVO() {
	}

	private String pk_corp;// 公司
	@JsonProperty("id")
	private String pk_expbill_h;
	private String bxr;//报销人
	private DZFDate bxbdate;//报销日期
	private DZFDate bxedate;
	private DZFDate scbdate;//上传日期
	private DZFDate scedate;
	private DZFDate zfbdate;//支付日期
	private DZFDate zfedate;
	@JsonProperty("zt")
	private Integer bxdStatus;
	private String zy;
	
	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getPk_expbill_h() {
		return pk_expbill_h;
	}

	public void setPk_expbill_h(String pk_expbill_h) {
		this.pk_expbill_h = pk_expbill_h;
	}

	public String getBxr() {
		return bxr;
	}

	public void setBxr(String bxr) {
		this.bxr = bxr;
	}

	public DZFDate getBxbdate() {
		return bxbdate;
	}

	public void setBxbdate(DZFDate bxbdate) {
		this.bxbdate = bxbdate;
	}

	public DZFDate getBxedate() {
		return bxedate;
	}

	public void setBxedate(DZFDate bxedate) {
		this.bxedate = bxedate;
	}

	public DZFDate getScbdate() {
		return scbdate;
	}

	public void setScbdate(DZFDate scbdate) {
		this.scbdate = scbdate;
	}

	public DZFDate getScedate() {
		return scedate;
	}

	public void setScedate(DZFDate scedate) {
		this.scedate = scedate;
	}

	public DZFDate getZfbdate() {
		return zfbdate;
	}

	public void setZfbdate(DZFDate zfbdate) {
		this.zfbdate = zfbdate;
	}

	public DZFDate getZfedate() {
		return zfedate;
	}

	public void setZfedate(DZFDate zfedate) {
		this.zfedate = zfedate;
	}

	public Integer getBxdStatus() {
		return bxdStatus;
	}

	public void setBxdStatus(Integer bxdStatus) {
		this.bxdStatus = bxdStatus;
	}

	public String getZy() {
		return zy;
	}

	public void setZy(String zy) {
		this.zy = zy;
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return null;
	}
	
}
