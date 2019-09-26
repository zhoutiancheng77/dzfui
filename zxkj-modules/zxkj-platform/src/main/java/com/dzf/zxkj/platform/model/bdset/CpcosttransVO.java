package com.dzf.zxkj.platform.model.bdset;

import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.entity.ITradeInfo;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Entity;

/**
 * 公司成本结转模板
 */
@Entity
public class CpcosttransVO extends SuperVO implements ITradeInfo {
    @JsonProperty("tratio")
    private DZFDouble transratio;//结转比例
    @JsonProperty("ts")
    private DZFDateTime ts;
    @JsonProperty("id_fillaccount")
    private String pk_fillaccount;//取数科目
    @JsonProperty("id_corp_costtransfer")
    private String pk_corp_costtransfer;
    @JsonProperty("acts")
    private String abstracts;//摘要
    @JsonProperty("dr")
    private Integer dr;
    @JsonProperty("coid")
    private String coperatorid;
    @JsonProperty("ddate")
    private DZFDate doperatedate;
    @JsonProperty("id_creditaccount")
    private String pk_creditaccount;//贷方科目
    @JsonProperty("id_debitaccount")
    private String pk_debitaccount;//借方科目
    @JsonProperty("id_corp")
    private String pk_corp;
    @JsonProperty("dvcode")
    private String dvcode;
    @JsonProperty("dvname")
    private String dvname;
    @JsonProperty("ddirect")
    private Integer ddirect;
    @JsonProperty("dvlevel")
    private Integer dvlevel;
    @JsonProperty("jvcode")
    private String jvcode;
    @JsonProperty("jvname")
    private String jvname;
    @JsonProperty("jdirect")
    private Integer jdirect;
    @JsonProperty("jvlevel")
    private Integer jvlevel;
    @JsonProperty("pk_creditaccount_name")
    private String pk_creditaccount_name;
    @JsonProperty("pk_debitaccount_name")
    private String pk_debitaccount_name;
    @JsonProperty("pk_fillaccount_name")
    private String pk_fillaccount_name;
    @JsonProperty("jztype")
    private Integer jztype;


    public Integer getJztype() {
        return jztype;
    }

    public void setJztype(Integer jztype) {
        this.jztype = jztype;
    }

    public String getPk_creditaccount_name() {
        return pk_creditaccount_name;
    }

    public void setPk_creditaccount_name(String pk_creditaccount_name) {
        this.pk_creditaccount_name = pk_creditaccount_name;
    }

    public String getPk_debitaccount_name() {
        return pk_debitaccount_name;
    }

    public void setPk_debitaccount_name(String pk_debitaccount_name) {
        this.pk_debitaccount_name = pk_debitaccount_name;
    }

    public String getPk_fillaccount_name() {
        return pk_fillaccount_name;
    }

    public void setPk_fillaccount_name(String pk_fillaccount_name) {
        this.pk_fillaccount_name = pk_fillaccount_name;
    }

    public DZFDouble getTransratio() {
        return transratio;
    }

    public void setTransratio(DZFDouble transratio) {
        this.transratio = transratio;
    }

    public DZFDateTime getTs() {
        return ts;
    }

    public void setTs(DZFDateTime ts) {
        this.ts = ts;
    }

    public String getPk_fillaccount() {
        return pk_fillaccount;
    }

    public void setPk_fillaccount(String pk_fillaccount) {
        this.pk_fillaccount = pk_fillaccount;
    }

    public String getPk_corp_costtransfer() {
        return pk_corp_costtransfer;
    }

    public void setPk_corp_costtransfer(String pk_corp_costtransfer) {
        this.pk_corp_costtransfer = pk_corp_costtransfer;
    }

    public String getAbstracts() {
        return abstracts;
    }

    public void setAbstracts(String abstracts) {
        this.abstracts = abstracts;
    }

    public Integer getDr() {
        return dr;
    }

    public void setDr(Integer dr) {
        this.dr = dr;
    }

    public String getCoperatorid() {
        return coperatorid;
    }

    public void setCoperatorid(String coperatorid) {
        this.coperatorid = coperatorid;
    }

    public DZFDate getDoperatedate() {
        return doperatedate;
    }

    public void setDoperatedate(DZFDate doperatedate) {
        this.doperatedate = doperatedate;
    }

    public String getPk_creditaccount() {
        return pk_creditaccount;
    }

    public void setPk_creditaccount(String pk_creditaccount) {
        this.pk_creditaccount = pk_creditaccount;
    }

    public String getPk_debitaccount() {
        return pk_debitaccount;
    }

    public void setPk_debitaccount(String pk_debitaccount) {
        this.pk_debitaccount = pk_debitaccount;
    }

    public String getPk_corp() {
        return pk_corp;
    }

    public void setPk_corp(String pk_corp) {
        this.pk_corp = pk_corp;
    }

    public String getDvcode() {
        return dvcode;
    }

    public void setDvcode(String dvcode) {
        this.dvcode = dvcode;
    }

    public String getDvname() {
        return dvname;
    }

    public void setDvname(String dvname) {
        this.dvname = dvname;
    }

    public Integer getDdirect() {
        return ddirect;
    }

    public void setDdirect(Integer ddirect) {
        this.ddirect = ddirect;
    }

    public Integer getDvlevel() {
        return dvlevel;
    }

    public void setDvlevel(Integer dvlevel) {
        this.dvlevel = dvlevel;
    }

    public String getJvcode() {
        return jvcode;
    }

    public void setJvcode(String jvcode) {
        this.jvcode = jvcode;
    }

    public String getJvname() {
        return jvname;
    }

    public void setJvname(String jvname) {
        this.jvname = jvname;
    }

    public Integer getJdirect() {
        return jdirect;
    }

    public void setJdirect(Integer jdirect) {
        this.jdirect = jdirect;
    }

    public Integer getJvlevel() {
        return jvlevel;
    }

    public void setJvlevel(Integer jvlevel) {
        this.jvlevel = jvlevel;
    }

    @Override
    public String getParentPKFieldName() {
        return null;
    }

    @Override
    public String getPKFieldName() {
        return "pk_corp_costtransfer";
    }

    @Override
    public String getTableName() {
        return "ynt_cpcosttrans";
    }
}
