package com.dzf.zxkj.app.model.resp.bean;

import com.dzf.zxkj.common.model.SuperVO;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author liangjy
 *
 */
public class ResponseBaseBeanVO extends SuperVO {
	/**
	 * (老版)公司注册状态
	 * 
	 * 0 未存在的公司名称，如第一张，点击确定即可添加
	 * 
	 * 1已存在的公司名称但未建立账套，如第二张显示提示信息，但提示信息的背景色需要修改，与背面的蒙版进行区分
	 * 
	 * 2已存在的公司名称且已建立账套，如第三张显示另一弹框，对手机号和验证码进行判断。手机号格式错误或未输入
	 * 
	 * 3公司创建成功，直接登录该公司
	 */
	@JsonProperty("cs")
	private String corpStatus;
	private String priid;// 用户在公司的唯一性标识
	private String isys;// 是否是演示公司
	private String isjgfz;//是否封存(代账机构)
	private String usergrade;//是否是管理员
	private String rescode;//返回值
	private Integer resnumber;//返回数字
	private Object resmsg;
	private String confirmsg;// 确认消息
	private String confirm;//同意不同意
	private Object kfmsg;// 客服信息
	private String bsign;
	@JsonProperty("corp")
	private String pk_corp;
	@JsonProperty("cname")
	private String corpname;
	@JsonProperty("caddr")
	private String corpaddr;
	private String bdata;// 图片上传的权限
	private String baccount;// 查看报表的权限
	private String bbillapply;// 是否有申请权限
	private String photopath;;
	private String istate;//
	private String logopath;// logo信息
	private String permitpath;// 营业执照
	private String isautologin;// 是否自动登录

	private String orgcodecerpath;// 组织机构
	private String taxregcerpath;// 税务登记
	private String bankopcerpath;// 银行开户证书
	private String stacerpath;// 统计证书

	@JsonProperty("soccre")
	private String vsoccrecode;// 注册码
	@JsonProperty("legalbody")
	private String legalbodycode;// 法人
	@JsonProperty("idustry")
	private String industry;// 行业
	@JsonProperty("chargedept")
	private String chargedeptname;// 公司性质
	private String chargedept_num;// 数字类型，公司性质
	@JsonProperty("province")
	private String vprovince;// 省
	@JsonProperty("city")
	private String vcity;// 市
	@JsonProperty("area")
	private String varea;// 地区

	@JsonProperty("ph")
	private String phone;
	@JsonProperty("fph")
	private String fphone;// 代账公司电话
	private String identify;// 验证码信息
	private String token;
	private String traceid;//前台传递的id字段，后台不处理
	
	private String fname;// 代账公司名称
	private String fcode;// 代账公司编码
	private String ftel;// 代账公司电话
	private String furl;//代账公司url
	private String flogo;//代账公司logo
	private String qysbh;//企业识别号
	private String nickname;//昵称
	
	//-------------税务信息--------
	private String sh;//税号
	private String kpdh;// 开票电话
	private String khzh;// 开户帐号
	private String khh;// 开户行
	private String pzlx;//票种类型
	private String iot;//iot信息
	private String grdh;//个人电话
	private String gryx;//个人邮箱
	
	//图片提示
	private String img_pj_tips;//图片提示 (0提示 1不提示)



	private String lxr;//--新app新增--联系人
	private String email;//--新app新增--联系人邮箱
	private String lphone;//--新app新增--联系人手机号
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

	public String getLphone() {
		return lphone;
	}

