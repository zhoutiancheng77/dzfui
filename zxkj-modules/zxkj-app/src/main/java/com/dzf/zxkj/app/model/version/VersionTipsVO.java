package com.dzf.zxkj.app.model.version;


import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.model.SuperVO;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 系统版本提示
 * 
 * @author zhangj
 *
 */
public class VersionTipsVO extends SuperVO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	public static final String TABLENAME = "sys_version_tips";
	public static final String TABLEPK = "pk_version_tips";

	@JsonProperty("id")
	private String pk_version_tips;//
	@JsonProperty("vno")
	private String vversiono;// 版本号
	@JsonProperty("pro")
	private Integer nproject;// 项目
	@JsonProperty("tips")
	private String vsystips;// 版本信息
	@JsonProperty("memo")
	private String vmemo;// 备注
	@JsonProperty("corpid")
	private String pk_corp;//
	@JsonProperty("date")
	private DZFDate vdate;//版本日期
	@JsonProperty("ope")
	private String vope;//发布人
	
	private String introduction;//版本功能介绍
	
	private String volume;//包大小
	
	public DZFDate getVdate() {
		return vdate;
	}

	public void setVdate(DZFDate vdate) {
		this.vdate = vdate;
	}

	public String getVope() {
		return vope;
	}

	public void setVope(String vope) {
		this.vope = vope;
	}

	private DZFDateTime ts;
	private Integer dr;
	
	public static final String PK_VERSION_TIPS="pk_version_tips";
	public static final String VVERSIONO="vversiono";
	public static final String NPROJECT="nproject";
	public static final String VSYSTIPS="vsystips";
	public static final String VMEMO="vmemo";
	public static final String PK_CORP="pk_corp";
	public static final String TS="ts";
	public static final String DR="dr";

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

	public String getPk_version_tips() {
		return pk_version_tips;
	}

	public void setPk_version_tips(String pk_version_tips) {
		this.pk_version_tips = pk_version_tips;
	}

	public String getVversiono() {
		return vversiono;
	}

	public void setVversiono(String vversiono) {
		this.vversiono = vversiono;
	}

	public Integer getNproject() {
		return nproject;
	}

	public void setNproject(Integer nproject) {
		this.nproject = nproject;
	}

	public String getVsystips() {
		return vsystips;
	}

	public void setVsystips(String vsystips) {
		this.vsystips = vsystips;
	}

	public String getVmemo() {
		return vmemo;
	}

	public void setVmemo(String vmemo) {
		this.vmemo = vmemo;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	
	public String getIntroduction() {
		return introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

	public String getVolume() {
		return volume;
	}

	public void setVolume(String volume) {
		this.volume = volume;
	}

	@Override
	public String getPKFieldName() {
		return TABLEPK;
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return TABLENAME;
	}

}
