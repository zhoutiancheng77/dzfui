package com.dzf.zxkj.app.model.resp.bean;

import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UserBeanVO extends RequestBaseBeanVO {

	
	//是否管理员
	@JsonProperty("ima")
	private String ismanage;
	//手机
	@JsonProperty("ph")
	private String phone;
	//密码
	@JsonProperty("p")
	private String password;
	//新密码
	@JsonProperty("np")
	private String newpwd;
	//用户编码
	@JsonProperty("ucode")
	private String usercode;
	//用户名
	@JsonProperty("uname")
	private String username;
	//公司名称
	@JsonProperty("cname")
	private String corpname;
	//用户id
	@JsonProperty("uid")
	private String userid;
	//未签约公司Pk
	@JsonProperty("tcorp")
	private String pk_tempcorp;
	//签约之后公司Pk
	@JsonProperty("scorp")
	private String pk_signcorp;
	//用户所属公司id
	@JsonProperty("cid")
	private String corpid;
	//用户所属公司父级公司id
	@JsonProperty("fcid")
	private String fathercorpid;
	//验证码
	@JsonProperty("idfy")
	private String identify;
	//经度
	@JsonProperty("lode")
	private double longitude;
	//纬度
	@JsonProperty("lade")
	private double latitude;
	//公司地址
	@JsonProperty("caddr")
	private String corpaddr;
	//服务机构Id
	private String pk_org;
	//数组列表
	private Object items;
	private String bdata ;
	private String baccount ;
	//查询权限类型
	private String type;
	//证件上传标识
	private String cert;
	//logo上传表示
	private String logo;
	//营业执照上传标识
	private String permit;
	//证件说明
	private String certtx;
	//合作评价信息
	private String des;
	private Integer busitype;
	private String message;
	
	private String photopath;//头像路径
	private String job;//职位
	private String app_user_tel;//电话
	private String app_user_qq;//QQ
	private String app_user_mail;//邮箱
	private String app_user_memo;//备注
	private String istates;//状态
	private String photo;//头像
	private String phototype;//头像图片类型
	
	private String groupkey;//上传证照存的主键
	
	//是否确认继续
	@JsonProperty("icmsg")
	private String isconfirmsg;
	
	@JsonProperty("soccre")
	private String vsoccrecode;// 注册码
	@JsonProperty("legalbody")
	private String legalbodycode;// 法人
	@JsonProperty("idustry")
	private String industry;// 行业
	@JsonProperty("chargedept")
	private String chargedeptname;// 公司性质
	@JsonProperty("province")
	private String vprovince;// 省
	@JsonProperty("city")
	private String vcity;// 市
	@JsonProperty("area")
	private String varea;// 地区
	
	private String orgcodecer;//组织机构
	private String taxregcer;//税务登记
	private String bankopcer;//银行开户证
	private String stacer;//统计证书
	private DZFBoolean iaudituser;//是否待审核的用户
	
	private String kpdate;//开票日期
	private String billno;//票据单号
	private DZFDouble totalmny;//总金额
	private DZFDouble smny;
	private String drcode;
	
	@JsonProperty("ztpj")
	private Integer satisfaction;// 总体评价
	@JsonProperty("yxmc")
	private String appraisalnames;// 印象名称 
	@JsonProperty("fwtd")
	private Integer severbearing ;// 服务态度
	@JsonProperty("zysp")
	private Integer  specialty  ; // 专业水平
	@JsonProperty("jsx")
	private Integer  betimes  ;// 及时性
	@JsonProperty("pjrq")
	private String busidate;//评价日期
	@JsonProperty("msgtype")
	private String msgtype;//消息类型
	@JsonProperty("acode")
	private String activecode;//激活码
	@JsonProperty("hcorp")
	private String handcorp;//待处理公司
	@JsonProperty("devicmsg")
	private String vdevicdmsg;//设备信息
	
	private String bbillapply;//票据申请提交权限
	private String sourcesys;//来源系统
	private String memo1;//备注
	private String wx_js_code;//微信code
	
	private String iot;//iot号码
	private String isautologin;//是否自动登录
	private String bw_login;//百望登录信息
	private String qysbh;//企业识别号
	
	private String wx_unionid;//微信unionid
	private String wx_nickname;//微信昵称
	private String wx_openid;//微信id
	
	public Integer getSatisfaction() {
		return satisfaction;
	}
	public void setSatisfaction(Integer satisfaction) {
		this.satisfaction = satisfaction;
	}
	public String getAppraisalnames() {
		return appraisalnames;
	}
	public void setAppraisalnames(String appraisalnames) {
		this.appraisalnames = appraisalnames;
	}
	public Integer getSeverbearing() {
		return severbearing;
	}
	public void setSeverbearing(Integer severbearing) {
		this.severbearing = severbearing;
	}
	public Integer getSpecialty() {
		return specialty;
	}
	public void setSpecialty(Integer specialty) {
		this.specialty = specialty;
	}
	public Integer getBetimes() {
		return betimes;
	}
	public void setBetimes(Integer betimes) {
		this.betimes = betimes;
	}
	public String getBusidate() {
		return busidate;
	}
	public void setBusidate(String busidate) {
		this.busidate = busidate;
	}
	public String getOrgcodecer() {
		return orgcodecer;
	}
	public void setOrgcodecer(String orgcodecer) {
		this.orgcodecer = orgcodecer;
	}
	public String getTaxregcer() {
		return taxregcer;
	}
	public void setTaxregcer(String taxregcer) {
		this.taxregcer = taxregcer;
	}
	public String getBankopcer() {
		return bankopcer;
	}
	public void setBankopcer(String bankopcer) {
		this.bankopcer = bankopcer;
	}
	public String getStacer() {
		return stacer;
	}
	public void setStacer(String stacer) {
		this.stacer = stacer;
	}
	public String getIsconfirmsg() {
		return isconfirmsg;
	}
	public void setIsconfirmsg(String isconfirmsg) {
		this.isconfirmsg = isconfirmsg;
	}
	public String getGroupkey() {
		return groupkey;
	}
	public void setGroupkey(String groupkey) {
		this.groupkey = groupkey;
	}
	private String mny;
	private String memo;
	@JsonProperty("method")
	private String paymethod;//0-1-2(0:现金1:银行:2其他)
	
	private String certbusitype;//业务类型
	private String certctnum;//联系方式
	private String certmsg;//业务合作留言
	private String otcorp;//对方单位;
	
	// 提交历史报表主键
	private String pk_taxreportent;
	// 报表主键
	private String pk_taxreport;
	private String vbillstatus;
	
	public String getOtcorp() {
		return otcorp;
	}
	public void setOtcorp(String otcorp) {
		this.otcorp = otcorp;
	}
	public String getCertbusitype() {
		return certbusitype;
	}
	public void setCertbusitype(String certbusitype) {
		this.certbusitype = certbusitype;
	}
	public String getCertctnum() {
		return certctnum;
	}
	public void setCertctnum(String certctnum) {
		this.certctnum = certctnum;
	}
	public String getCertmsg() {
		return certmsg;
	}
	public void setCertmsg(String certmsg) {
		this.certmsg = certmsg;
	}
	public String getMny() {
		return mny;
	}
	public void setMny(String mny) {
		this.mny = mny;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public String getPaymethod() {
		return paymethod;
	}
	public void setPaymethod(String paymethod) {
		this.paymethod = paymethod;
	}
	public String getPhoto() {
		return photo;
	}
	public void setPhoto(String photo) {
		this.photo = photo;
	}
	public String getPhototype() {
		return phototype;
	}
	public void setPhototype(String phototype) {
		this.phototype = phototype;
	}
	public String getPhotopath() {
		return photopath;
	}
	public void setPhotopath(String photopath) {
		this.photopath = photopath;
	}
	public String getIstates() {
		return istates;
	}
	public void setIstates(String istates) {
		this.istates = istates;
	}
	
	public String getJob() {
		return job;
	}
	public void setJob(String job) {
		this.job = job;
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
	public String getDes() {
		return des;
	}
	public void setDes(String des) {
		this.des = des;
	}
	public Integer getBusitype() {
		return busitype;
	}
	public void setBusitype(Integer busitype) {
		this.busitype = busitype;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getCerttx() {
		return certtx;
	}
	public void setCerttx(String certtx) {
		this.certtx = certtx;
	}
	public String getCert() {
		return cert;
	}
	public void setCert(String cert) {
		this.cert = cert;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getBdata() {
		return bdata;
	}
	public void setBdata(String bdata) {
		this.bdata = bdata;
	}
	public String getBaccount() {
		return baccount;
	}
	public void setBaccount(String baccount) {
		this.baccount = baccount;
	}
	
	
	public String getPk_signcorp() {
		return pk_signcorp;
	}
	public void setPk_signcorp(String pk_signcorp) {
		this.pk_signcorp = pk_signcorp;
	}
	public String getPk_tempcorp() {
		return pk_tempcorp;
	}
	public void setPk_tempcorp(String pk_tempcorp) {
		this.pk_tempcorp = pk_tempcorp;
	}
	public String getFathercorpid() {
		return fathercorpid;
	}
	public void setFathercorpid(String fathercorpid) {
		this.fathercorpid = fathercorpid;
	}
	public Object getItems() {
		return items;
	}
	public void setItems(Object items) {
		this.items = items;
	}
	public String getUsercode() {
		return usercode;
	}
	public void setUsercode(String usercode) {
		this.usercode = usercode;
	}
	public String getPk_org() {
		return pk_org;
	}
	public void setPk_org(String pk_org) {
		this.pk_org = pk_org;
	}
	public String getCorpaddr() {
		return corpaddr;
	}
	public void setCorpaddr(String corpaddr) {
		this.corpaddr = corpaddr;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public String getIdentify() {
		return identify;
	}
	public void setIdentify(String identify) {
		this.identify = identify;
	}
	public String getNewpwd() {
		return newpwd;
	}
	public void setNewpwd(String newpwd) {
		this.newpwd = newpwd;
	}
	public String getCorpid() {
		return corpid;
	}
	public void setCorpid(String corpid) {
		this.corpid = corpid;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getCorpname() {
		return corpname;
	}
	public void setCorpname(String corpname) {
		this.corpname = corpname;
	}
	public String getIsmanage() {
		return ismanage;
	}
	public void setIsmanage(String ismanage) {
		this.ismanage = ismanage;
	}
	public String getLogo() {
		return logo;
	}
	public void setLogo(String logo) {
		this.logo = logo;
	}
	public String getPermit() {
		return permit;
	}
	public void setPermit(String permit) {
		this.permit = permit;
	}
	public String getPk_taxreportent() {
		return pk_taxreportent;
	}
	public void setPk_taxreportent(String pk_taxreportent) {
		this.pk_taxreportent = pk_taxreportent;
	}
	public String getPk_taxreport() {
		return pk_taxreport;
	}
	public void setPk_taxreport(String pk_taxreport) {
		this.pk_taxreport = pk_taxreport;
	}
	public String getVbillstatus() {
		return vbillstatus;
	}
	public void setVbillstatus(String vbillstatus) {
		this.vbillstatus = vbillstatus;
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
	public DZFBoolean getIaudituser() {
		return iaudituser;
	}
	public void setIaudituser(DZFBoolean iaudituser) {
		this.iaudituser = iaudituser;
	}
	public String getKpdate() {
		return kpdate;
	}
	public void setKpdate(String kpdate) {
		this.kpdate = kpdate;
	}
	public String getBillno() {
		return billno;
	}
	public void setBillno(String billno) {
		this.billno = billno;
	}
	public DZFDouble getTotalmny() {
		return totalmny;
	}
	public void setTotalmny(DZFDouble totalmny) {
		this.totalmny = totalmny;
	}
	public DZFDouble getSmny() {
		return smny;
	}
	public void setSmny(DZFDouble smny) {
		this.smny = smny;
	}
	public String getDrcode() {
		return drcode;
	}
	public void setDrcode(String drcode) {
		this.drcode = drcode;
	}
	public String getMsgtype() {
		return msgtype;
	}
	public void setMsgtype(String msgtype) {
		this.msgtype = msgtype;
	}
	public String getActivecode() {
		return activecode;
	}
	public void setActivecode(String activecode) {
		this.activecode = activecode;
	}
	public String getHandcorp() {
		return handcorp;
	}
	public void setHandcorp(String handcorp) {
		this.handcorp = handcorp;
	}
	public String getVdevicdmsg() {
		return vdevicdmsg;
	}
	public void setVdevicdmsg(String vdevicdmsg) {
		this.vdevicdmsg = vdevicdmsg;
	}
	public String getBbillapply() {
		return bbillapply;
	}
	public void setBbillapply(String bbillapply) {
		this.bbillapply = bbillapply;
	}
	public String getSourcesys() {
		return sourcesys;
	}
	public void setSourcesys(String sourcesys) {
		this.sourcesys = sourcesys;
	}
	public String getMemo1() {
		return memo1;
	}
	public void setMemo1(String memo1) {
		this.memo1 = memo1;
	}
	public String getWx_js_code() {
		return wx_js_code;
	}
	public void setWx_js_code(String wx_js_code) {
		this.wx_js_code = wx_js_code;
	}
	public String getIot() {
		return iot;
	}
	public void setIot(String iot) {
		this.iot = iot;
	}
	public String getIsautologin() {
		return isautologin;
	}
	public void setIsautologin(String isautologin) {
		this.isautologin = isautologin;
	}
	public String getBw_login() {
		return bw_login;
	}
	public void setBw_login(String bw_login) {
		this.bw_login = bw_login;
	}
	public String getQysbh() {
		return qysbh;
	}
	public void setQysbh(String qysbh) {
		this.qysbh = qysbh;
	}
	public String getWx_unionid() {
		return wx_unionid;
	}
	public void setWx_unionid(String wx_unionid) {
		this.wx_unionid = wx_unionid;
	}
	public String getWx_nickname() {
		return wx_nickname;
	}
	public void setWx_nickname(String wx_nickname) {
		this.wx_nickname = wx_nickname;
	}
	public String getWx_openid() {
		return wx_openid;
	}
	public void setWx_openid(String wx_openid) {
		this.wx_openid = wx_openid;
	}
	
}
