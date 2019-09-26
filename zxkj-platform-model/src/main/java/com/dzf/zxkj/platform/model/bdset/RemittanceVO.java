package com.dzf.zxkj.platform.model.bdset;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Entity;

/**
 * 汇兑损益
 *
 * @author liangyi
 */

@Entity
public class RemittanceVO extends SuperVO {

    private static final long serialVersionUID = 1L;
    private DZFDateTime ts;
    @JsonProperty("remtt_id")
    private String pk_remittance;
    @JsonProperty("copid")
    private String coperatorid;
    @JsonProperty("corpacc_id")
    private String pk_corp_account;
    @JsonProperty("acccd")
    private String accountcode;
    @JsonProperty("outacc_id")
    private String pk_out_account;
    @JsonProperty("outcd")
    private String outatcode;
    private Integer dr;
    @JsonProperty("dopdate")
    private DZFDate doperatedate;
    @JsonProperty("corp_id")
    private String pk_corp;
    @JsonProperty("mark")
    private String memo;
    @JsonProperty("accname")
    private String pk_corp_account_name;
    @JsonProperty("outaccname")
    private String pk_out_account_name;
    @JsonProperty("coporname")
    private String coperatorname;
    @JsonProperty("hyid")
    private String pk_trade_accountschema;//行业

    public String getPk_trade_accountschema() {
        return pk_trade_accountschema;
    }

    public void setPk_trade_accountschema(String pk_trade_accountschema) {
        this.pk_trade_accountschema = pk_trade_accountschema;
    }

    public String getPk_out_account_name() {
        return pk_out_account_name;
    }

    public void setPk_out_account_name(String pk_out_account_name) {
        this.pk_out_account_name = pk_out_account_name;
    }

    public String getCoperatorname() {
        return coperatorname;
    }

    public void setCoperatorname(String coperatorname) {
        this.coperatorname = coperatorname;
    }

    public String getPk_corp_account_name() {
        return pk_corp_account_name;
    }

    public void setPk_corp_account_name(String pk_corp_account_name) {
        this.pk_corp_account_name = pk_corp_account_name;
    }

    public DZFDateTime getTs() {
        return ts;
    }

    public void setTs(DZFDateTime ts) {
        this.ts = ts;
    }

    public String getPk_remittance() {
        return pk_remittance;
    }

    public void setPk_remittance(String pk_remittance) {
        this.pk_remittance = pk_remittance;
    }

    public String getCoperatorid() {
        return coperatorid;
    }

    public void setCoperatorid(String coperatorid) {
        this.coperatorid = coperatorid;
    }

    public String getPk_corp_account() {
        return pk_corp_account;
    }

    public void setPk_corp_account(String pk_corp_account) {
        this.pk_corp_account = pk_corp_account;
    }

    public String getAccountcode() {
        return accountcode;
    }

    public void setAccountcode(String accountcode) {
        this.accountcode = accountcode;
    }

    public String getPk_out_account() {
        return pk_out_account;
    }

    public void setPk_out_account(String pk_out_account) {
        this.pk_out_account = pk_out_account;
    }

    public String getOutatcode() {
        return outatcode;
    }

    public void setOutatcode(String outatcode) {
        this.outatcode = outatcode;
    }

    public Integer getDr() {
        return dr;
    }

    public void setDr(Integer dr) {
        this.dr = dr;
    }

    public DZFDate getDoperatedate() {
        return doperatedate;
    }

    public void setDoperatedate(DZFDate doperatedate) {
        this.doperatedate = doperatedate;
    }

    public String getPk_corp() {
        return pk_corp;
    }

    public void setPk_corp(String pk_corp) {
        this.pk_corp = pk_corp;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    @Override
    public String getParentPKFieldName() {
        return null;
    }

    @Override
    public String getPKFieldName() {
        return "pk_remittance";
    }

    @Override
    public String getTableName() {
        return "ynt_remittance";
    }

}
