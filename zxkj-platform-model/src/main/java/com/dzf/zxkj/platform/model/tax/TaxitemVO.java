package com.dzf.zxkj.platform.model.tax;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 税目信息
 *
 * @author zpm
 */
public class TaxitemVO extends SuperVO {

    @JsonProperty("itemid")
    private String pk_taxitem;
    @JsonProperty("code")
    private String taxcode;
    @JsonProperty("name")
    private String taxname;
    private String pk_corp;
    @JsonProperty("chname")
    public String chargedeptname;// 公司性质
    @JsonProperty("sname")
    private String shortname;
    private String taxstyle;//税率类型[1,收入损益 /////2,资产(库存商品原材料)、损益成本费用]总共两类
    @JsonProperty("shuilv")
    private DZFDouble taxratio;
    private DZFDouble ndef1;
    private DZFDouble ndef2;
    private DZFDouble ndef3;
    private String def1;
    private String def2;
    private String def3;
    private DZFDateTime ts;
    private Integer dr;
    private DZFBoolean isselect;//--不存库
    private Integer iorder;//排序
    private Integer fp_style;// 发票类型
    // (1), 普票（开具的普通发票）
    // (2), 专票（一般人而言是开具的专用发票，小规模为代开的专用发票）
    // 如果为null 为不区分专、普票


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

    public DZFDouble getTaxratio() {
        return taxratio;
    }

    public void setTaxratio(DZFDouble taxratio) {
        this.taxratio = taxratio;
    }

    public DZFDouble getNdef1() {
        return ndef1;
    }

    public void setNdef1(DZFDouble ndef1) {
        this.ndef1 = ndef1;
    }

    public DZFDouble getNdef2() {
        return ndef2;
    }

    public void setNdef2(DZFDouble ndef2) {
        this.ndef2 = ndef2;
    }

    public DZFDouble getNdef3() {
        return ndef3;
    }

    public void setNdef3(DZFDouble ndef3) {
        this.ndef3 = ndef3;
    }

    public String getDef1() {
        return def1;
    }

    public void setDef1(String def1) {
        this.def1 = def1;
    }

    public String getDef2() {
        return def2;
    }

    public void setDef2(String def2) {
        this.def2 = def2;
    }

    public String getDef3() {
        return def3;
    }

    public void setDef3(String def3) {
        this.def3 = def3;
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

    public String getPk_corp() {
        return pk_corp;
    }

    public void setPk_corp(String pk_corp) {
        this.pk_corp = pk_corp;
    }

    public String getTaxstyle() {
        return taxstyle;
    }

    public void setTaxstyle(String taxstyle) {
        this.taxstyle = taxstyle;
    }

    public String getChargedeptname() {
        return chargedeptname;
    }

    public void setChargedeptname(String chargedeptname) {
        this.chargedeptname = chargedeptname;
    }

    public String getShortname() {
        return shortname;
    }

    public void setShortname(String shortname) {
        this.shortname = shortname;
    }

    public DZFBoolean getIsselect() {
        return isselect;
    }

    public void setIsselect(DZFBoolean isselect) {
        this.isselect = isselect;
    }

    public Integer getIorder() {
        return iorder;
    }

    public void setIorder(Integer iorder) {
        this.iorder = iorder;
    }

    public Integer getFp_style() {
        return fp_style;
    }

    public void setFp_style(Integer fp_style) {
        this.fp_style = fp_style;
    }

    @Override
    public String getParentPKFieldName() {
        return null;
    }

    @Override
    public String getPKFieldName() {
        return "pk_taxitem";
    }

    @Override
    public String getTableName() {
        return "ynt_taxitem";
    }
}