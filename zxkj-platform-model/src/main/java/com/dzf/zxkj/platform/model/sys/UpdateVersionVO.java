package com.dzf.zxkj.platform.model.sys;


import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.model.SuperVO;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 在线会计 更新表，可以通用
 *
 */
public class UpdateVersionVO extends SuperVO {
	
	@JsonProperty("verno")
	private String versionno;//版本号
	private DZFDate versiondate;//版本日期
	private String module;//模块
	private DZFDateTime ts;//
	private Integer dr;//
	@JsonProperty("verid")
	private String versionid;//版本号id;
	@JsonProperty("verts")
	private Integer item;//主要更新了几条记录
	@JsonProperty("isread")
	private DZFBoolean isread;//不存库--------------------这个不存库
	private String pk_userversion;//不存库----------------这个不存库
	private DZFBoolean islast;//是否最新版本
	private String vdef1;
	private String vdef2;
	private String vdef3;
	private String vdef4;
	private String vdef5;
	private String pk_corp;
	public String getVersionno() {
		return versionno;
	}
	public void setVersionno(String versionno) {
		this.versionno = versionno;
	}
	public DZFDate getVersiondate() {
		return versiondate;
	}
	public void setVersiondate(DZFDate versiondate) {
		this.versiondate = versiondate;
	}
	public String getModule() {
		return module;
	}
	public void setModule(String module) {
		this.module = module;
	}
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
	public Integer getItem() {
		return item;
	}
	public void setItem(Integer item) {
		this.item = item;
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
	public DZFBoolean getIsread() {
		return isread;
	}
	public void setIsread(DZFBoolean isread) {
		this.isread = isread;
	}
	public DZFBoolean getIslast() {
		return islast;
	}
	public void setIslast(DZFBoolean islast) {
		this.islast = islast;
	}
	public String getPk_corp() {
		return pk_corp;
	}
	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}
	public String getPk_userversion() {
		return pk_userversion;
	}
	public void setPk_userversion(String pk_userversion) {
		this.pk_userversion = pk_userversion;
	}
	@Override
	public String getPKFieldName() {
		return "versionid";
	}
	@Override
	public String getParentPKFieldName() {
		return null;
	}
	@Override
	public String getTableName() {
		return "ynt_upversion";
	}
}