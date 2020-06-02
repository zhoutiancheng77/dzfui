package com.dzf.zxkj.app.model.app;


import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.model.SuperVO;

/**
 * 日志VO
 * 
 * @author Administrator
 *
 */
public class LogVO extends SuperVO {

	private String pk_applog;
	private String accout;
	private String accout_id;// 用户信息
	private String optcontent;
	private DZFDateTime opttime;// 操作时间
	private String msg;
	private Integer dr;
	private DZFDateTime ts;

	private String pk_corp;
	private String pk_temp_corp;// 公司信息
	private String versionno;// 版本号
	private String optsys;// 系统来源
	private String optnumber;// 操作符

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getPk_temp_corp() {
		return pk_temp_corp;
	}

	public void setPk_temp_corp(String pk_temp_corp) {
		this.pk_temp_corp = pk_temp_corp;
	}

	public String getVersionno() {
		return versionno;
	}

	public void setVersionno(String versionno) {
		this.versionno = versionno;
	}

	public String getOptsys() {
		return optsys;
	}

	public void setOptsys(String optsys) {
		this.optsys = optsys;
	}

	public String getOptnumber() {
		return optnumber;
	}

	public void setOptnumber(String optnumber) {
		this.optnumber = optnumber;
	}

	public String getPk_applog() {
		return pk_applog;
	}

	public void setPk_applog(String pk_applog) {
		this.pk_applog = pk_applog;
	}

	public String getAccout() {
		return accout;
	}

	public void setAccout(String accout) {
		this.accout = accout;
	}

	public String getAccout_id() {
		return accout_id;
	}

	public void setAccout_id(String accout_id) {
		this.accout_id = accout_id;
	}

	public String getOptcontent() {
		return optcontent;
	}

	public void setOptcontent(String optcontent) {
		this.optcontent = optcontent;
	}

	public DZFDateTime getOpttime() {
		return opttime;
	}

	public void setOpttime(DZFDateTime opttime) {
		this.opttime = opttime;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Integer getDr() {
		return dr;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
	}

	public DZFDateTime getTs() {
		return ts;
	}

	public void setTs(DZFDateTime ts) {
		this.ts = ts;
	}

	@Override
	public String getPKFieldName() {
		return "pk_applog";
	}

	@Override
	public String getTableName() {
		return "app_log";
	}

	@Override
	public String getParentPKFieldName() {
		// TODO Auto-generated method stub
		return null;
	}
}
