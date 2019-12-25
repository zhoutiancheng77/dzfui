package com.dzf.zxkj.platform.model.sys;


import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.model.SuperVO;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 用户看过   更新日志   记录表
 */
public class UserVersionVO extends SuperVO {
	private String pk_userversion;
	@JsonProperty("verid")
	private String versionid;//版本号id;
	@JsonProperty("cuserid")
	private String pk_user;//用户id;
	private String vdef1;
	private String vdef2;
	private String vdef3;
	private String pk_corp;
	private DZFDateTime ts;//
	private Integer dr;//
	public DZFDateTime getTs() {
		return ts;
	}
	public void setTs(DZFDateTime ts) {
		this.ts = ts;
	}
	public Integer getDr() {
		return dr;
	}
	public void setDr(Integer dr) {
		this.dr = dr;
	}
	public String getVersionid() {
		return versionid;
	}
	public void setVersionid(String versionid) {
		this.versionid = versionid;
	}
	public String getPk_user() {
		return pk_user;
	}
	public void setPk_user(String pk_user) {
		this.pk_user = pk_user;
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
	public String getPk_userversion() {
		return pk_userversion;
	}
	public void setPk_userversion(String pk_userversion) {
		this.pk_userversion = pk_userversion;
	}
	public String getPk_corp() {
		return pk_corp;
	}
	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}
	@Override
	public String getPKFieldName() {
		return "pk_userversion";
	}
	@Override
	public String getParentPKFieldName() {
		return null;
	}
	@Override
	public String getTableName() {
		return "ynt_upversion_read";
	}
}