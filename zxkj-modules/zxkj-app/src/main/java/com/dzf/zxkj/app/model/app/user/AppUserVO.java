package com.dzf.zxkj.app.model.app.user;


import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.platform.model.sys.UserVO;

/**
 * 登录获取信息
 * 
 * @author Administrator
 *
 */
public class AppUserVO extends UserVO {
	private DZFBoolean locked;
	private String job;// 职位
	private String app_corpname;// 公司名称
	private String app_corpadd;// 公司地址
	private String app_user_tel;// 电话
	private String app_user_qq;// QQ
	private String app_user_mail;// 邮箱
	private String app_user_memo;// 备注
	private String unitname;
	private String unitcode;
	private DZFBoolean bisgn;
	private String lguuid;
	private String priid;// 用户在公司的唯一标识
	private DZFDate begdate;// 建账开始日期

	private String vsoccrecode;// 注册码
	private String legalbodycode;// 法人
	private String industry;// 行业
	private String chargedeptname;// 公司性质
	private String vprovince;// 省
	private String vcity;// 市
	private String varea;// 地区

	private String longitude;// 公司经纬度
	private String latitude;
	private String fwlongitude;// 服务机构经纬度
	private String fwlatitude;
	private String pk_svorg;// 服务机构主键
	private String bconfirmsign;// 是否签约

	private String photo;// 图片

	private String hximaccountid;// 环信账户ID
	private String hxaccountname;// 环信账户名称
	private String hximpwd;// 环信账户PWD
	private String hxuuid;// 环信UUID
	private String srvcustid;// 聊天客服ID
	private String nickname;// 昵称
	private String photopath;// 头像路径
	private String password;

	private String sh;//税号
	private String kpdh;// 开票电话
	private String khzh;// 开户帐号
	private String khh;// 开户行
	private String qylx;//企业类型
	private String bbillapply ;//是否有开票权限
	private DZFDate sealeddate;//代账公司封存日期
	private String innercode;//公司编码
	private String fcode;//会计公司编码

	private String lxr;//--新app新增--联系人
	private String email;//--新app新增--联系人邮箱
	private String phone;//--新app新增--联系人手机号
	private String addr;//--新app新增--地址
	private String bankname;//--新app新增--开户银行
	private String bankcode;//--新app新增--银行账号

	public String getLxr() {
		return lxr;
	}

	public void setLxr(String lxr) {
		this.lxr = lxr;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String getPhone() {
		return phone;
	}

	@Override
	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public String getBankname() {
		return bankname;
	}

	public void setBankname(String bankname) {
		this.bankname = bankname;
	}

	public String getBankcode() {
		return bankcode;
	}

	public void setBankcode(String bankcode) {
		this.bankcode = bankcode;
	}

	public String getFcode() {
		return fcode;
	}

	public void setFcode(String fcode) {
		this.fcode = fcode;
	}

	public String getInnercode() {
		return innercode;
	}

	public void setInnercode(String innercode) {
		this.innercode = innercode;
	}

	public DZFDate getSealeddate() {
		return sealeddate;
	}

	public void setSealeddate(DZFDate sealeddate) {
		this.sealeddate = sealeddate;
	}

	public String getBbillapply() {
		return bbillapply;
	}

	public void setBbillapply(String bbillapply) {
		this.bbillapply = bbillapply;
	}

	public String getQylx() {
		return qylx;
	}

	public void setQylx(String qylx) {
		this.qylx = qylx;
	}

	public String getSh() {
		return sh;
	}

	public void setSh(String sh) {
		this.sh = sh;
	}

	public String getKpdh() {
		return kpdh;
	}

	public void setKpdh(String kpdh) {
		this.kpdh = kpdh;
	}

	public String getKhzh() {
		return khzh;
	}

	public void setKhzh(String khzh) {
		this.khzh = khzh;
	}

	public String getKhh() {
		return khh;
	}

	public void setKhh(String khh) {
		this.khh = khh;
	}

	// 代账机构信息
	private String accname;

	public String getLguuid() {
		return lguuid;
	}

	public void setLguuid(String lguuid) {
		this.lguuid = lguuid;
	}

	public String getPhotopath() {
		return photopath;
	}

	public void setPhotopath(String photopath) {
		this.photopath = photopath;
	}

	public String getAccname() {
		return accname;
	}

	public void setAccname(String accname) {
		this.accname = accname;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}

	public String getApp_corpname() {
		return app_corpname;
	}

	public void setApp_corpname(String app_corpname) {
		this.app_corpname = app_corpname;
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

	public String getPk_svorg() {
		return pk_svorg;
	}

	public void setPk_svorg(String pk_svorg) {
		this.pk_svorg = pk_svorg;
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

	public DZFBoolean getLocked() {
		return locked;
	}

	public void setLocked(DZFBoolean locked) {
		this.locked = locked;
	}

	public String getBconfirmsign() {
		return bconfirmsign;
	}

	public void setBconfirmsign(String bconfirmsign) {
		this.bconfirmsign = bconfirmsign;
	}

	public String getFwlongitude() {
		return fwlongitude;
	}

	public void setFwlongitude(String fwlongitude) {
		this.fwlongitude = fwlongitude;
	}

	public String getFwlatitude() {
		return fwlatitude;
	}

	public void setFwlatitude(String fwlatitude) {
		this.fwlatitude = fwlatitude;
	}

	public String getUnitname() {
		return unitname;
	}

	public void setUnitname(String unitname) {
		this.unitname = unitname;
	}

	public DZFBoolean getBisgn() {
		return bisgn;
	}

	public void setBisgn(DZFBoolean bisgn) {
		this.bisgn = bisgn;
	}

	public String getUnitcode() {
		return unitcode;
	}

	public void setUnitcode(String unitcode) {
		this.unitcode = unitcode;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getHxaccountname() {
		return hxaccountname;
	}

	public void setHxaccountname(String hxaccountname) {
		this.hxaccountname = hxaccountname;
	}

	public String getPriid() {
		return priid;
	}

	public void setPriid(String priid) {
		this.priid = priid;
	}

	public String getVsoccrecode() {
		return vsoccrecode;
	}

	public void setVsoccrecode(String vsoccrecode) {
		this.vsoccrecode = vsoccrecode;
	}

	public String getLegalbodycode() {
		return legalbodycode;
	}

	public void setLegalbodycode(String legalbodycode) {
		this.legalbodycode = legalbodycode;
	}

	public String getIndustry() {
		return industry;
	}

	public void setIndustry(String industry) {
		this.industry = industry;
	}

	public String getChargedeptname() {
		return chargedeptname;
	}

	public void setChargedeptname(String chargedeptname) {
		this.chargedeptname = chargedeptname;
	}

	public String getVprovince() {
		return vprovince;
	}

	public void setVprovince(String vprovince) {
		this.vprovince = vprovince;
	}

	public String getVcity() {
		return vcity;
	}

	public void setVcity(String vcity) {
		this.vcity = vcity;
	}

	public String getVarea() {
		return varea;
	}

	public void setVarea(String varea) {
		this.varea = varea;
	}

	public DZFDate getBegdate() {
		return begdate;
	}

	public void setBegdate(DZFDate begdate) {
		this.begdate = begdate;
	}

}