	public void setLphone(String lphone) {
		this.lphone = lphone;
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

	public String getImg_pj_tips() {
		return img_pj_tips;
	}

	public void setImg_pj_tips(String img_pj_tips) {
		this.img_pj_tips = img_pj_tips;
	}

	public String getIsjgfz() {
		return isjgfz;
	}

	public void setIsjgfz(String isjgfz) {
		this.isjgfz = isjgfz;
	}

	public String getIsautologin() {
		return isautologin;
	}

	public void setIsautologin(String isautologin) {
		this.isautologin = isautologin;
	}

	public String getConfirm() {
		return confirm;
	}

	public void setConfirm(String confirm) {
		this.confirm = confirm;
	}

	public String getChargedept_num() {
		return chargedept_num;
	}

	public void setChargedept_num(String chargedept_num) {
		this.chargedept_num = chargedept_num;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getGrdh() {
		return grdh;
	}

	public void setGrdh(String grdh) {
		this.grdh = grdh;
	}

	public String getGryx() {
		return gryx;
	}

	public void setGryx(String gryx) {
		this.gryx = gryx;
	}

	public String getQysbh() {
		return qysbh;
	}

	public void setQysbh(String qysbh) {
		this.qysbh = qysbh;
	}

	public String getFlogo() {
		return flogo;
	}

	public void setFlogo(String flogo) {
		this.flogo = flogo;
	}

	public String getFurl() {
		return furl;
	}

	public void setFurl(String furl) {
		this.furl = furl;
	}
	
	public String getFname() {
		return fname;
	}

	public void setFname(String fname) {
		this.fname = fname;
	}

	public String getFtel() {
		return ftel;
	}

	public void setFtel(String ftel) {
		this.ftel = ftel;
	}

	public String getPzlx() {
		return pzlx;
	}

	public void setPzlx(String pzlx) {
		this.pzlx = pzlx;
	}

	public String getIot() {
		return iot;
	}

	public void setIot(String iot) {
		this.iot = iot;
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

	public String getTraceid() {
		return traceid;
	}

	public void setTraceid(String traceid) {
		this.traceid = traceid;
	}

	public Integer getResnumber() {
		return resnumber;
	}

	public void setResnumber(Integer resnumber) {
		this.resnumber = resnumber;
	}

	public String getToken() {
		return token;
	}

	public String getFphone() {
		return fphone;
	}

	public void setFphone(String fphone) {
		this.fphone = fphone;
	}

	public String getBbillapply() {
		return bbillapply;
	}

	public void setBbillapply(String bbillapply) {
		this.bbillapply = bbillapply;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getCorpStatus() {
		return corpStatus;
	}

	public void setCorpStatus(String corpStatus) {
		this.corpStatus = corpStatus;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
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

	public String getCorpname() {
		return corpname;
	}

	public void setCorpname(String corpname) {
		this.corpname = corpname;
	}

	public String getCorpaddr() {
		return corpaddr;
	}

	public void setCorpaddr(String corpaddr) {
		this.corpaddr = corpaddr;
	}

	public String getBsign() {
		return bsign;
	}

	public void setBsign(String bsign) {
		this.bsign = bsign;
	}

	public Object getResmsg() {
		return resmsg;
	}

	public void setResmsg(Object resmsg) {
		this.resmsg = resmsg;
	}

	public String getRescode() {
		return rescode;
	}

	public void setRescode(String rescode) {
		this.rescode = rescode;
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return null;
	}

	public String getPhotopath() {
		return photopath;
	}

	public void setPhotopath(String photopath) {
		this.photopath = photopath;
	}

	public String getLogopath() {
		return logopath;
	}

	public void setLogopath(String logopath) {
		this.logopath = logopath;
	}

	public String getPermitpath() {
		return permitpath;
	}

	public void setPermitpath(String permitpath) {
		this.permitpath = permitpath;
	}

	public String getIsys() {
		return isys;
	}

	public void setIsys(String isys) {
		this.isys = isys;
	}

	public Object getKfmsg() {
		return kfmsg;
	}

	public String getIstate() {
		return istate;
	}

	public void setIstate(String istate) {
		this.istate = istate;
	}

	public void setKfmsg(Object kfmsg) {
		this.kfmsg = kfmsg;
	}

	public String getConfirmsg() {
		return confirmsg;
	}

	public void setConfirmsg(String confirmsg) {
		this.confirmsg = confirmsg;
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

	public String getOrgcodecerpath() {
		return orgcodecerpath;
	}

	public void setOrgcodecerpath(String orgcodecerpath) {
		this.orgcodecerpath = orgcodecerpath;
	}

	public String getTaxregcerpath() {
		return taxregcerpath;
	}

	public void setTaxregcerpath(String taxregcerpath) {
		this.taxregcerpath = taxregcerpath;
	}

	public String getBankopcerpath() {
		return bankopcerpath;
	}

	public void setBankopcerpath(String bankopcerpath) {
		this.bankopcerpath = bankopcerpath;
	}

	public String getStacerpath() {
		return stacerpath;
	}

	public void setStacerpath(String stacerpath) {
		this.stacerpath = stacerpath;
	}

	public String getIdentify() {
		return identify;
	}

	public void setIdentify(String identify) {
		this.identify = identify;
	}

	public String getPriid() {
		return priid;
	}

	public void setPriid(String priid) {
		this.priid = priid;
	}

	public String getUsergrade() {
		return usergrade;
	}

	public void setUsergrade(String usergrade) {
		this.usergrade = usergrade;
	}

}
