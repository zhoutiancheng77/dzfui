package com.dzf.zxkj.platform.model.am.zcgl;

/**
 * 固定资产折旧模板
 */
public class AssetDepTemplate {
    private String pk_debitAccount;
    private String pk_creditAccount;
    private String debitAbstracts;
    private String creditAbstracts;
    private String debit_subcode;
    private String debit_subname;
    private String credit_subcode;
    private String credit_subname;
    private String pk_account;//科目主键
    private String subcode;//科目编码
    private String subname;//科目名称
    private Integer direct;//方向
    private String abstracts;//摘要
    private Integer accountkind;


    public AssetDepTemplate() {

    }

    public Integer getAccountkind() {
        return accountkind;
    }

    public void setAccountkind(Integer accountkind) {
        this.accountkind = accountkind;
    }

    public String getAbstracts() {
        return abstracts;
    }

    public void setAbstracts(String abstracts) {
        this.abstracts = abstracts;
    }

    public String getPk_account() {
        return pk_account;
    }

    public void setPk_account(String pk_account) {
        this.pk_account = pk_account;
    }

    public String getSubcode() {
        return subcode;
    }

    public void setSubcode(String subcode) {
        this.subcode = subcode;
    }

    public String getSubname() {
        return subname;
    }

    public void setSubname(String subname) {
        this.subname = subname;
    }

    public Integer getDirect() {
        return direct;
    }

    public void setDirect(Integer direct) {
        this.direct = direct;
    }

    public String getPk_debitAccount() {
        return pk_debitAccount;
    }

    public void setPk_debitAccount(String pk_debitAccount) {
        this.pk_debitAccount = pk_debitAccount;
    }

    public String getPk_creditAccount() {
        return pk_creditAccount;
    }

    public void setPk_creditAccount(String pk_creditAccount) {
        this.pk_creditAccount = pk_creditAccount;
    }

    public String getDebitAbstracts() {
        return debitAbstracts;
    }

    public void setDebitAbstracts(String debitAbstracts) {
        this.debitAbstracts = debitAbstracts;
    }

    public String getCreditAbstracts() {
        return creditAbstracts;
    }

    public void setCreditAbstracts(String creditAbstracts) {
        this.creditAbstracts = creditAbstracts;
    }

    public String getDebit_subcode() {
        return debit_subcode;
    }

    public void setDebit_subcode(String debit_subcode) {
        this.debit_subcode = debit_subcode;
    }

    public String getDebit_subname() {
        return debit_subname;
    }

    public void setDebit_subname(String debit_subname) {
        this.debit_subname = debit_subname;
    }

    public String getCredit_subcode() {
        return credit_subcode;
    }

    public void setCredit_subcode(String credit_subcode) {
        this.credit_subcode = credit_subcode;
    }

    public String getCredit_subname() {
        return credit_subname;
    }

    public void setCredit_subname(String credit_subname) {
        this.credit_subname = credit_subname;
    }
}
