package com.dzf.zxkj.common.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * EasyUI DataGrid模型  禁用
 * 
 * @author 孙宇
 * 
 */
public class Grid implements java.io.Serializable {

	private Long total = 0L;
	private List rows = new ArrayList();
	private boolean success;
	private String msg;

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

	public List getRows() {
		return rows;
	}

	public void setRows(List rows) {
		this.rows = rows;
	}

	public boolean getSuccess() {
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
	
	

}
