package com.dzf.zxkj.platform.model.bdset;


public class YntCpaccountVOClassify  implements java.io.Serializable{
	
	private boolean success = false;
	
	private int status = 200;

	private String msg = "";
	
	private Object rows;//损益

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Object getRows() {
		return rows;
	}

	public void setRows(Object rows) {
		this.rows = rows;
	}

}