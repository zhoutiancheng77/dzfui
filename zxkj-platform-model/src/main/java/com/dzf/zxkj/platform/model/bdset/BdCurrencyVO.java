package com.dzf.zxkj.platform.model.bdset;

import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.model.SuperVO;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 币种
 */
public class BdCurrencyVO extends SuperVO {

    private DZFDateTime ts;
    @JsonProperty("symbol")
    private String currencysymbol;
    @JsonProperty("cpid")
    private String coperatorid;
    @JsonProperty("dr")
    private Integer dr;
    @JsonProperty("dpdate")
    private DZFDate doperatedate;
    @JsonProperty("id")
    private String pk_currency;
    @JsonProperty("code")
    private String currencycode;
    @JsonProperty("name")
    private String currencyname;
    @JsonProperty("precision")
    private Integer precision;
    @JsonProperty("corp")
    private String pk_corp;


    public DZFDateTime getTs() {
        return ts;
    }

    public void setTs(DZFDateTime ts) {
        this.ts = ts;
    }

    public String getCurrencysymbol() {
        return currencysymbol;
    }

    public void setCurrencysymbol(String currencysymbol) {
        this.currencysymbol = currencysymbol;
    }

    public String getCoperatorid() {
        return coperatorid;
    }

    public void setCoperatorid(String coperatorid) {
        this.coperatorid = coperatorid;
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

    public String getPk_currency() {
        return pk_currency;
    }

    public void setPk_currency(String pk_currency) {
        this.pk_currency = pk_currency;
    }

    public String getCurrencycode() {
        return currencycode;
    }

    public void setCurrencycode(String currencycode) {
        this.currencycode = currencycode;
    }

    public String getCurrencyname() {
        return currencyname;
    }

    public void setCurrencyname(String currencyname) {
        this.currencyname = currencyname;
    }

    public Integer getPrecision() {
        return precision;
    }

    public void setPrecision(Integer precision) {
        this.precision = precision;
    }

    public String getPk_corp() {
        return pk_corp;
    }

    public void setPk_corp(String pk_corp) {
        this.pk_corp = pk_corp;
    }

    /**
     * <p>取得父VO主键字段.
     * <p>
     * 创建日期:2014-09-15 17:19:10
     *
     * @return java.lang.String
     */
    public String getParentPKFieldName() {
        return null;
    }

    /**
     * <p>取得表主键.
     * <p>
     * 创建日期:2014-09-15 17:19:10
     *
     * @return java.lang.String
     */
    public String getPKFieldName() {
        return "pk_currency";
    }

    /**
     * <p>返回表名称.
     * <p>
     * 创建日期:2014-09-15 17:19:10
     *
     * @return java.lang.String
     */
    public String getTableName() {
        return "ynt_bd_currency";
    }

    /**
     * 按照默认方式创建构造子.
     * <p>
     * 创建日期:2014-09-15 17:19:10
     */
    public BdCurrencyVO() {
        super();
    }

    @Override
    public String toString() {
        return currencyname;
    }
} 
