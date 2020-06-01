package com.dzf.zxkj.app.model.app.user;


import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.model.SuperVO;

/**
 * 用户注册表
 * 
 * @author zhangj
 *
 */
public class TempUserRegVO extends SuperVO {
	
	public static final String PK_TEMP_USER="pk_temp_user";
	public static final String TABLENAME = "app_temp_user";

	private String pk_temp_user;//
	private Integer dr;//
	private DZFBoolean locked_tag;//
	private String user_code;//
	private String user_name;//
	private String user_note;//
	private String user_password;//
	private Integer istate;//
	private String pk_user;// 生成正式的用户pk
	private DZFDateTime ts;//
	private String job;//
	private String app_corpadd;//
	private String app_user_tel;//
	private String app_user_qq;//
	private String app_user_mail;//
	private String app_user_memo;//
	private String phone;//
	private String user_mail;//
	private String user_qq;//
	private String pk_svorg;//服务机构
	private DZFBoolean bbwdefaultpwd;//百望是否默认密码
	
	public DZFBoolean getBbwdefaultpwd() {
		return bbwdefaultpwd;
	}
	public void setBbwdefaultpwd(DZFBoolean bbwdefaultpwd) {
		this.bbwdefaultpwd = bbwdefaultpwd;
	}
	public String getPk_svorg() {
		return pk_svorg;
	}
	public void setPk_svorg(String pk_svorg) {
		this.pk_svorg = pk_svorg;
	}
	public String getPk_temp_user() {
		return pk_temp_user;
	}
	public void setPk_temp_user(String pk_temp_user) {
		this.pk_temp_user = pk_temp_user;
	}
	public Integer getDr() {
		return dr;
	}
	public void setDr(Integer dr) {
		this.dr = dr;
	}
	public DZFBoolean getLocked_tag() {
		return locked_tag;
	}
	public void setLocked_tag(DZFBoolean locked_tag) {
		this.locked_tag = locked_tag;
	}
	public String getUser_code() {
		return user_code;
	}
	public void setUser_code(String user_code) {
		this.user_code = user_code;
	}
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public String getUser_note() {
		return user_note;
	}
	public void setUser_note(String user_note) {
		this.user_note = user_note;
	}
	public String getUser_password() {
		return user_password;
	}
	public void setUser_password(String user_password) {
		this.user_password = user_password;
	}
	
	public Integer getIstate() {
		return istate;
	}
	public void setIstate(Integer istate) {
		this.istate = istate;
	}
	public String getPk_user() {
		return pk_user;
	}
	public void setPk_user(String pk_user) {
		this.pk_user = pk_user;
	}
	public DZFDateTime getTs() {
		return ts;
	}
	public void setTs(DZFDateTime ts) {
		this.ts = ts;
	}
	public String getJob() {
		return job;
	}
	public void setJob(String job) {
		this.job = job;
	}
	public String getApp_corpadd() {
		return app_corpadd;
	}
	public void setApp_corpadd(String app_corpadd) {
		this.app_corpadd = app_corpadd;
	}
	public String getApp_user_tel() {
		return app_user_tel;
	}
	public void setApp_user_tel(String app_user_tel) {
		this.app_user_tel = app_user_tel;
	}
	public String getApp_user_qq() {
		return app_user_qq;
	}
	public void setApp_user_qq(String app_user_qq) {
		this.app_user_qq = app_user_qq;
	}
	public String getApp_user_mail() {
		return app_user_mail;
	}
	public void setApp_user_mail(String app_user_mail) {
		this.app_user_mail = app_user_mail;
	}
	public String getApp_user_memo() {
		return app_user_memo;
	}
	public void setApp_user_memo(String app_user_memo) {
		this.app_user_memo = app_user_memo;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getUser_mail() {
		return user_mail;
	}
	public void setUser_mail(String user_mail) {
		this.user_mail = user_mail;
	}
	public String getUser_qq() {
		return user_qq;
	}
	public void setUser_qq(String user_qq) {
		this.user_qq = user_qq;
	}
	@Override
	public String getPKFieldName() {
		return PK_TEMP_USER;
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
