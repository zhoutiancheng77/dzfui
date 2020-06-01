package com.dzf.zxkj.app.model.log;


import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.model.SuperVO;

/**
 * APP������־VO
 * @author Administrator
 *
 */
public class AppOptLogVO extends SuperVO {

	private String pk_applog;
	private String accout;
	private String accout_id;
	private String optcontent;
	private String opttime;
	private String msg;
	private Integer dr;
	private DZFDateTime ts;
	
	
	
	
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
	public String getOpttime() {
		return opttime;
	}
	public void setOpttime(String opttime) {
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
		// TODO Auto-generated method stub
		return "pk_applog";
	}
	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return "app_log";
	}
	@Override
	public String getParentPKFieldName() {
		// TODO Auto-generated method stub
		return null;
	}
}
