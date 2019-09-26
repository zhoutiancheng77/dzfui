package com.dzf.zxkj.platform.model.st;


import com.dzf.zxkj.base.model.SuperVO;

public abstract class StBaseVO extends SuperVO {

  private String vno;//varchar(10) DEFAULT NULL,
  private String vprojectname;//varchar(100) DEFAULT NULL,
  private String vprojectcode;
  private String pk_project;
  private String cyear;
  private String pk_corp;
  private String period;
	  
	  
	public String getCyear() {
		return cyear;
	}
	public void setCyear(String cyear) {
		this.cyear = cyear;
	}
	public String getPk_corp() {
		return pk_corp;
	}
	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}
	public String getVno() {
		return vno;
	}
	public void setVno(String vno) {
		this.vno = vno;
	}
	public String getVprojectname() {
		return vprojectname;
	}
	public void setVprojectname(String vprojectname) {
		this.vprojectname = vprojectname;
	}
	public String getVprojectcode() {
		return vprojectcode;
	}
	public void setVprojectcode(String vprojectcode) {
		this.vprojectcode = vprojectcode;
	}
	public String getPk_project() {
		return pk_project;
	}
	public void setPk_project(String pk_project) {
		this.pk_project = pk_project;
	}
	  
	public String getPeriod() {
		return period;
	}
	public void setPeriod(String period) {
		this.period = period;
	}
	  
	
}
