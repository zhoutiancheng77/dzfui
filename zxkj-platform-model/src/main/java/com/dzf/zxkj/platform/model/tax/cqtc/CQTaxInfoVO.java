package com.dzf.zxkj.platform.model.tax.cqtc;

import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.model.SuperVO;
import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("rawtypes")
public class CQTaxInfoVO extends SuperVO {
	
	private static final long serialVersionUID = -4401221799240291153L;

	private String pk_corp;//主键
	
	private String openId;//

	@JsonProperty("vscode")
	private String vsoccrecode;//纳税人识别号
	
	@JsonProperty("uname")
	private String unitname;//企业名称
	
	@JsonProperty("bodycode")
	private String legalbodycode;//法人代表
	
	@JsonProperty("paddr")
	private String postaddr;//营业地址
	
	@JsonProperty("chargename")
	private String chargedeptname;//公司性质 一般纳税人||小规模纳税人
	
	private String tradename;//所属行业名称
	
	private String tradecode;//所属行业编码
	
	private String industry;//所属行业主键
	

	
	@JsonProperty("kjzc")
	private Integer ikjzc;//会计政策编码
	
	@JsonProperty("nkname")
	private String vbankname;// 开户银行
	
	@JsonProperty("nkcode")
	private String vbankcode;//银行账号
	
	@JsonProperty("p1")
	private String phone1;//联系人电话
	
	@JsonProperty("dcldate")
	private DZFDate destablishdate;//成立日期
	
	@JsonProperty("atetaxplace")
	private String vstatetaxplace;// 国税主管所
	
    @JsonProperty("zsfs")
    private Integer taxlevytype;// 征收方式:0:定期定额征收（核定征收），1:查账征收
    
    @JsonProperty("djxh")
    private String region;// 等级序号
    

	public Integer getTaxlevytype() {
		return taxlevytype;
	}

	public void setTaxlevytype(Integer taxlevytype) {
		this.taxlevytype = taxlevytype;
	}

	public String getTradecode() {
		return tradecode;
	}

	public void setTradecode(String tradecode) {
		this.tradecode = tradecode;
	}

	public String getVstatetaxplace() {
		return vstatetaxplace;
	}

	public void setVstatetaxplace(String vstatetaxplace) {
		this.vstatetaxplace = vstatetaxplace;
	}

	public DZFDate getDestablishdate() {
		return destablishdate;
	}

	public void setDestablishdate(DZFDate destablishdate) {
		this.destablishdate = destablishdate;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getIndustry() {
		return industry;
	}

	public void setIndustry(String industry) {
		this.industry = industry;
	}

	public String getTradename() {
		return tradename;
	}

	public void setTradename(String tradename) {
		this.tradename = tradename;
	}

	public String getPostaddr() {
		return postaddr;
	}

	public void setPostaddr(String postaddr) {
		this.postaddr = postaddr;
	}

	public String getPhone1() {
		return phone1;
	}

	public void setPhone1(String phone1) {
		this.phone1 = phone1;
	}

	public String getVbankcode() {
		return vbankcode;
	}

	public void setVbankcode(String vbankcode) {
		this.vbankcode = vbankcode;
	}

	public Integer getIkjzc() {
		return ikjzc;
	}

	public void setIkjzc(Integer ikjzc) {
		this.ikjzc = ikjzc;
	}

	public String getVbankname() {
		return vbankname;
	}

	public void setVbankname(String vbankname) {
		this.vbankname = vbankname;
	}

	public String getChargedeptname() {
		return chargedeptname;
	}

	public void setChargedeptname(String chargedeptname) {
		this.chargedeptname = chargedeptname;
	}

	public String getLegalbodycode() {
		return legalbodycode;
	}

	public void setLegalbodycode(String legalbodycode) {
		this.legalbodycode = legalbodycode;
	}

	public String getVsoccrecode() {
		return vsoccrecode;
	}

	public void setVsoccrecode(String vsoccrecode) {
		this.vsoccrecode = vsoccrecode;
	}

	public String getUnitname() {
		return unitname;
	}

	public void setUnitname(String unitname) {
		this.unitname = unitname;
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getPKFieldName() {
		return "pk_corp";
	}

	@Override
	public String getTableName() {
		return "bd_corp";
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}
	
	


	
}
