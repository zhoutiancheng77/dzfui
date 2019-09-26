package com.dzf.zxkj.platform.model.tax;


import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;

public class SurtaxArchiveVO extends SuperVO {
    private String pk_archive;
    // 编码
    private String tax_code;
    // 税名
    private String tax_name;
    // 税率
    private DZFDouble rate;
    // 是否为附加税
    private Boolean is_surtax;
    // 是否预置
    private Boolean is_preset;
    // 顺序
    private Integer show_order;
    private String pk_corp;
    private DZFDateTime ts;
    private Integer dr;

    public String getPk_archive() {
        return pk_archive;
    }

    public void setPk_archive(String pk_archive) {
        this.pk_archive = pk_archive;
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

    public DZFDouble getRate() {
        return rate;
    }

    public void setRate(DZFDouble rate) {
        this.rate = rate;
    }

    public Boolean getIs_surtax() {
        return is_surtax;
    }

    public void setIs_surtax(Boolean is_surtax) {
        this.is_surtax = is_surtax;
    }

    public Boolean getIs_preset() {
        return is_preset;
    }

    public void setIs_preset(Boolean is_preset) {
        this.is_preset = is_preset;
    }

    public Integer getShow_order() {
        return show_order;
    }

    public void setShow_order(Integer show_order) {
        this.show_order = show_order;
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

    public Integer getDr() {
        return dr;
    }

    public void setDr(Integer dr) {
        this.dr = dr;
    }

    @Override
    public String getParentPKFieldName() {
        return null;
    }

    @Override
    public String getPKFieldName() {
        return "pk_tax";
    }

    @Override
    public String getTableName() {
        return "ynt_taxcal_surtaxarchive";
    }
}
