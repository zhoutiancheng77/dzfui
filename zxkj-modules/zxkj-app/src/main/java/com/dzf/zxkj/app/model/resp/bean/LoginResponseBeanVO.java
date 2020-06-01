package com.dzf.zxkj.app.model.resp.bean;


import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.platform.model.sys.CorpDocVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginResponseBeanVO extends ResponseBaseBeanVO {

	private String[] longitude;// 服务机构经纬度
	private String[] latitude;
	private String[] pk_svorg;// 服务机构主键信息
	private String[] adverts;// 代帐公司广告
	private CorpDocVO[] advertvos;//代账公司广告vo
	private String user_name;// 用户名字
	private String lguuid;// 登录的uuid
	private String invite_url;//登录的uuid邀请人员地址
	private DZFDate begdate;// 建账日期
	private String corplongitude;// 公司经纬度
	private String corplatitude;
	private String job;
	private String app_user_mail;
	private String app_user_memo;
	private String app_user_qq;
	private String app_user_tel;
	@JsonProperty("tcorp") // 未签约的公司
	private String pk_temp_corp;// 临时公司pk
	private String photopath;
	private Integer unrcount;// 未读数
	private SubCopMsgBeanVO[] subcorpvos;
	private CorpVO[] cpvos;//当前待关联的公司
	private String ibind;//是否绑定
	private String ibindfwjg;//是否绑定服务机构
	private String wx_3rd_session;//微信标识
	private String ibwdefaultpwd;//百望默认密码
	@JsonProperty("inner_code")
	private String innercode;//公司编码
	
	
	

	public String getInnercode() {
		return innercode;
	}

	public void setInnercode(String innercode) {
		this.innercode = innercode;
	}

	public String getInvite_url() {
		return invite_url;
	}

	public void setInvite_url(String invite_url) {
		this.invite_url = invite_url;
	}

	public String getIbindfwjg() {
		return ibindfwjg;
	}

	public void setIbindfwjg(String ibindfwjg) {
		this.ibindfwjg = ibindfwjg;
	}

	public String getIbwdefaultpwd() {
		return ibwdefaultpwd;
	}

	public void setIbwdefaultpwd(String ibwdefaultpwd) {
		this.ibwdefaultpwd = ibwdefaultpwd;
	}

	public CorpVO[] getCpvos() {
		return cpvos;
	}

	public void setCpvos(CorpVO[] cpvos) {
		this.cpvos = cpvos;
	}

	// --------------------------不使用-----------------
	private String hximaccountid;// 环信账户ID
	private String hximpwd;// 环信账户PWD
	private String hxuuid;// 环信UUID
	private String srvcustid;// 聊天客服ID
	
	

	public String getWx_3rd_session() {
		return wx_3rd_session;
	}

	public void setWx_3rd_session(String wx_3rd_session) {
		this.wx_3rd_session = wx_3rd_session;
	}

	public String getIbind() {
		return ibind;
	}

	public void setIbind(String ibind) {
		this.ibind = ibind;
	}

	public CorpDocVO[] getAdvertvos() {
		return advertvos;
	}

	public void setAdvertvos(CorpDocVO[] advertvos) {
		this.advertvos = advertvos;
	}

	public Integer getUnrcount() {
		return unrcount;
	}

	public void setUnrcount(Integer unrcount) {
		this.unrcount = unrcount;
	}

	public String getLguuid() {
		return lguuid;
	}

	public void setLguuid(String lguuid) {
		this.lguuid = lguuid;
	}

	public String[] getAdverts() {
		return adverts;
	}

	public void setAdverts(String[] adverts) {
		this.adverts = adverts;
	}

	// 密码 无密码 0 密码1
	private String pwdcode;

	public String getPwdcode() {
		return pwdcode;
	}

	public void setPwdcode(String pwdcode) {
		this.pwdcode = pwdcode;
	}

	public SubCopMsgBeanVO[] getSubcorpvos() {
		return subcorpvos;
	}

	public void setSubcorpvos(SubCopMsgBeanVO[] subcorpvos) {
		this.subcorpvos = subcorpvos;
	}

	public void setSrvcustid(String srvcustid) {
		this.srvcustid = srvcustid;
	}

	public String getSrvcustid() {
		return srvcustid;
	}

	public String getHximaccountid() {
		return hximaccountid;
	}

	public void setHximaccountid(String hximaccountid) {
		this.hximaccountid = hximaccountid;
	}

	public String getHximpwd() {
		return hximpwd;
	}

	public void setHximpwd(String hximpwd) {
		this.hximpwd = hximpwd;
	}

	public String getHxuuid() {
		return hxuuid;
	}

	public void setHxuuid(String hxuuid) {
		this.hxuuid = hxuuid;
	}

	public String getPhotopath() {
		return photopath;
	}

	public void setPhotopath(String photopath) {
		this.photopath = photopath;
	}

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
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

	public String getApp_user_qq() {
		return app_user_qq;
	}

	public void setApp_user_qq(String app_user_qq) {
		this.app_user_qq = app_user_qq;
	}

	public String getApp_user_tel() {
		return app_user_tel;
	}

	public void setApp_user_tel(String app_user_tel) {
		this.app_user_tel = app_user_tel;
	}

	public String[] getLongitude() {
		return longitude;
	}

	public void setLongitude(String[] longitude) {
		this.longitude = longitude;
	}

	public String[] getLatitude() {
		return latitude;
	}

	public void setLatitude(String[] latitude) {
		this.latitude = latitude;
	}

	private String isaccorp;

	public String getIsaccorp() {
		return isaccorp;
	}

	public void setIsaccorp(String isaccorp) {
		this.isaccorp = isaccorp;
	}

	// �û���
	private String account;

	// �û�����
	private String token;
	// �˺�id(cuserid)
	private String account_id;
	// �Ƿ�demo��˾�û�
	private String isdemo;

	public String getIsdemo() {
		return isdemo;
	}

	public void setIsdemo(String isdemo) {
		this.isdemo = isdemo;
	}

	public String getAccount_id() {
		return account_id;
	}

	public void setAccount_id(String account_id) {
		this.account_id = account_id;
	}

	// public String getPk_corp() {
	// return pk_corp;
	// }
	// public void setPk_corp(String pk_corp) {
	// this.pk_corp = pk_corp;
	// }
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String[] getPk_svorg() {
		return pk_svorg;
	}

	public void setPk_svorg(String[] pk_svorg) {
		this.pk_svorg = pk_svorg;
	}

	public String getCorplongitude() {
		return corplongitude;
	}

	public void setCorplongitude(String corplongitude) {
		this.corplongitude = corplongitude;
	}

	public String getCorplatitude() {
		return corplatitude;
	}

	public void setCorplatitude(String corplatitude) {
		this.corplatitude = corplatitude;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}


	public String getPk_temp_corp() {
		return pk_temp_corp;
	}

	public void setPk_temp_corp(String pk_temp_corp) {
		this.pk_temp_corp = pk_temp_corp;
	}


	public DZFDate getBegdate() {
		return begdate;
	}

	public void setBegdate(DZFDate begdate) {
		this.begdate = begdate;
	}

}
