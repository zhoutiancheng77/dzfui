package com.dzf.zxkj.base.query;


import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.model.SuperVO;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class QueryParamVO extends SuperVO {
    @JsonProperty("corpIds")
    private String pk_corp;
    @JsonProperty("corpcode")
    private String corpcode;
    @JsonProperty("corpname")
    private String corpname;
    private String corpid;
    private String userid;
    private String username;
    private String usercode;
    @JsonProperty("corpIdss")
    private String[] pk_corps;
    private List<String> corpslist;
    private DZFDate clientdate;
    @JsonProperty("qjq")
    private String qjq;
    @JsonProperty("qjz")
    private String qjz;
    private String kms_id;
    @JsonProperty("kms")
    private String kms;
    private String kmsx;
    private String xjlsx;
    @JsonProperty("begindate")
    private DZFDate begindate1;
    @JsonProperty("enddate")
    private DZFDate enddate;
    private DZFBoolean xswyewfs;
    private DZFBoolean xsyljfs;
    private DZFBoolean ishasjz;
    private DZFBoolean ishassh;
    private DZFBoolean ismj;
    private DZFBoolean isleaf;
    private String pk_currency;

    private List<String> kmcodelist;
    private String fathercorp;
    private String year;
    private String ymonth;
    @JsonProperty("cjq")
    private Integer cjq;
    @JsonProperty("cjz")
    private Integer cjz;
    private Integer levelq;
    private Integer levelz;
    private DZFBoolean isLevel;
    @JsonProperty("pk_assetcard")
    private String pk_assetcard;
    @JsonProperty("pk_inventory")
    private String pk_inventory;
    @JsonProperty("pk_assetcategory")
    private String pk_assetcategory;
    @JsonProperty("ascode")
    private String ascode;
    @JsonProperty("asname")
    private String asname;
    private String zccode;
    private String zcsx;
    private DZFBoolean isqc;
    private DZFBoolean istogl;
    private DZFBoolean isclear;
    private DZFBoolean iscarover;
    private DZFBoolean isuncarover;
    private String hc;
    private String kms_first;
    private String kms_last;
    @JsonProperty("bcreatedate")
    private DZFDate bcreatedate;
    @JsonProperty("ecreatedate")
    private DZFDate ecreatedate;
    private DZFBoolean sfzxm;
    private DZFBoolean ishowfs;
    private DZFBoolean isnomonthfs;
    private String vprovince;
    @JsonProperty("ismantax")
    private DZFBoolean ismaintainedtax;
    @JsonProperty("mantax")
    private Integer maintainedtax;
    private DZFBoolean isdkfp;
    private DZFBoolean isdbbx;
    private Integer isywskp;
    private Integer ifwgs;
    private DZFBoolean isappqry;
    private String sql;
    private DZFBoolean btotalyear;
    private DZFBoolean isformal;
    private String xmlbid;
    private String xmmcid;
    private Integer qrytype;
    private String rptsource;
    private Integer isdzfapp;
    private List<String> firstlevelkms;
    private DZFBoolean xswyewfs_bn;
    private DZFBoolean nhasyj;
    @JsonProperty("fname")
    private String foreignname;
    private String chargedeptname;
    private String currency; //币种名称


    public String getChargedeptname() {
        return chargedeptname;
    }

    public void setChargedeptname(String chargedeptname) {
        this.chargedeptname = chargedeptname;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public QueryParamVO() {
        this.isLevel = DZFBoolean.FALSE;
    }

    public String getForeignname() {
        return this.foreignname;
    }

    public void setForeignname(String foreignname) {
        this.foreignname = foreignname;
    }

    public String getCorpid() {
        return this.corpid;
    }

    public void setCorpid(String corpid) {
        this.corpid = corpid;
    }

    public Integer getIsdzfapp() {
        return this.isdzfapp;
    }

    public void setIsdzfapp(Integer isdzfapp) {
        this.isdzfapp = isdzfapp;
    }

    public Integer getQrytype() {
        return this.qrytype;
    }

    public void setQrytype(Integer qrytype) {
        this.qrytype = qrytype;
    }

    public Integer getMaintainedtax() {
        return this.maintainedtax;
    }

    public void setMaintainedtax(Integer maintainedtax) {
        this.maintainedtax = maintainedtax;
    }

    public DZFBoolean getIsformal() {
        return this.isformal;
    }

    public void setIsformal(DZFBoolean isformal) {
        this.isformal = isformal;
    }

    public DZFBoolean getIsdkfp() {
        return this.isdkfp;
    }

    public void setIsdkfp(DZFBoolean isdkfp) {
        this.isdkfp = isdkfp;
    }

    public DZFBoolean getIsdbbx() {
        return this.isdbbx;
    }

    public void setIsdbbx(DZFBoolean isdbbx) {
        this.isdbbx = isdbbx;
    }

    public Integer getIsywskp() {
        return this.isywskp;
    }

    public void setIsywskp(Integer isywskp) {
        this.isywskp = isywskp;
    }

    public Integer getIfwgs() {
        return this.ifwgs;
    }

    public void setIfwgs(Integer ifwgs) {
        this.ifwgs = ifwgs;
    }

    public DZFDate getBcreatedate() {
        return this.bcreatedate;
    }

    public void setBcreatedate(DZFDate bcreatedate) {
        this.bcreatedate = bcreatedate;
    }

    public DZFDate getEcreatedate() {
        return this.ecreatedate;
    }

    public void setEcreatedate(DZFDate ecreatedate) {
        this.ecreatedate = ecreatedate;
    }

    public DZFBoolean getIsqc() {
        return this.isqc;
    }

    public void setIsqc(DZFBoolean isqc) {
        this.isqc = isqc;
    }

    public DZFBoolean getIstogl() {
        return this.istogl;
    }

    public void setIstogl(DZFBoolean istogl) {
        this.istogl = istogl;
    }

    public DZFBoolean getIsclear() {
        return this.isclear;
    }

    public void setIsclear(DZFBoolean isclear) {
        this.isclear = isclear;
    }

    public String getPk_assetcategory() {
        return this.pk_assetcategory;
    }

    public void setPk_assetcategory(String pk_assetcategory) {
        this.pk_assetcategory = pk_assetcategory;
    }

    public String getAscode() {
        return this.ascode;
    }

    public void setAscode(String ascode) {
        this.ascode = ascode;
    }

    public String getAsname() {
        return this.asname;
    }

    public void setAsname(String asname) {
        this.asname = asname;
    }

    public String getPk_inventory() {
        return this.pk_inventory;
    }

    public void setPk_inventory(String pk_inventory) {
        this.pk_inventory = pk_inventory;
    }

    public String getPk_assetcard() {
        return this.pk_assetcard;
    }

    public void setPk_assetcard(String pk_assetcard) {
        this.pk_assetcard = pk_assetcard;
    }

    public String getPk_corp() {
        return this.pk_corp;
    }

    public void setPk_corp(String pk_corp) {
        this.pk_corp = pk_corp;
    }

    public String getKms() {
        return this.kms;
    }

    public void setKms(String kms) {
        this.kms = kms;
    }

    public String getKmsx() {
        return this.kmsx;
    }

    public void setKmsx(String kmsx) {
        this.kmsx = kmsx;
    }

    public DZFDate getBegindate1() {
        return this.begindate1;
    }

    public void setBegindate1(DZFDate begindate1) {
        this.begindate1 = begindate1;
    }

    public DZFDate getEnddate() {
        return this.enddate;
    }

    public void setEnddate(DZFDate enddate) {
        this.enddate = enddate;
    }

    public DZFBoolean getXswyewfs() {
        return this.xswyewfs;
    }

    public void setXswyewfs(DZFBoolean xswyewfs) {
        this.xswyewfs = xswyewfs;
    }

    public DZFBoolean getXsyljfs() {
        return this.xsyljfs;
    }

    public void setXsyljfs(DZFBoolean xsyljfs) {
        this.xsyljfs = xsyljfs;
    }

    public DZFBoolean getIshasjz() {
        return this.ishasjz;
    }

    public void setIshasjz(DZFBoolean ishasjz) {
        this.ishasjz = ishasjz;
    }

    public List<String> getKmcodelist() {
        return this.kmcodelist;
    }

    public void setKmcodelist(List<String> kmcodelist) {
        this.kmcodelist = kmcodelist;
    }

    public DZFBoolean getIshassh() {
        return this.ishassh;
    }

    public void setIshassh(DZFBoolean ishassh) {
        this.ishassh = ishassh;
    }

    public String getPk_currency() {
        return this.pk_currency;
    }

    public void setPk_currency(String pk_currency) {
        this.pk_currency = pk_currency;
    }

    public DZFBoolean getIsmj() {
        return this.ismj;
    }

    public void setIsmj(DZFBoolean ismj) {
        this.ismj = ismj;
    }

    public Integer getLevelq() {
        return this.levelq;
    }

    public void setLevelq(Integer levelq) {
        this.levelq = levelq;
    }

    public Integer getLevelz() {
        return this.levelz;
    }

    public void setLevelz(Integer levelz) {
        this.levelz = levelz;
    }

    public DZFBoolean getIsLevel() {
        return this.isLevel;
    }

    public void setIsLevel(DZFBoolean isLevel) {
        this.isLevel = isLevel;
    }

    public String getQjq() {
        return this.qjq;
    }

    public void setQjq(String qjq) {
        this.qjq = qjq;
    }

    public String getQjz() {
        return this.qjz;
    }

    public void setQjz(String qjz) {
        this.qjz = qjz;
    }

    public Integer getCjq() {
        return this.cjq;
    }

    public void setCjq(Integer cjq) {
        this.cjq = cjq;
    }

    public Integer getCjz() {
        return this.cjz;
    }

    public void setCjz(Integer cjz) {
        this.cjz = cjz;
    }

    public String getKms_id() {
        return this.kms_id;
    }

    public void setKms_id(String kms_id) {
        this.kms_id = kms_id;
    }

    public String getParentPKFieldName() {
        return null;
    }

    public String getPKFieldName() {
        return null;
    }

    public String getTableName() {
        return null;
    }

    public List<String> getCorpslist() {
        return this.corpslist;
    }

    public void setCorpslist(List<String> corpslist) {
        this.corpslist = corpslist;
    }

    public DZFDate getClientdate() {
        return this.clientdate;
    }

    public void setClientdate(DZFDate clientdate) {
        this.clientdate = clientdate;
    }

    public String getCorpcode() {
        return this.corpcode;
    }

    public void setCorpcode(String corpcode) {
        this.corpcode = corpcode;
    }

    public String getCorpname() {
        return this.corpname;
    }

    public void setCorpname(String corpname) {
        this.corpname = corpname;
    }

    public String[] getPk_corps() {
        return this.pk_corps;
    }

    public void setPk_corps(String[] pk_corps) {
        this.pk_corps = pk_corps;
    }

    public String getKms_first() {
        return this.kms_first;
    }

    public void setKms_first(String kms_first) {
        this.kms_first = kms_first;
    }

    public String getKms_last() {
        return this.kms_last;
    }

    public void setKms_last(String kms_last) {
        this.kms_last = kms_last;
    }

    public String getFathercorp() {
        return this.fathercorp;
    }

    public void setFathercorp(String fathercorp) {
        this.fathercorp = fathercorp;
    }

    public String getZcsx() {
        return this.zcsx;
    }

    public void setZcsx(String zcsx) {
        this.zcsx = zcsx;
    }

    public DZFBoolean getIscarover() {
        return this.iscarover;
    }

    public void setIscarover(DZFBoolean iscarover) {
        this.iscarover = iscarover;
    }

    public DZFBoolean getIsuncarover() {
        return this.isuncarover;
    }

    public void setIsuncarover(DZFBoolean isuncarover) {
        this.isuncarover = isuncarover;
    }

    public String getYear() {
        return this.year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getUserid() {
        return this.userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getHc() {
        return this.hc;
    }

    public void setHc(String hc) {
        this.hc = hc;
    }

    public String getYmonth() {
        return this.ymonth;
    }

    public void setYmonth(String ymonth) {
        this.ymonth = ymonth;
    }

    public String getZccode() {
        return this.zccode;
    }

    public void setZccode(String zccode) {
        this.zccode = zccode;
    }

    public String getXjlsx() {
        return this.xjlsx;
    }

    public void setXjlsx(String xjlsx) {
        this.xjlsx = xjlsx;
    }

    public DZFBoolean getIsleaf() {
        return this.isleaf;
    }

    public void setIsleaf(DZFBoolean isleaf) {
        this.isleaf = isleaf;
    }

    public DZFBoolean getSfzxm() {
        return this.sfzxm;
    }

    public void setSfzxm(DZFBoolean sfzxm) {
        this.sfzxm = sfzxm;
    }

    public DZFBoolean getIshowfs() {
        return this.ishowfs;
    }

    public void setIshowfs(DZFBoolean ishowfs) {
        this.ishowfs = ishowfs;
    }

    public DZFBoolean getIsnomonthfs() {
        return this.isnomonthfs;
    }

    public void setIsnomonthfs(DZFBoolean isnomonthfs) {
        this.isnomonthfs = isnomonthfs;
    }

    public String getVprovince() {
        return this.vprovince;
    }

    public void setVprovince(String vprovince) {
        this.vprovince = vprovince;
    }

    public DZFBoolean getIsmaintainedtax() {
        return this.ismaintainedtax;
    }

    public void setIsmaintainedtax(DZFBoolean ismaintainedtax) {
        this.ismaintainedtax = ismaintainedtax;
    }

    public DZFBoolean getIsappqry() {
        return this.isappqry;
    }

    public String getSql() {
        return this.sql;
    }

    public void setIsappqry(DZFBoolean isappqry) {
        this.isappqry = isappqry;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public DZFBoolean getBtotalyear() {
        return this.btotalyear;
    }

    public void setBtotalyear(DZFBoolean btotalyear) {
        this.btotalyear = btotalyear;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsercode() {
        return this.usercode;
    }

    public void setUsercode(String usercode) {
        this.usercode = usercode;
    }

    public String getXmlbid() {
        return this.xmlbid;
    }

    public void setXmlbid(String xmlbid) {
        this.xmlbid = xmlbid;
    }

    public String getXmmcid() {
        return this.xmmcid;
    }

    public void setXmmcid(String xmmcid) {
        this.xmmcid = xmmcid;
    }

    public String getRptsource() {
        return this.rptsource;
    }

    public void setRptsource(String rptsource) {
        this.rptsource = rptsource;
    }

    public List<String> getFirstlevelkms() {
        return this.firstlevelkms;
    }

    public void setFirstlevelkms(List<String> firstlevelkms) {
        this.firstlevelkms = firstlevelkms;
    }

    public DZFBoolean getXswyewfs_bn() {
        return this.xswyewfs_bn;
    }

    public void setXswyewfs_bn(DZFBoolean xswyewfs_bn) {
        this.xswyewfs_bn = xswyewfs_bn;
    }

    public DZFBoolean getNhasyj() {
        return this.nhasyj;
    }

    public void setNhasyj(DZFBoolean nhasyj) {
        this.nhasyj = nhasyj;
    }
}
