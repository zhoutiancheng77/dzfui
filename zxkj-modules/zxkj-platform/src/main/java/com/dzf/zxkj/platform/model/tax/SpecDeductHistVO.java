package com.dzf.zxkj.platform.model.tax;

import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.fasterxml.jackson.annotation.JsonProperty;

//专项扣除
public class SpecDeductHistVO extends SuperVO {

    @JsonProperty("spechisid")
    private String pk_specdeduct_his;
    @JsonProperty("pk_gs")
    private String pk_corp;
    @JsonProperty("sdslx")
    private Integer incomtaxtype;// 所得税类型 0：企业所得税 ， 1：个人所得税生产经营所得
    @JsonProperty("zsfs")
    private Integer taxlevytype;// 征收方式 WJX 征收方式:0:定期定额征收（核定征收），1:查账征收
    @JsonProperty("bgqj")
    private String bgperiod;// 变更期间
    @JsonProperty("ylao")
    private DZFDouble yanglaobx;// 养老保险
    @JsonProperty("yliao")
    private DZFDouble yiliaobx;// 医疗保险
    @JsonProperty("sye")
    private DZFDouble shiyebx;// 失业保险
    @JsonProperty("gjj")
    private DZFDouble zfgjj;// 住房公积金
    @JsonProperty("zxxj")
    private DZFDouble zxkcxj;// 专项小计

    public Integer dr;
    public DZFDateTime ts;

    public String getPk_specdeduct_his() {
        return pk_specdeduct_his;
    }

    public void setPk_specdeduct_his(String pk_specdeduct_his) {
        this.pk_specdeduct_his = pk_specdeduct_his;
    }

    public String getPk_corp() {
        return pk_corp;
    }

    public Integer getIncomtaxtype() {
        return incomtaxtype;
    }

    public Integer getTaxlevytype() {
        return taxlevytype;
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

    public Integer getDr() {
        return dr;
    }

    public DZFDateTime getTs() {
        return ts;
    }

    public void setPk_corp(String pk_corp) {
        this.pk_corp = pk_corp;
    }

    public void setIncomtaxtype(Integer incomtaxtype) {
        this.incomtaxtype = incomtaxtype;
    }

    public void setTaxlevytype(Integer taxlevytype) {
        this.taxlevytype = taxlevytype;
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

    public void setDr(Integer dr) {
        this.dr = dr;
    }

    public void setTs(DZFDateTime ts) {
        this.ts = ts;
    }

    public String getBgperiod() {
        return bgperiod;
    }

    public void setBgperiod(String bgperiod) {
        this.bgperiod = bgperiod;
    }

    @Override
    public String getParentPKFieldName() {
        return null;
    }

    @Override
    public String getPKFieldName() {
        return "pk_specdeduct_his";
    }

    @Override
    public String getTableName() {
        return "ynt_specdeduct_his";
    }

}
