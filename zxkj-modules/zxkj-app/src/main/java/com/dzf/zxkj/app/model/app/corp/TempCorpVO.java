package com.dzf.zxkj.app.model.app.corp;


import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 临时公司信息
 * 
 * @author Administrator
 */
public class TempCorpVO extends SuperVO {

	private String pk_temp_corp;
	private String corpname;
	private String usercode;
	private String username;
	private String corpaddr;
	private String contactman;
	private DZFDouble longitude;
	private DZFDouble latitude;
	private String tel;
	private DZFBoolean bsign;
	private DZFDateTime ts;
	private Integer dr;
	private String pk_svorg;//代账机构

	private String vsoccrecode;// 注册码
	private String legalbodycode;// 法人
	private String industry;// 行业
	private Integer custnature;//1个人,2法人,3经办人

	private String chargedeptname;// 公司性质
	private String vprovince;// 省
	private String vcity;// 市
	private String varea;// 地区
	private String pk_corp;
	@JsonProperty("sh")
	private String vtaxcode;// 税号
	@JsonProperty("kpdh")
	private String vbillphone;// 开票电话
	@JsonProperty("khh")
	private String vbillbank;// 开户行
	@JsonProperty("khzh")
	private String vbillbankcode;// 开户账号
	
	//工商消息
	public String saleaddr;//住所
	private Integer icompanytype;// 公司类型 1：有限公司；2：个人独资企业；3：合伙企业；
	public String def9;// 注册资本
	public DZFDate destablishdate;// 成立日期
	private String vbusinescope;// 经营范围
	private String vregistorgans;// 登记机关
	private DZFDate dapprovaldate;// 核准日期（发证日期）
	
	
	public String getSaleaddr() {
		return saleaddr;
	}

	public void setSaleaddr(String saleaddr) {
		this.saleaddr = saleaddr;
	}

	public Integer getIcompanytype() {
		return icompanytype;
	}

	public void setIcompanytype(Integer icompanytype) {
		this.icompanytype = icompanytype;
	}

	public String getDef9() {
		return def9;
	}

	public void setDef9(String def9) {
		this.def9 = def9;
	}

	public DZFDate getDestablishdate() {
		return destablishdate;
	}

	public void setDestablishdate(DZFDate destablishdate) {
		this.destablishdate = destablishdate;
	}

	public String getVbusinescope() {
		return vbusinescope;
	}

	public void setVbusinescope(String vbusinescope) {
		this.vbusinescope = vbusinescope;
	}

	public String getVregistorgans() {
		return vregistorgans;
	}

	public void setVregistorgans(String vregistorgans) {
		this.vregistorgans = vregistorgans;
	}

	public DZFDate getDapprovaldate() {
		return dapprovaldate;
	}

	public void setDapprovaldate(DZFDate dapprovaldate) {
		this.dapprovaldate = dapprovaldate;
	}

	public String getPk_svorg() {
		return pk_svorg;
	}

	public void setPk_svorg(String pk_svorg) {
		this.pk_svorg = pk_svorg;
	}

	public Integer getCustnature() {
		return custnature;
	}

	public void setCustnature(Integer custnature) {
		this.custnature = custnature;
	}

	public String getVtaxcode() {
		return vtaxcode;
	}

	public void setVtaxcode(String vtaxcode) {
		this.vtaxcode = vtaxcode;
	}

	public String getVbillphone() {
		return vbillphone;
	}

	public void setVbillphone(String vbillphone) {
		this.vbillphone = vbillphone;
	}

	public String getVbillbank() {
		return vbillbank;
	}

	public void setVbillbank(String vbillbank) {
		this.vbillbank = vbillbank;
	}

	public String getVbillbankcode() {
		return vbillbankcode;
	}

	public void setVbillbankcode(String vbillbankcode) {
		this.vbillbankcode = vbillbankcode;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	private DZFBoolean bconfirmsign;// ȷ��ǩԼ

	public DZFBoolean getBconfirmsign() {
		return bconfirmsign;
	}

	public void setBconfirmsign(DZFBoolean bconfirmsign) {
		this.bconfirmsign = bconfirmsign;
	}

	public DZFDouble getLongitude() {
		return longitude;
	}

	public void setLongitude(DZFDouble longitude) {
		this.longitude = longitude;
	}

	public DZFDouble getLatitude() {
		return latitude;
	}

	public void setLatitude(DZFDouble latitude) {
		this.latitude = latitude;
	}

	public String getPk_temp_corp() {
		return pk_temp_corp;
	}

	public void setPk_temp_corp(String pk_temp_corp) {
		this.pk_temp_corp = pk_temp_corp;
	}

	public String getUsercode() {
		return usercode;
	}

	public void setUsercode(String usercode) {
		this.usercode = usercode;
	}

	public String getCorpname() {
		return corpname;
	}

	public void setCorpname(String corpname) {
		this.corpname = corpname;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getCorpaddr() {
		return corpaddr;
	}

	public void setCorpaddr(String corpaddr) {
		this.corpaddr = corpaddr;
	}

	public String getContactman() {
		return contactman;
	}

	public void setContactman(String contactman) {
		this.contactman = contactman;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public DZFBoolean getBsign() {
		return bsign;
	}

	public void setBsign(DZFBoolean bsign) {
		this.bsign = bsign;
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

	@Override
	public String getPKFieldName() {
		return "pk_temp_corp";
	}

	@Override
	public String getTableName() {
		return "app_temp_corp";
	}

	@Override
	public String getParentPKFieldName() {
		return null;
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

}
