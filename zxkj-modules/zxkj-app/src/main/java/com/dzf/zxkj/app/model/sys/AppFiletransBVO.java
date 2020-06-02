package com.dzf.zxkj.app.model.sys;

import com.dzf.zxkj.common.model.SuperVO;
import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("rawtypes")
public class AppFiletransBVO extends SuperVO {

	private static final long serialVersionUID = 3771391366411564072L;
	
	@JsonProperty("corpkna")
	private String corpkname; // 客户公司名称
	
	@JsonProperty("vfname")
	private String vfilename; // 资料名称
	
	@JsonProperty("nums")
	private Integer nfilenums;//资料件数
	
	@JsonProperty("vbperiod")
	private String vbegperiod;//开始期间

	@JsonProperty("veperiod")
	private String vendperiod;//结束期间

	@JsonProperty("memo")
	private String vmemo; // 备注
	
	public String getVbegperiod() {
		return vbegperiod;
	}

	public void setVbegperiod(String vbegperiod) {
		this.vbegperiod = vbegperiod;
	}

	public String getVendperiod() {
		return vendperiod;
	}

	public void setVendperiod(String vendperiod) {
		this.vendperiod = vendperiod;
	}

	public String getVmemo() {
		return vmemo;
	}

	public void setVmemo(String vmemo) {
		this.vmemo = vmemo;
	}

	public String getCorpkname() {
		return corpkname;
	}

	public void setCorpkname(String corpkname) {
		this.corpkname = corpkname;
	}

	public String getVfilename() {
		return vfilename;
	}

	public void setVfilename(String vfilename) {
		this.vfilename = vfilename;
	}

	public Integer getNfilenums() {
		return nfilenums;
	}

	public void setNfilenums(Integer nfilenums) {
		this.nfilenums = nfilenums;
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
