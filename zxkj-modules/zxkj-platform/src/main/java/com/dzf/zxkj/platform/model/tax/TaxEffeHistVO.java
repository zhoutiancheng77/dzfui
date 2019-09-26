package com.dzf.zxkj.platform.model.tax;

import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TaxEffeHistVO extends SuperVO {

    @JsonProperty("taxhisid")
    private String pk_taxeff_his;
    @JsonProperty("pk_gs")
    private String pk_corp;
    @JsonProperty("sdslx")
    private Integer incomtaxtype;//所得税类型  0：企业所得税 ， 1：个人所得税生产经营所得
    @JsonProperty("zsfs")
    private Integer taxlevytype;// 征收方式 WJX 征收方式:0:定期定额征收（核定征收），1:查账征收
    @JsonProperty("zsksqj")
    private String sxbegperiod;//生效开始期间
    @JsonProperty("zsjsqj")
    private String sxendperiod;//生效结束期间
    @JsonProperty("hcff")
    private Integer verimethod;//核定征收方式
    @JsonProperty("sdssl")
    private DZFDouble incometaxrate;//所得税税率

    public Integer dr;
    public DZFDateTime ts;

    public String getPk_taxeff_his() {
        return pk_taxeff_his;
    }

    public String getSxbegperiod() {
        return sxbegperiod;
    }

    public String getSxendperiod() {
        return sxendperiod;
    }

    public Integer getDr() {
        return dr;
    }

    public Integer getIncomtaxtype() {
        return incomtaxtype;
    }

    public Integer getVerimethod() {
        return verimethod;
    }

    public DZFDouble getIncometaxrate() {
        return incometaxrate;
    }

    public void setVerimethod(Integer verimethod) {
        this.verimethod = verimethod;
    }

    public void setIncometaxrate(DZFDouble incometaxrate) {
        this.incometaxrate = incometaxrate;
    }

    public void setIncomtaxtype(Integer incomtaxtype) {
        this.incomtaxtype = incomtaxtype;
    }

    public Integer getTaxlevytype() {
        return taxlevytype;
    }

    public void setTaxlevytype(Integer taxlevytype) {
        this.taxlevytype = taxlevytype;
    }

    public DZFDateTime getTs() {
        return ts;
    }

    public void setPk_taxeff_his(String pk_taxeff_his) {
        this.pk_taxeff_his = pk_taxeff_his;
    }

    public String getPk_corp() {
        return pk_corp;
    }

    public void setPk_corp(String pk_corp) {
        this.pk_corp = pk_corp;
    }

    public void setSxbegperiod(String sxbegperiod) {
        this.sxbegperiod = sxbegperiod;
    }

    public void setSxendperiod(String sxendperiod) {
        this.sxendperiod = sxendperiod;
    }

    public void setDr(Integer dr) {
        this.dr = dr;
    }

    public void setTs(DZFDateTime ts) {
        this.ts = ts;
    }

    @Override
    public String getParentPKFieldName() {
        return null;
    }

    @Override
    public String getPKFieldName() {
        return "pk_taxeff_his";
    }

    @Override
    public String getTableName() {
        return "ynt_taxeff_his";
    }

}
