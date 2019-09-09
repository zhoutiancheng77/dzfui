package com.dzf.zxkj.platform.model.bdset;

import com.dzf.zxkj.base.model.SuperVO;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;


/**
 * 公司期间损益
 */

public class YntCptransmbHVO extends SuperVO {

    @JsonProperty("mainid")
    private String pk_corp_transtemplate_h;
    @JsonProperty("cpid")
    private String pk_corp; // 公司主键;
    @JsonProperty("zdr")
    private String coperatorid;
    @JsonProperty("tzrq")
    private String doperatedate;
    private String ts;
    private Integer dr;
    @JsonProperty("zrkmid")
    private String pk_transferinaccount;
    private String pkcorp;
    @JsonProperty("zy")
    private String abstracts;
    @JsonProperty("bz")
    private String memo;
    @JsonProperty("zrkmbm")
    private String accountcode;
    @JsonProperty("zrkmmc")
    private String accountname;
    private BigDecimal childcount;
    private Integer direction;
    private String vcode;
    private String vname;
    private BigDecimal direct;
    private Integer vlevel;


    public String getAccountname() {
        return accountname;
    }

    public void setAccountname(String accountname) {
        this.accountname = accountname;
    }

    public String getPk_corp() {
        return pk_corp;
    }

    public void setPk_corp(String pk_corp) {
        this.pk_corp = pk_corp;
    }

    @Override
    public String getParentPKFieldName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getPKFieldName() {
        return "pk_corp_transtemplate_h";
    }

    @Override
    public String getTableName() {
        // TODO Auto-generated method stub
        return "ynt_cptransmb";
    }

    public String getPk_corp_transtemplate_h() {
        return pk_corp_transtemplate_h;
    }

    public void setPk_corp_transtemplate_h(String pk_corp_transtemplate_h) {
        this.pk_corp_transtemplate_h = pk_corp_transtemplate_h;
    }

    public String getCoperatorid() {
        return coperatorid;
    }

    public void setCoperatorid(String coperatorid) {
        this.coperatorid = coperatorid;
    }

    public String getDoperatedate() {
        return doperatedate;
    }

    public void setDoperatedate(String doperatedate) {
        this.doperatedate = doperatedate;
    }

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }

    public Integer getDr() {
        return dr;
    }

    public void setDr(Integer dr) {
        this.dr = dr;
    }


    public String getPk_transferinaccount() {
        return pk_transferinaccount;
    }

    public void setPk_transferinaccount(String pk_transferinaccount) {
        this.pk_transferinaccount = pk_transferinaccount;
    }

    public String getPkcorp() {
        return pkcorp;
    }

    public void setPkcorp(String pkcorp) {
        this.pkcorp = pkcorp;
    }

    public String getAbstracts() {
        return abstracts;
    }

    public void setAbstracts(String abstracts) {
        this.abstracts = abstracts;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getAccountcode() {
        return accountcode;
    }

    public void setAccountcode(String accountcode) {
        this.accountcode = accountcode;
    }

    public BigDecimal getChildcount() {
        return childcount;
    }

    public void setChildcount(BigDecimal childcount) {
        this.childcount = childcount;
    }

    public Integer getDirection() {
        return direction;
    }

    public void setDirection(Integer direction) {
        this.direction = direction;
    }

    public String getVcode() {
        return vcode;
    }

    public void setVcode(String vcode) {
        this.vcode = vcode;
    }

    public String getVname() {
        return vname;
    }

    public void setVname(String vname) {
        this.vname = vname;
    }

    public BigDecimal getDirect() {
        return direct;
    }

    public void setDirect(BigDecimal direct) {
        this.direct = direct;
    }

    public Integer getVlevel() {
        return vlevel;
    }

    public void setVlevel(Integer vlevel) {
        this.vlevel = vlevel;
    }


}