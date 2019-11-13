package com.dzf.zxkj.platform.model.qcset;

public class QueryVOClassify implements java.io.Serializable{
	
	private boolean success = false;
	
	private int status = 200;

	private String msg = "";
	
	private Object o0;//资产
	
	private Object o1;//负债

	private Object o2;//共同
	
	private Object o3;//所有者权益
	
	private Object o4;//成本
	
	private Object o5;//损益

	private Object o6;//全部
	
	private Object fzqc;//辅助核算数据

	public Object getO6() {
		return o6;
	}

	public void setO6(Object o6) {
		this.o6 = o6;
	}

	public Object getO0() {
		return o0;
	}

	public void setO0(Object o0) {
		this.o0 = o0;
	}

	public Object getO1() {
		return o1;
	}

	public void setO1(Object o1) {
		this.o1 = o1;
	}

	public Object getO2() {
		return o2;
	}

	public void setO2(Object o2) {
		this.o2 = o2;
	}

	public Object getO3() {
		return o3;
	}

	public void setO3(Object o3) {
		this.o3 = o3;
	}

	public Object getO4() {
		return o4;
	}

	public void setO4(Object o4) {
		this.o4 = o4;
	}

	public Object getO5() {
		return o5;
	}

	public void setO5(Object o5) {
		this.o5 = o5;
	}

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

	public Object getFzqc() {
		return fzqc;
	}

	public void setFzqc(Object fzqc) {
		this.fzqc = fzqc;
	}
	
	

}
