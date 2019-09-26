package com.dzf.zxkj.platform.model.tax;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 公司税率税种信息
 *
 * @author weiju
 */
public class CorpTaxInfoVO extends SuperVO {

    @JsonProperty("sz_bm")
    private String taxcode;//税种编码
    @JsonProperty("sz_mc")
    private String taxname;//税种名称
    @JsonProperty("sz_sl")
    private DZFDouble taxrate;//税种税率
    @JsonProperty("sz_sljc")
    private Integer taxbase;//税率基础
    @JsonProperty("sz_mzxse")
    private DZFDouble tax_mzxse;//免征销售额
    @JsonProperty("id")
    private String pk_taxinfo;//主键
    @JsonProperty("gsid")
    private String pk_corp;//公司主键
    private String memo;//备注

    @JsonProperty("sz_sbzq")
    private Integer reptype;  //申报周期 0:按月申报，1:按季申报 2  年报


    public Integer getReptype() {
        return reptype;
    }

    public void setReptype(Integer reptype) {
        this.reptype = reptype;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getMemo() {
        return memo;
    }

    private Integer dr;


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

    public DZFDouble getTaxrate() {
        return taxrate;
    }

    public void setTaxrate(DZFDouble taxrate) {
        this.taxrate = taxrate;
    }


    public Integer getTaxbase() {
        return taxbase;
    }

    public void setTaxbase(Integer taxbase) {
        this.taxbase = taxbase;
    }

    public DZFDouble getTax_mzxse() {
        return tax_mzxse;
    }

    public void setTax_mzxse(DZFDouble tax_mzxse) {
        this.tax_mzxse = tax_mzxse;
    }

    public String getPk_taxinfo() {
        return pk_taxinfo;
    }

    public void setPk_taxinfo(String pk_taxinfo) {
        this.pk_taxinfo = pk_taxinfo;
    }

    public String getPk_corp() {
        return pk_corp;
    }

    public void setPk_corp(String pk_corp) {
        this.pk_corp = pk_corp;
    }

    public Integer getDr() {
        return dr;
    }

    public void setDr(Integer dr) {
        this.dr = dr;
    }

    @Override
    public String getPKFieldName() {
        // TODO Auto-generated method stub
        return "pk_taxinfo";
    }

    @Override
    public String getParentPKFieldName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getTableName() {
        // TODO Auto-generated method stub
        return "bd_taxinfo";
    }


}
