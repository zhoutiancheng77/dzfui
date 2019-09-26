package com.dzf.zxkj.platform.model.st;


import com.dzf.zxkj.base.model.SuperVO;
import com.fasterxml.jackson.annotation.JsonProperty;

public class StProjectVO extends SuperVO {

	private String pk_project;//主键
	@JsonProperty("code")
	private String pj_code;//项目编码
	@JsonProperty("name")
	private String pj_name;//项目名称
	private String pj_stname;//项目简称
	private String vdef1;//自由项
	private String vdef2;
	private String vdef3;
	private String vdef4;
	private String vdef5;
	@JsonProperty("gsid")
	private String pk_corp;//公司主键
	
	
	
	
	public String getPk_project() {
		return pk_project;
	}

	public void setPk_project(String pk_project) {
		this.pk_project = pk_project;
	}

	public String getPj_code() {
		return pj_code;
	}

	public void setPj_code(String pj_code) {
		this.pj_code = pj_code;
	}

	public String getPj_name() {
		return pj_name;
	}

	public void setPj_name(String pj_name) {
		this.pj_name = pj_name;
	}

	public String getPj_stname() {
		return pj_stname;
	}

	public void setPj_stname(String pj_stname) {
		this.pj_stname = pj_stname;
	}

	public String getVdef1() {
		return vdef1;
	}

	public void setVdef1(String vdef1) {
		this.vdef1 = vdef1;
	}

	public String getVdef2() {
		return vdef2;
	}

	public void setVdef2(String vdef2) {
		this.vdef2 = vdef2;
	}

	public String getVdef3() {
		return vdef3;
	}

	public void setVdef3(String vdef3) {
		this.vdef3 = vdef3;
	}

	public String getVdef4() {
		return vdef4;
	}

	public void setVdef4(String vdef4) {
		this.vdef4 = vdef4;
	}

	public String getVdef5() {
		return vdef5;
	}

	public void setVdef5(String vdef5) {
		this.vdef5 = vdef5;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	@Override
	public String getParentPKFieldName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPKFieldName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return null;
	}

}
