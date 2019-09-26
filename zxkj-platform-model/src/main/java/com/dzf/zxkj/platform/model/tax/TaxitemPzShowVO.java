package com.dzf.zxkj.platform.model.tax;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TaxitemPzShowVO extends SuperVO {

    @JsonProperty("itemid")
    private String pk_taxitem;
    @JsonProperty("code")
    private String taxcode;
    @JsonProperty("name")
    private String taxname;
    private String pk_corp;
    @JsonProperty("sname")
    private String shortname;
    @JsonProperty("shuilv")
    private DZFDouble taxratio;
    //科目编码
    private String subj_code;
    //科目id
    private String pk_accsubj;

    private Integer iorder;//排序

    //是否凭证显示。
    private DZFBoolean shuimushowpz;//是否显示在凭证上.

    public String getPk_taxitem() {
        return pk_taxitem;
    }

    public void setPk_taxitem(String pk_taxitem) {
        this.pk_taxitem = pk_taxitem;
    }

    public String getTaxcode() {
        return taxcode;
    }

    public void setTaxcode(String taxcode) {
        this.taxcode = taxcode;
    }

    public String getTaxname() {
        return taxname;
    }

    public void setTaxname(String taxname) {
        this.taxname = taxname;
    }

    public String getPk_corp() {
        return pk_corp;
    }

    public void setPk_corp(String pk_corp) {
        this.pk_corp = pk_corp;
    }

    public String getShortname() {
        return shortname;
    }

    public void setShortname(String shortname) {
        this.shortname = shortname;
    }

    public DZFDouble getTaxratio() {
        return taxratio;
    }

    public void setTaxratio(DZFDouble taxratio) {
        this.taxratio = taxratio;
    }

    public String getSubj_code() {
        return subj_code;
    }

    public void setSubj_code(String subj_code) {
        this.subj_code = subj_code;
    }

    public String getPk_accsubj() {
        return pk_accsubj;
    }

    public void setPk_accsubj(String pk_accsubj) {
        this.pk_accsubj = pk_accsubj;
    }

    public Integer getIorder() {
        return iorder;
    }

    public void setIorder(Integer iorder) {
        this.iorder = iorder;
    }

    public DZFBoolean getShuimushowpz() {
        return shuimushowpz;
    }

    public void setShuimushowpz(DZFBoolean shuimushowpz) {
        this.shuimushowpz = shuimushowpz;
    }

    @Override
    public String getParentPKFieldName() {
        return null;
    }

    @Override
    public String getPKFieldName() {
        return null;
    }

    @Override
    public String getTableName() {
        return null;
    }
}