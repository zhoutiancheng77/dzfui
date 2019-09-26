package com.dzf.zxkj.platform.model.jzcl;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;

public class SurTaxTemplate extends SuperVO {
    private String pk_template;
    private String pk_archive;
    private String pk_debit_subject;
    private String pk_credit_subject;
    // 计税依据
    private String pk_base_subject;
    private String summary;
    private DZFDouble rate;
    private Integer period_type;
    private Integer dr;
    private String pk_corp;
    private DZFDateTime ts;

    private String tax_code;
    private String tax_name;
    private String debit_subject_code;
    private String debit_subject_name;
    private String credit_subject_code;
    private String credit_subject_name;

    public String getPk_template() {
        return pk_template;
    }

    public void setPk_template(String pk_template) {
        this.pk_template = pk_template;
    }

    public String getPk_archive() {
        return pk_archive;
    }

    public void setPk_archive(String pk_archive) {
        this.pk_archive = pk_archive;
    }

    public String getPk_debit_subject() {
        return pk_debit_subject;
    }

    public void setPk_debit_subject(String pk_debit_subject) {
        this.pk_debit_subject = pk_debit_subject;
    }

    public String getPk_credit_subject() {
        return pk_credit_subject;
    }

    public void setPk_credit_subject(String pk_credit_subject) {
        this.pk_credit_subject = pk_credit_subject;
    }

    public String getPk_base_subject() {
        return pk_base_subject;
    }

    public void setPk_base_subject(String pk_base_subject) {
        this.pk_base_subject = pk_base_subject;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public DZFDouble getRate() {
        return rate;
    }

    public void setRate(DZFDouble rate) {
        this.rate = rate;
    }

    public Integer getPeriod_type() {
        return period_type;
    }

    public void setPeriod_type(Integer period_type) {
        this.period_type = period_type;
    }

    public Integer getDr() {
        return dr;
    }

    public void setDr(Integer dr) {
        this.dr = dr;
    }

    public String getPk_corp() {
        return pk_corp;
    }

    public void setPk_corp(String pk_corp) {
        this.pk_corp = pk_corp;
    }

    public DZFDateTime getTs() {
        return ts;
    }

    public void setTs(DZFDateTime ts) {
        this.ts = ts;
    }

    public String getTax_code() {
        return tax_code;
    }

    public void setTax_code(String tax_code) {
        this.tax_code = tax_code;
    }

    public String getTax_name() {
        return tax_name;
    }

    public void setTax_name(String tax_name) {
        this.tax_name = tax_name;
    }

    public String getDebit_subject_code() {
        return debit_subject_code;
    }

    public void setDebit_subject_code(String debit_subject_code) {
        this.debit_subject_code = debit_subject_code;
    }

    public String getDebit_subject_name() {
        return debit_subject_name;
    }

    public void setDebit_subject_name(String debit_subject_name) {
        this.debit_subject_name = debit_subject_name;
    }

    public String getCredit_subject_code() {
        return credit_subject_code;
    }

    public void setCredit_subject_code(String credit_subject_code) {
        this.credit_subject_code = credit_subject_code;
    }

    public String getCredit_subject_name() {
        return credit_subject_name;
    }

    public void setCredit_subject_name(String credit_subject_name) {
        this.credit_subject_name = credit_subject_name;
    }

    @Override
    public String getParentPKFieldName() {
        return null;
    }

    @Override
    public String getPKFieldName() {
        return "pk_template";
    }

    @Override
    public String getTableName() {
        return "ynt_taxcal_surtax_template";
    }
}
