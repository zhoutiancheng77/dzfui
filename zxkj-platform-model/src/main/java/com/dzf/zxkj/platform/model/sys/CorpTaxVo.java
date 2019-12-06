package com.dzf.zxkj.platform.model.sys;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CorpTaxVo extends SuperVO {

    public static final String TABLE_NAME = "bd_corp_tax";

    public static final String PK_FIELD = "pk_corp_tax";

    @JsonProperty("taxid")
    public String pk_corp_tax;//公司报税id
    @JsonProperty("pk_gs")
    public String pk_corp;//公司id
    @JsonProperty("mpcode")
    public String vcompcode;// 计算机代码
    @JsonProperty("ukpwd")
    private String ukeypwd;// UKey 密码
    @JsonProperty("d16")
    public String def16;// ----------- 登录方式 CFCA证书和法人一证通[贺智鹏]---------------------bdcorp并没有删除--------------
    @JsonProperty("vsuname")
    private String vstateuname;// 国税用户名
    @JsonProperty("atetaxpwd")
    public String vstatetaxpwd;// 国税密码
    @JsonProperty("vltype")
    private String vllogintype;// 地税登录方式
    @JsonProperty("vluname")
    private String vlocaluname;// 地税用户名
    @JsonProperty("caltaxpwd")
    public String vlocaltaxpwd;// 地税密码
    @JsonProperty("vperpwd")
    public String vpersonalpwd;// 个税密码
    @JsonProperty("cbtax")
    private DZFDouble citybuildtax; // 城建税
    @JsonProperty("sdsbsjg")
    private Integer isdsbsjg;// 所得税报送机关(0-国税局、1-地税局)
    @JsonProperty("taxconmatpe")
    private String taxcontrmachtype; // 税控器具类型
    @JsonProperty("l1")
    public String linkman1;// 财务负责人-----------------------------------------------bdcorp并没有删除-----
    @JsonProperty("taxer")
    private String vtaxofficernm;// 办税人员名称
    @JsonProperty("taxerid")
    private String vtaxofficer;// 办税人员id
    @JsonProperty("jspbh")
    private String golddiskno;// 金税盘编号 WJX
    @JsonProperty("tax_code")
    public String taxcode; // --公司类别(普通,结算中心)-- 税务代码
    @JsonProperty("idnum")
    public String idnumber;// 期末从业人数
    @JsonProperty("yhzc")
    private String vyhzc;// 公司可享受的优惠政策 注：小微企业（null、0）、高新企业（1）、软件企业（2）、不享受（9）
    @JsonProperty("rzsj")
    private DZFDate drzsj;// 认证时间
    @JsonProperty("schlnd")
    private String vschlnd;// 首次获利年度
    @JsonProperty("caltaxplace")
    public String vlocaltaxplace;// 地税主管所
    @JsonProperty("caltaxper")
    public String vlocaltaxper;// 地税专管员
    @JsonProperty("caltaxpertel")
    public String vlocaltaxpertel;// 地税专管员电话
    @JsonProperty("caltaxaddr")
    public String vlocaltaxaddr;// 地税位置
    @JsonProperty("atetaxplace")
    public String vstatetaxplace;// 国税主管所
    @JsonProperty("atetaxper")
    public String vstatetaxper;// 国税专管员
    @JsonProperty("atetaxpertel")
    public String vstatetaxpertel;// 国税专管员电话
    @JsonProperty("atetaxdate")
    public DZFDate dstatetaxdate;// 有效期至
    @JsonProperty("atetaxaddr")
    public String vstatetaxaddr;// 国税位置
    @JsonProperty("caltaxcode")
    public String vlocaltaxcode;// 地税登记号
    @JsonProperty("lecode")
    public String vfilecode;// 档案号
    @JsonProperty("ywskp")
    private DZFBoolean isywskp;// 有无税控盘
    @JsonProperty("xtype")
    public DZFBoolean vtaxtype;// 税控类型
    @JsonProperty("ukey")
    public DZFBoolean isukey;// 地税有无UKEY
    @JsonProperty("dudate")
    private DZFDate dukeydate;// UKEY到期日
    @JsonProperty("kjzc")
    private Integer ikjzc;// 会计政策(0-企业会计准则、1-小企业会计准则、2-企业会计制度、3-事业单位会计准则、4-民间非营利组织会计制度)(*)
    @JsonProperty("ovince")
    public Integer tax_area;// 报税地区
    public Integer dr;
    public DZFDateTime ts;
    @JsonProperty("ismantax")
    private DZFBoolean ismaintainedtax;// 是否已维护税率信息
    @JsonProperty("hcff")
    private Integer verimethod;//核查方法
    @JsonProperty("jsfs")
    private Integer caltype;//计算方式
    @JsonProperty("sdssl")
    private DZFDouble incometaxrate;//所得税税率
    @JsonProperty("zsfs")
    private Integer taxlevytype;// 征收方式 WJX 征收方式:0:定期定额征收（核定征收），1:查账征收 (不存库--从bd_corp获取)
    @JsonProperty("sdslx")
    private Integer incomtaxtype;//所得税类型  0：企业所得税 ， 1：个人所得税生产经营所得
    @JsonProperty("zsksqj")
    private String sxbegperiod;//生效开始期间
    @JsonProperty("zsjsqj")
    private String sxendperiod;//生效结束期间
    @JsonProperty("ksjyscdate")
    private DZFDate begprodate;//开始经营生产日期
    @JsonProperty("qysbbh")
    private String vcompsocisecucode;//企业社保编号
    @JsonProperty("leatax")
    private DZFDouble localeducaddtax; //地方教育费附加
    @JsonProperty("demo")
    private String vdemo;//备注
    @JsonProperty("zgswj")
    private String restaxbureau;//主管税务局
    @JsonProperty("zgswry")
    private String restaxman;//主管税务人员
    @JsonProperty("zgswlxfs")
    private String restaxphone;//主管税务人员联系方式
    @JsonProperty("edutax")
    private DZFDouble educaddtax; //教育费附加
    @JsonProperty("ifirdnum")
    public String ifirdnumber;// 季初从业人数
    @JsonProperty("taxpertype")
    public String vtaxpersontype;//办税人员身份证件类型（默认居民身份证）
    @JsonProperty("taxpersid")
    public String vtaxpersonid;//办税人员身份证件号码
    @JsonProperty("begincom")
    public DZFBoolean isbegincom;//是否有生产经营所得无综合收入所得

    @JsonProperty("skysfxy")
    public DZFBoolean taxbasesilv;//是否是否签约税库银三方协议 默认不打勾（未签约），打勾（已签约）
    //---------------不存库的字段

    //-------------不存库，需要回写的字段-----------
    @JsonProperty("ccrecode")
    public String vsoccrecode;// 社会信用代码(不存库--从bd_corp获取)
    @JsonProperty("sxrq")
    private Integer isxrq;// 一般纳税人生效日期(不存库--从bd_corp获取)
    @JsonProperty("rdsj")
    private DZFDate drdsj;// 认定时间(不存库--从bd_corp获取)
    @JsonProperty("bodycode")
    public String legalbodycode;// 法人代表(不存库--从bd_corp获取)
    @JsonProperty("corprhone")
    private String vcorporatephone;// 法人电话(不存库--从bd_corp获取)
    @JsonProperty("indusname")
    public String indusname;// 行业名称--不存库
    @JsonProperty("indus")
    public String industry;// 行业(不存库--从bd_corp获取)
    @JsonProperty("uname")
    public String unitname;// 公司名称(不存库--从bd_corp获取)
    @JsonProperty("chname")
    public String chargedeptname;// 公司性质(不存库--从bd_corp获取)

    private DZFBoolean ischannel;//是否加盟商
    //----------不存库，需要回写的字段------------------


    //-----------不存库，不需要回写的字段------------
    @JsonProperty("ctypename")
    public String ctypename;// 行业科目方案---不存库
    @JsonProperty("bdate")
    public DZFDate begindate;// 建账日期
    @JsonProperty("cdate")
    public DZFDate createdate;// 录入日期
    @JsonProperty("incode")
    public String innercode;//客户编码
    //	public Integer vprovince;// 省(不存库--从bd_corp获取),不是报税地区。千万注意
    @JsonProperty("stsource")
    public String vcustsource;// 客户来源(不存库--从bd_corp获取)
    //	@JsonProperty("city")
//	public Integer vcity;// 市(不存库--从bd_corp获取)
//	@JsonProperty("area")
//	public Integer varea;// 区(不存库--从bd_corp获取)
    @JsonProperty("nkname")
    public String vbankname;// 开户银行(不存库--从bd_corp获取)
    @JsonProperty("fcorp")
    public String fathercorp;//(不存库--从bd_corp获取)
    @JsonProperty("seal")
    public DZFBoolean isseal;// 是否已停用(不存库--从bd_corp获取)
    @JsonProperty("tradecode")
    private String vtradecode;// 国家标准行业编码---不存库
    @JsonProperty("pcount")
    public String pcountname;// 主管会计名称
    @JsonProperty("pcountid")
    public String vsuperaccount;// 主管会计主键
    @JsonProperty("comptype")
    private Integer icompanytype;// 公司类型 1：有限公司；2：个人独资企业；3：合伙企业；

    @JsonProperty("costforwardstyle")
    private Integer icostforwardstyle;// 成本结转类型
    @JsonProperty("buildic")
    private String bbuildic;// --启用ic模块-- 是否库存管理 (0,代表之前的 (N 和 null) ----,1,代表之前的Y ----, 2,代表现在的总账存货核算-----)
    @JsonProperty("hasaccount")
    public DZFBoolean ishasaccount;// 是否已建帐
    @JsonProperty("hflag")
    public DZFBoolean holdflag;// 是否启用资产
    @JsonProperty("bb_date")
    private DZFDate busibegindate; // 固定资产启用日期
    @JsonProperty("icbegindate")
    public DZFDate icbegindate;// 库存启用日期
    @JsonProperty("ctype")
    public String corptype;// 科目方案
    //-----------不存库，不需要回写的字段

    @JsonProperty("bgqj")
    private String bgperiod;//变更期间
    @JsonProperty("ylao")
    private DZFDouble yanglaobx;//养老保险
    @JsonProperty("yliao")
    private DZFDouble yiliaobx;//医疗保险
    @JsonProperty("sye")
    private DZFDouble shiyebx;//失业保险
    @JsonProperty("gjj")
    private DZFDouble zfgjj;//住房公积金
    @JsonProperty("zxxj")
    private DZFDouble zxkcxj;//专项小计

    @JsonProperty("coachbdate")
    private DZFDate dcoachbdate;//辅导期开始

    @JsonProperty("coachedate")
    private DZFDate dcoachedate;//辅导期结束

    public DZFBoolean getTaxbasesilv() {
        return taxbasesilv;
    }

    public void setTaxbasesilv(DZFBoolean taxbasesilv) {
        this.taxbasesilv = taxbasesilv;
    }

    public DZFBoolean getIsbegincom() {
        return isbegincom;
    }

    public void setIsbegincom(DZFBoolean isbegincom) {
        this.isbegincom = isbegincom;
    }

    public String getIfirdnumber() {
        return ifirdnumber;
    }

    public String getVtaxpersontype() {
        return vtaxpersontype;
    }

    public String getVtaxpersonid() {
        return vtaxpersonid;
    }

    public void setIfirdnumber(String ifirdnumber) {
        this.ifirdnumber = ifirdnumber;
    }

    public void setVtaxpersontype(String vtaxpersontype) {
        this.vtaxpersontype = vtaxpersontype;
    }

    public void setVtaxpersonid(String vtaxpersonid) {
        this.vtaxpersonid = vtaxpersonid;
    }

    public DZFDouble getEducaddtax() {
        return educaddtax;
    }

    public void setEducaddtax(DZFDouble educaddtax) {
        this.educaddtax = educaddtax;
    }

    public String getRestaxbureau() {
        return restaxbureau;
    }

    public String getRestaxman() {
        return restaxman;
    }

    public String getRestaxphone() {
        return restaxphone;
    }

    public void setRestaxbureau(String restaxbureau) {
        this.restaxbureau = restaxbureau;
    }

    public void setRestaxman(String restaxman) {
        this.restaxman = restaxman;
    }

    public void setRestaxphone(String restaxphone) {
        this.restaxphone = restaxphone;
    }

    public String getCorptype() {
        return corptype;
    }

    public void setCorptype(String corptype) {
        this.corptype = corptype;
    }

    public DZFDate getDcoachbdate() {
        return dcoachbdate;
    }

    public DZFBoolean getHoldflag() {
        return holdflag;
    }

    public DZFDate getBusibegindate() {
        return busibegindate;
    }

    public DZFDate getIcbegindate() {
        return icbegindate;
    }

    public void setHoldflag(DZFBoolean holdflag) {
        this.holdflag = holdflag;
    }

    public void setBusibegindate(DZFDate busibegindate) {
        this.busibegindate = busibegindate;
    }

    public void setIcbegindate(DZFDate icbegindate) {
        this.icbegindate = icbegindate;
    }

    public void setDcoachbdate(DZFDate dcoachbdate) {
        this.dcoachbdate = dcoachbdate;
    }

    public DZFDate getDcoachedate() {
        return dcoachedate;
    }

    public void setDcoachedate(DZFDate dcoachedate) {
        this.dcoachedate = dcoachedate;
    }

    public String getUnitname() {
        return unitname;
    }

    public void setUnitname(String unitname) {
        this.unitname = unitname;
    }

//	public Integer getVprovince() {
//		return vprovince;
//	}
//
//	public void setVprovince(Integer vprovince) {
//		this.vprovince = vprovince;
//	}

    public DZFBoolean getIschannel() {
        return ischannel;
    }

    public void setIschannel(DZFBoolean ischannel) {
        this.ischannel = ischannel;
    }

    public Integer getIncomtaxtype() {
        return incomtaxtype;
    }

    public Integer getIcompanytype() {
        return icompanytype;
    }

    public void setIcompanytype(Integer icompanytype) {
        this.icompanytype = icompanytype;
    }

    public Integer getIcostforwardstyle() {
        return icostforwardstyle;
    }

    public String getBbuildic() {
        return bbuildic;
    }

    public DZFBoolean getIshasaccount() {
        return ishasaccount;
    }

    public void setIcostforwardstyle(Integer icostforwardstyle) {
        this.icostforwardstyle = icostforwardstyle;
    }

    public void setBbuildic(String bbuildic) {
        this.bbuildic = bbuildic;
    }

    public void setIshasaccount(DZFBoolean ishasaccount) {
        this.ishasaccount = ishasaccount;
    }

    public String getSxbegperiod() {
        return sxbegperiod;
    }

    public String getSxendperiod() {
        return sxendperiod;
    }

    public DZFDate getBegprodate() {
        return begprodate;
    }

    public String getVcompsocisecucode() {
        return vcompsocisecucode;
    }

    public void setVcompsocisecucode(String vcompsocisecucode) {
        this.vcompsocisecucode = vcompsocisecucode;
    }

    public DZFDouble getLocaleducaddtax() {
        return localeducaddtax;
    }

    public void setLocaleducaddtax(DZFDouble localeducaddtax) {
        this.localeducaddtax = localeducaddtax;
    }

    public void setIncomtaxtype(Integer incomtaxtype) {
        this.incomtaxtype = incomtaxtype;
    }

    public void setSxbegperiod(String sxbegperiod) {
        this.sxbegperiod = sxbegperiod;
    }

    public void setSxendperiod(String sxendperiod) {
        this.sxendperiod = sxendperiod;
    }

    public void setBegprodate(DZFDate begProdate) {
        this.begprodate = begProdate;
    }

    public String getVcompcode() {
        return vcompcode;
    }

    public void setVcompcode(String vcompcode) {
        this.vcompcode = vcompcode;
    }

    public String getIndusname() {
        return indusname;
    }

    public void setIndusname(String indusname) {
        this.indusname = indusname;
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

    public String getVsoccrecode() {
        return vsoccrecode;
    }

    public void setVsoccrecode(String vsoccrecode) {
        this.vsoccrecode = vsoccrecode;
    }

    public Integer getIsxrq() {
        return isxrq;
    }

    public void setIsxrq(Integer isxrq) {
        this.isxrq = isxrq;
    }

    public DZFDate getDrdsj() {
        return drdsj;
    }

    public void setDrdsj(DZFDate drdsj) {
        this.drdsj = drdsj;
    }

    public String getUkeypwd() {
        return ukeypwd;
    }

    public void setUkeypwd(String ukeypwd) {
        this.ukeypwd = ukeypwd;
    }

    public String getDef16() {
        return def16;
    }

    public void setDef16(String def16) {
        this.def16 = def16;
    }

    public String getVstateuname() {
        return vstateuname;
    }

    public void setVstateuname(String vstateuname) {
        this.vstateuname = vstateuname;
    }

    public String getVstatetaxpwd() {
        return vstatetaxpwd;
    }

    public void setVstatetaxpwd(String vstatetaxpwd) {
        this.vstatetaxpwd = vstatetaxpwd;
    }

    public String getVllogintype() {
        return vllogintype;
    }

    public void setVllogintype(String vllogintype) {
        this.vllogintype = vllogintype;
    }

    public String getVlocaluname() {
        return vlocaluname;
    }

    public void setVlocaluname(String vlocaluname) {
        this.vlocaluname = vlocaluname;
    }

    public String getVlocaltaxpwd() {
        return vlocaltaxpwd;
    }

    public void setVlocaltaxpwd(String vlocaltaxpwd) {
        this.vlocaltaxpwd = vlocaltaxpwd;
    }

    public String getVpersonalpwd() {
        return vpersonalpwd;
    }

    public void setVpersonalpwd(String vpersonalpwd) {
        this.vpersonalpwd = vpersonalpwd;
    }

    public String getPk_corp() {
        return pk_corp;
    }

    public void setPk_corp(String pk_corp) {
        this.pk_corp = pk_corp;
    }

    public String getFathercorp() {
        return fathercorp;
    }

    public void setFathercorp(String fathercorp) {
        this.fathercorp = fathercorp;
    }

    public DZFBoolean getIsseal() {
        return isseal;
    }

    public void setIsseal(DZFBoolean isseal) {
        this.isseal = isseal;
    }

    public String getVcustsource() {
        return vcustsource;
    }

    public void setVcustsource(String vcustsource) {
        this.vcustsource = vcustsource;
    }

//	public Integer getVcity() {
//		return vcity;
//	}
//
//	public void setVcity(Integer vcity) {
//		this.vcity = vcity;
//	}
//
//	public Integer getVarea() {
//		return varea;
//	}
//
//	public void setVarea(Integer varea) {
//		this.varea = varea;
//	}

    public DZFDouble getCitybuildtax() {
        return citybuildtax;
    }

    public void setCitybuildtax(DZFDouble citybuildtax) {
        this.citybuildtax = citybuildtax;
    }

    public Integer getIkjzc() {
        return ikjzc;
    }

    public void setIkjzc(Integer ikjzc) {
        this.ikjzc = ikjzc;
    }

    public Integer getTaxlevytype() {
        return taxlevytype;
    }

    public void setTaxlevytype(Integer taxlevytype) {
        this.taxlevytype = taxlevytype;
    }

    public Integer getIsdsbsjg() {
        return isdsbsjg;
    }

    public void setIsdsbsjg(Integer isdsbsjg) {
        this.isdsbsjg = isdsbsjg;
    }

    public String getTaxcontrmachtype() {
        return taxcontrmachtype;
    }

    public void setTaxcontrmachtype(String taxcontrmachtype) {
        this.taxcontrmachtype = taxcontrmachtype;
    }

    public String getLinkman1() {
        return linkman1;
    }

    public void setLinkman1(String linkman1) {
        this.linkman1 = linkman1;
    }

    public String getVtaxofficernm() {
        return vtaxofficernm;
    }

    public void setVtaxofficernm(String vtaxofficernm) {
        this.vtaxofficernm = vtaxofficernm;
    }

    public String getVtaxofficer() {
        return vtaxofficer;
    }

    public void setVtaxofficer(String vtaxofficer) {
        this.vtaxofficer = vtaxofficer;
    }

    public String getGolddiskno() {
        return golddiskno;
    }

    public void setGolddiskno(String golddiskno) {
        this.golddiskno = golddiskno;
    }

    public String getTaxcode() {
        return taxcode;
    }

    public void setTaxcode(String taxcode) {
        this.taxcode = taxcode;
    }

    public String getCtypename() {
        return ctypename;
    }

    public void setCtypename(String ctypename) {
        this.ctypename = ctypename;
    }

    public DZFDate getBegindate() {
        return begindate;
    }

    public void setBegindate(DZFDate begindate) {
        this.begindate = begindate;
    }

    public DZFDate getCreatedate() {
        return createdate;
    }

    public void setCreatedate(DZFDate createdate) {
        this.createdate = createdate;
    }

    public String getPcountname() {
        return pcountname;
    }

    public void setPcountname(String pcountname) {
        this.pcountname = pcountname;
    }

    public String getVsuperaccount() {
        return vsuperaccount;
    }

    public void setVsuperaccount(String vsuperaccount) {
        this.vsuperaccount = vsuperaccount;
    }

    public String getLegalbodycode() {
        return legalbodycode;
    }

    public void setLegalbodycode(String legalbodycode) {
        this.legalbodycode = legalbodycode;
    }

    public String getVcorporatephone() {
        return vcorporatephone;
    }

    public void setVcorporatephone(String vcorporatephone) {
        this.vcorporatephone = vcorporatephone;
    }

    public String getVbankname() {
        return vbankname;
    }

    public void setVbankname(String vbankname) {
        this.vbankname = vbankname;
    }

    public String getIdnumber() {
        return idnumber;
    }

    public void setIdnumber(String idnumber) {
        this.idnumber = idnumber;
    }

    public String getVyhzc() {
        return vyhzc;
    }

    public void setVyhzc(String vyhzc) {
        this.vyhzc = vyhzc;
    }

    public DZFDate getDrzsj() {
        return drzsj;
    }

    public void setDrzsj(DZFDate drzsj) {
        this.drzsj = drzsj;
    }

    public String getVschlnd() {
        return vschlnd;
    }

    public void setVschlnd(String vschlnd) {
        this.vschlnd = vschlnd;
    }

    public String getVlocaltaxplace() {
        return vlocaltaxplace;
    }

    public void setVlocaltaxplace(String vlocaltaxplace) {
        this.vlocaltaxplace = vlocaltaxplace;
    }

    public String getVlocaltaxper() {
        return vlocaltaxper;
    }

    public void setVlocaltaxper(String vlocaltaxper) {
        this.vlocaltaxper = vlocaltaxper;
    }

    public String getVlocaltaxpertel() {
        return vlocaltaxpertel;
    }

    public void setVlocaltaxpertel(String vlocaltaxpertel) {
        this.vlocaltaxpertel = vlocaltaxpertel;
    }

    public String getVlocaltaxaddr() {
        return vlocaltaxaddr;
    }

    public void setVlocaltaxaddr(String vlocaltaxaddr) {
        this.vlocaltaxaddr = vlocaltaxaddr;
    }

    public String getVstatetaxplace() {
        return vstatetaxplace;
    }

    public void setVstatetaxplace(String vstatetaxplace) {
        this.vstatetaxplace = vstatetaxplace;
    }

    public String getVstatetaxper() {
        return vstatetaxper;
    }

    public void setVstatetaxper(String vstatetaxper) {
        this.vstatetaxper = vstatetaxper;
    }

    public String getVstatetaxpertel() {
        return vstatetaxpertel;
    }

    public void setVstatetaxpertel(String vstatetaxpertel) {
        this.vstatetaxpertel = vstatetaxpertel;
    }

    public DZFDate getDstatetaxdate() {
        return dstatetaxdate;
    }

    public void setDstatetaxdate(DZFDate dstatetaxdate) {
        this.dstatetaxdate = dstatetaxdate;
    }

    public String getVstatetaxaddr() {
        return vstatetaxaddr;
    }

    public void setVstatetaxaddr(String vstatetaxaddr) {
        this.vstatetaxaddr = vstatetaxaddr;
    }

    public String getVlocaltaxcode() {
        return vlocaltaxcode;
    }

    public void setVlocaltaxcode(String vlocaltaxcode) {
        this.vlocaltaxcode = vlocaltaxcode;
    }

    public String getVfilecode() {
        return vfilecode;
    }

    public void setVfilecode(String vfilecode) {
        this.vfilecode = vfilecode;
    }

    public DZFBoolean getIsywskp() {
        return isywskp;
    }

    public void setIsywskp(DZFBoolean isywskp) {
        this.isywskp = isywskp;
    }

    public DZFBoolean getVtaxtype() {
        return vtaxtype;
    }

    public void setVtaxtype(DZFBoolean vtaxtype) {
        this.vtaxtype = vtaxtype;
    }

    public DZFBoolean getIsukey() {
        return isukey;
    }

    public void setIsukey(DZFBoolean isukey) {
        this.isukey = isukey;
    }

    public DZFDate getDukeydate() {
        return dukeydate;
    }

    public void setDukeydate(DZFDate dukeydate) {
        this.dukeydate = dukeydate;
    }

    @Override
    public String getPKFieldName() {
        return PK_FIELD;
    }

    @Override
    public String getParentPKFieldName() {
        return null;
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    public String getPk_corp_tax() {
        return pk_corp_tax;
    }

    public void setPk_corp_tax(String pk_corp_tax) {
        this.pk_corp_tax = pk_corp_tax;
    }

    public Integer getTax_area() {
        return tax_area;
    }

    public void setTax_area(Integer tax_area) {
        this.tax_area = tax_area;
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

    public String getVtradecode() {
        return vtradecode;
    }

    public void setVtradecode(String vtradecode) {
        this.vtradecode = vtradecode;
    }

    public String getInnercode() {
        return innercode;
    }

    public void setInnercode(String innercode) {
        this.innercode = innercode;
    }

    public DZFBoolean getIsmaintainedtax() {
        return ismaintainedtax;
    }

    public void setIsmaintainedtax(DZFBoolean ismaintainedtax) {
        this.ismaintainedtax = ismaintainedtax;
    }

    public Integer getVerimethod() {
        return verimethod;
    }

    public void setVerimethod(Integer verimethod) {
        this.verimethod = verimethod;
    }

    public Integer getCaltype() {
        return caltype;
    }

    public void setCaltype(Integer caltype) {
        this.caltype = caltype;
    }

    public DZFDouble getIncometaxrate() {
        return incometaxrate;
    }

    public void setIncometaxrate(DZFDouble incometaxrate) {
        this.incometaxrate = incometaxrate;
    }

    public String getVdemo() {
        return vdemo;
    }

    public void setVdemo(String vdemo) {
        this.vdemo = vdemo;
    }


    public DZFDouble getYanglaobx() {
        return yanglaobx;
    }

    public DZFDouble getYiliaobx() {
        return yiliaobx;
    }

    public DZFDouble getShiyebx() {
        return shiyebx;
    }

    public DZFDouble getZfgjj() {
        return zfgjj;
    }

    public DZFDouble getZxkcxj() {
        return zxkcxj;
    }

    public String getBgperiod() {
        return bgperiod;
    }

    public void setBgperiod(String bgperiod) {
        this.bgperiod = bgperiod;
    }

    public void setYanglaobx(DZFDouble yanglaobx) {
        this.yanglaobx = yanglaobx;
    }

    public void setYiliaobx(DZFDouble yiliaobx) {
        this.yiliaobx = yiliaobx;
    }

    public void setShiyebx(DZFDouble shiyebx) {
        this.shiyebx = shiyebx;
    }

    public void setZfgjj(DZFDouble zfgjj) {
        this.zfgjj = zfgjj;
    }

    public void setZxkcxj(DZFDouble zxkcxj) {
        this.zxkcxj = zxkcxj;
    }

}
