package com.dzf.zxkj.common.base;


import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.model.SuperVO;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 日志记录vo
 * 
 * @author zhangj
 *
 */
public class LogRecordVo extends SuperVO {

	public static final String TABLENAME = "ynt_logrecord";

	public static final String PKFIELDNAME = "pk_logrecord";

	@JsonProperty("id")
	private String pk_logrecord;// 主键
	@JsonProperty("odate")
	private String doperatedate;// 操作日期
	private String opestr;//打印使用
	@JsonProperty("user")
	private String vuser;// 操作用户
	@JsonProperty("ip")
	private String vuserip;// 用户ip
	@JsonProperty("otype")
	private Integer iopetype;// 操作类型
	private String opetypestr;//操作类型对应
	@JsonProperty("omsg")
	private String vopemsg;// 操作明细
	private DZFDateTime ts;// ts
	private Integer dr;// dr
	@JsonProperty("cid")
	private String pk_corp;// 公司
	private String cuserid;//用户id
	private Integer iversion;//  版本   空或者0 旧版   1----新版

	private String sys_version = "1.0.0";

	public String getSys_version() {
		return sys_version;
	}

	public void setSys_version(String sys_version) {
		this.sys_version = sys_version;
	}

	private Integer sys_ident;//系统类型0:集团,1:管理端,2会计端,3、加盟商系统(后续的继续添加)
	
	public String getOpetypestr() {
		return opetypestr;
	}

	public void setOpetypestr(String opetypestr) {
		this.opetypestr = opetypestr;
	}

	public String getOpestr() {
		return opestr;
	}

	public void setOpestr(String opestr) {
		this.opestr = opestr;
	}

	public Integer getSys_ident() {
		return sys_ident;
	}

	public void setSys_ident(Integer sys_ident) {
		this.sys_ident = sys_ident;
	}

	public String getPk_logrecord() {
		return pk_logrecord;
	}

	public void setPk_logrecord(String pk_logrecord) {
		this.pk_logrecord = pk_logrecord;
	}

	public String getDoperatedate() {
		return doperatedate;
	}

	public void setDoperatedate(String doperatedate) {
		this.doperatedate = doperatedate;
	}

	public String getVuser() {
		return vuser;
	}

	public void setVuser(String vuser) {
		this.vuser = vuser;
	}

	public String getVuserip() {
		return vuserip;
	}

	public void setVuserip(String vuserip) {
		this.vuserip = vuserip;
	}

	public Integer getIopetype() {
		return iopetype;
	}

	public void setIopetype(Integer iopetype) {
		this.iopetype = iopetype;
	}

	public String getVopemsg() {
		return vopemsg;
	}

	public void setVopemsg(String vopemsg) {
		this.vopemsg = vopemsg;
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

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getCuserid() {
		return cuserid;
	}

	public void setCuserid(String cuserid) {
		this.cuserid = cuserid;
	}

	public Integer getIversion() {
		return iversion;
	}
	public void setIversion(Integer iversion) {
		this.iversion = iversion;
	}
	@Override
	public String getPKFieldName() {
		return PKFIELDNAME;
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
