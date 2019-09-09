package com.dzf.zxkj.platform.model.am.zcgl;

import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.custom.type.DZFDate;
import com.dzf.zxkj.custom.type.DZFDateTime;
import com.dzf.zxkj.custom.type.DZFDouble;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @Auther: dandelion
 * @Date: 2019-09-05
 * @Description:
 */
public class BdTradeAssetCheckVO extends SuperVO {
    private DZFDateTime ts;
    @JsonProperty("cpsn")
    private String coperatorid;
    private Integer dr;
    @JsonProperty("accountcode")
    private String accountcode;
    @JsonProperty("dopd")
    private DZFDate doperatedate;
    @JsonProperty("zzkmid")
    private String pk_glaccount;
    @JsonProperty("zcsx")
    private Integer assetproperty;
    @JsonProperty("hykmid")
    private String pk_trade_accountschema;
    @JsonProperty("id")
    private String pk_trade_assetcheck;
    @JsonProperty("zclbid")
    private String pk_assetcategory;
    @JsonProperty("zckm")
    private Integer assetaccount;
    @JsonProperty("gsid")
    private String pk_corp;
    private String zclbmc;
    private String zzkmmc;
    @JsonProperty("zjfykmid")
    private String pk_zjfykm;
    private String zjfykmcode;
    private String zjfykmmc;
    @JsonProperty("jskmid")
    private String pk_jskm;
    private String jskmmc;
    private String jskmcode;
    private String zzkmcode;
    @JsonProperty("zjfs")
    private Integer zjtype;
    @JsonProperty("czl")
    private DZFDouble salvageratio;
    public static final String COPERATORID = "coperatorid";
    public static final String DOPERATEDATE = "doperatedate";
    public static final String PK_GLACCOUNT = "pk_glaccount";
    public static final String ASSETPROPERTY = "assetproperty";
    public static final String PK_TRADE_ACCOUNTSCHEMA = "pk_trade_accountschema";
    public static final String PK_TRADE_ASSETCHECK = "pk_trade_assetcheck";
    public static final String PK_ASSETCATEGORY = "pk_assetcategory";
    public static final String ASSETACCOUNT = "assetaccount";

    public String getZclbmc() {
        return this.zclbmc;
    }

    public void setZclbmc(String zclbmc) {
        this.zclbmc = zclbmc;
    }

    public String getZzkmmc() {
        return this.zzkmmc;
    }

    public void setZzkmmc(String zzkmmc) {
        this.zzkmmc = zzkmmc;
    }

    public String getPk_corp() {
        return this.pk_corp;
    }

    public void setPk_corp(String pk_corp) {
        this.pk_corp = pk_corp;
    }

    public DZFDateTime getTs() {
        return this.ts;
    }

    public void setTs(DZFDateTime newTs) {
        this.ts = newTs;
    }

    public String getCoperatorid() {
        return this.coperatorid;
    }

    public void setCoperatorid(String newCoperatorid) {
        this.coperatorid = newCoperatorid;
    }

    public Integer getDr() {
        return this.dr;
    }

    public void setDr(Integer newDr) {
        this.dr = newDr;
    }

    public DZFDate getDoperatedate() {
        return this.doperatedate;
    }

    public void setDoperatedate(DZFDate newDoperatedate) {
        this.doperatedate = newDoperatedate;
    }

    public String getPk_glaccount() {
        return this.pk_glaccount;
    }

    public void setPk_glaccount(String newPk_glaccount) {
        this.pk_glaccount = newPk_glaccount;
    }

    public Integer getAssetproperty() {
        return this.assetproperty;
    }

    public void setAssetproperty(Integer newAssetproperty) {
        this.assetproperty = newAssetproperty;
    }

    public String getPk_trade_accountschema() {
        return this.pk_trade_accountschema;
    }

    public void setPk_trade_accountschema(String newPk_trade_accountschema) {
        this.pk_trade_accountschema = newPk_trade_accountschema;
    }

    public String getPk_trade_assetcheck() {
        return this.pk_trade_assetcheck;
    }

    public void setPk_trade_assetcheck(String newPk_trade_assetcheck) {
        this.pk_trade_assetcheck = newPk_trade_assetcheck;
    }

    public String getPk_assetcategory() {
        return this.pk_assetcategory;
    }

    public void setPk_assetcategory(String newPk_assetcategory) {
        this.pk_assetcategory = newPk_assetcategory;
    }

    public Integer getAssetaccount() {
        return this.assetaccount;
    }

    public void setAssetaccount(Integer newAssetaccount) {
        this.assetaccount = newAssetaccount;
    }

    public String getParentPKFieldName() {
        return null;
    }

    public String getPKFieldName() {
        return "pk_trade_assetcheck";
    }

    public String getTableName() {
        return "ynt_tdcheck";
    }

    public BdTradeAssetCheckVO() {
    }

    public String getAccountcode() {
        return this.accountcode;
    }

    public void setAccountcode(String accountcode) {
        this.accountcode = accountcode;
    }

    public String getPk_zjfykm() {
        return this.pk_zjfykm;
    }

    public void setPk_zjfykm(String pk_zjfykm) {
        this.pk_zjfykm = pk_zjfykm;
    }

    public String getZjfykmcode() {
        return this.zjfykmcode;
    }

    public void setZjfykmcode(String zjfykmcode) {
        this.zjfykmcode = zjfykmcode;
    }

    public String getZjfykmmc() {
        return this.zjfykmmc;
    }

    public void setZjfykmmc(String zjfykmmc) {
        this.zjfykmmc = zjfykmmc;
    }

    public String getPk_jskm() {
        return this.pk_jskm;
    }

    public void setPk_jskm(String pk_jskm) {
        this.pk_jskm = pk_jskm;
    }

    public String getJskmmc() {
        return this.jskmmc;
    }

    public void setJskmmc(String jskmmc) {
        this.jskmmc = jskmmc;
    }

    public String getJskmcode() {
        return this.jskmcode;
    }

    public void setJskmcode(String jskmcode) {
        this.jskmcode = jskmcode;
    }

    public String getZzkmcode() {
        return this.zzkmcode;
    }

    public void setZzkmcode(String zzkmcode) {
        this.zzkmcode = zzkmcode;
    }

    public Integer getZjtype() {
        return this.zjtype;
    }

    public void setZjtype(Integer zjtype) {
        this.zjtype = zjtype;
    }

    public DZFDouble getSalvageratio() {
        return this.salvageratio;
    }

    public void setSalvageratio(DZFDouble salvageratio) {
        this.salvageratio = salvageratio;
    }
}
