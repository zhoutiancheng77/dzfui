package com.dzf.zxkj.app.model.resp.bean;


import com.dzf.zxkj.common.lang.DZFDouble;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RegisterRespBeanVO extends ResponseBaseBeanVO {

	private String exitcorp;
	private String corpid;
	private String cuserid;
	private String cusercode;
	private String ismanage;
	@JsonProperty("lode")
	private DZFDouble longitude;
	@JsonProperty("lade")
	private DZFDouble latitude;
	private String isdemo;
	private ContainCorpVo[] cpvos;
	
	public ContainCorpVo[] getCpvos() {
		return cpvos;
	}

	public void setCpvos(ContainCorpVo[] cpvos) {
		this.cpvos = cpvos;
	}

	public String getIsdemo() {
		return isdemo;
	}

	public void setIsdemo(String isdemo) {
		this.isdemo = isdemo;
	}

	public DZFDouble getLongitude() {
		return longitude;
	}

	public void setLongitude(DZFDouble longitude) {
		this.longitude = longitude;
	}

	public DZFDouble getLatitude() {
		return latitude;
	}

	public void setLatitude(DZFDouble latitude) {
		this.latitude = latitude;
	}

	public String getIsmanage() {
		return ismanage;
	}

	public void setIsmanage(String ismanage) {
		this.ismanage = ismanage;
	}

	public String getCuserid() {
		return cuserid;
	}

	public void setCuserid(String cuserid) {
		this.cuserid = cuserid;
	}

	public String getCusercode() {
		return cusercode;
	}

	public void setCusercode(String cusercode) {
		this.cusercode = cusercode;
	}

	public String getCorpid() {
		return corpid;
	}

	public void setCorpid(String corpid) {
		this.corpid = corpid;
	}

	public String getExitcorp() {
		return exitcorp;
	}

	public void setExitcorp(String exitcorp) {
		this.exitcorp = exitcorp;
	}
	
	
}
