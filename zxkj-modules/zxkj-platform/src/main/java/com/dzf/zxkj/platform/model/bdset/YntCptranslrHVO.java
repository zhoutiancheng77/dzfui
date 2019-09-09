package com.dzf.zxkj.platform.model.bdset;

import com.dzf.zxkj.base.model.SuperVO;
import com.fasterxml.jackson.annotation.JsonProperty;

/*
 * 利润结转模版vo
 */
public class YntCptranslrHVO extends SuperVO {
    @JsonProperty("mainid")
    private String pk_corp_translr_h;
    private String pk_corp; // 公司主键;
    @JsonProperty("zdr")
    private String coperatorid;
    @JsonProperty("tzrq")
    private String doperatedate;
    private String ts;
    private Integer dr;
    @JsonProperty("zrkm_id")
    private String pk_transferinaccount;
    @JsonProperty("zckm_id")
    private String pk_transferoutaccount;
    // private String pkcorp;
    @JsonProperty("zy")
    private String abstracts;
    @JsonProperty("bz")
    private String memo;
    @JsonProperty("zrkmbm")
    private String accountcode;
    @JsonProperty("zrkm")
    private String accountname;
    // private BigDecimal childcount;
    private Integer indirect;
    @JsonProperty("zckmbm")
    private String vcode;
    @JsonProperty("zckm")
    private String vname;
    private Integer outdirect;
    //private Integer vlevel;
    @JsonProperty("zyc")
    private String abstracts1;
    @JsonProperty("bzc")
    private String memo1;

    public String getAbstracts1() {
        return abstracts1;
    }

    public void setAbstracts1(String abstracts1) {
        this.abstracts1 = abstracts1;
    }

    public String getMemo1() {
        return memo1;
    }

    public void setMemo1(String memo1) {
        this.memo1 = memo1;
    }

    public String getPk_corp_translr_h() {
        return pk_corp_translr_h;
    }

    public void setPk_corp_translr_h(String pk_corp_translr_h) {
        this.pk_corp_translr_h = pk_corp_translr_h;
    }

    public String getPk_corp() {
        return pk_corp;
    }

    public void setPk_corp(String pk_corp) {
        this.pk_corp = pk_corp;
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

    public String getPk_transferoutaccount() {
        return pk_transferoutaccount;
    }

    public void setPk_transferoutaccount(String pk_transferoutaccount) {
        this.pk_transferoutaccount = pk_transferoutaccount;
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

    public String getAccountname() {
        return accountname;
    }

    public void setAccountname(String accountname) {
        this.accountname = accountname;
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

    @Override
    public String getParentPKFieldName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getPKFieldName() {
        // TODO Auto-generated method stub
        return "pk_corp_translr_h";
    }

    @Override
    public String getTableName() {
        // TODO Auto-generated method stub
        return "ynt_cptranslr";
    }

    public Integer getIndirect() {
        return indirect;
    }

    public void setIndirect(Integer indirect) {
        this.indirect = indirect;
    }

    public Integer getOutdirect() {
        return outdirect;
    }

    public void setOutdirect(Integer outdirect) {
        this.outdirect = outdirect;
    }


}
