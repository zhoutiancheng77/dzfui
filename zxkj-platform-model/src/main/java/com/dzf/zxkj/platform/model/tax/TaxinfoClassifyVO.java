package com.dzf.zxkj.platform.model.tax;

import com.dzf.zxkj.common.model.SuperVO;

public class TaxinfoClassifyVO extends SuperVO {
	private TaxinfoClassifyBVO[] data;
	private boolean success;
	private String msg;
	public TaxinfoClassifyBVO[] getData() {
		return data;
	}
	public void setData(TaxinfoClassifyBVO[] data) {
		this.data = data;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
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