package com.dzf.zxkj.app.model.sys;

import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.model.SuperVO;
import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("rawtypes")
public class AppFiletransHVO extends SuperVO {

	private static final long serialVersionUID = -8532250094282912760L;
	
	@JsonProperty("ista")
	private Integer istatus;//消息状态： 1、待确认；2、已借出；3、已收到；
	
	@JsonProperty("istnm")
	private String istaname;//消息状态状态名称
	
	@JsonProperty("vsuernm")
	private String vsuername;//交出人名称
	
	@JsonProperty("vcaernm")
	private String vcaername;//接手人名称
	
	@JsonProperty("dttime")
	private DZFDateTime dtranstime;//时间
	
	@JsonProperty("sdate")
	private String vshowdate;//显示日期
	
	@JsonProperty("btnm")
	private String vbtnname;//按钮名称
	
	@JsonProperty("id")
	private String pk_zj;//主键

	private AppFiletransBVO[] files;
	
	public String getVshowdate() {
		return vshowdate;
	}

	public void setVshowdate(String vshowdate) {
		this.vshowdate = vshowdate;
	}

	public String getPk_zj() {
		return pk_zj;
	}

	public void setPk_zj(String pk_zj) {
		this.pk_zj = pk_zj;
	}

	public String getVbtnname() {
		return vbtnname;
	}

	public void setVbtnname(String vbtnname) {
		this.vbtnname = vbtnname;
	}

	public String getVcaername() {
		return vcaername;
	}

	public void setVcaername(String vcaername) {
		this.vcaername = vcaername;
	}

	public String getVsuername() {
		return vsuername;
	}

	public void setVsuername(String vsuername) {
		this.vsuername = vsuername;
	}

	public DZFDateTime getDtranstime() {
		return dtranstime;
	}

	public void setDtranstime(DZFDateTime dtranstime) {
		this.dtranstime = dtranstime;
	}

	public Integer getIstatus() {
		return istatus;
	}

	public void setIstatus(Integer istatus) {
		this.istatus = istatus;
	}

	public String getIstaname() {
		return istaname;
	}

	public void setIstaname(String istaname) {
		this.istaname = istaname;
	}

	public AppFiletransBVO[] getFiles() {
		return files;
	}

	public void setFiles(AppFiletransBVO[] files) {
		this.files = files;
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
