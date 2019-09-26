package com.dzf.zxkj.platform.model.sys;

import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * 股东及出资信息
 *
 */
public class CorpSholderVO extends SuperVO {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @JsonProperty("sholderid")
    private String pk_corpsholder; // 主键

    @JsonProperty("corpid")
    private String pk_corp; // 客户主键

    @JsonProperty("vshname")
    private String vshname; // 股东名称

    @JsonProperty("subtype")
    private Integer isubtype; // 认缴出资方式

    @JsonProperty("subscription")
    private DZFDouble nsubscription;// 认缴出资金额

    @JsonProperty("subdate")
    private DZFDate dsubdate; // 认缴出资日期

    @JsonProperty("paidtype")
    private Integer ipaidtype; // 实缴出资方式

    @JsonProperty("paidamount")
    private DZFDouble npaidamount;// 实缴出资金额

    @JsonProperty("paiddate")
    private DZFDate dpaiddate; // 实缴出资日期

    private Integer dr; // 删除标记

    private DZFDateTime ts; // 时间

    public String getPk_corpsholder() {
        return pk_corpsholder;
    }

    public void setPk_corpsholder(String pk_corpsholder) {
        this.pk_corpsholder = pk_corpsholder;
    }

    public String getVshname() {
        return vshname;
    }

    public void setVshname(String vshname) {
        this.vshname = vshname;
    }

    public Integer getIsubtype() {
        return isubtype;
    }

    public void setIsubtype(Integer isubtype) {
        this.isubtype = isubtype;
    }

    public DZFDouble getNsubscription() {
        return nsubscription;
    }

    public void setNsubscription(DZFDouble nsubscription) {
        this.nsubscription = nsubscription;
    }

    public DZFDate getDsubdate() {
        return dsubdate;
    }

    public void setDsubdate(DZFDate dsubdate) {
        this.dsubdate = dsubdate;
    }

    public Integer getIpaidtype() {
        return ipaidtype;
    }

    public void setIpaidtype(Integer ipaidtype) {
        this.ipaidtype = ipaidtype;
    }

    public DZFDouble getNpaidamount() {
        return npaidamount;
    }

    public void setNpaidamount(DZFDouble npaidamount) {
        this.npaidamount = npaidamount;
    }

    public DZFDate getDpaiddate() {
        return dpaiddate;
    }

    public void setDpaiddate(DZFDate dpaiddate) {
        this.dpaiddate = dpaiddate;
    }

    public Integer getDr() {
        return dr;
    }

    public void setDr(Integer dr) {
        this.dr = dr;
    }

    public DZFDateTime getTs() {
        return ts;
    }

    public void setTs(DZFDateTime ts) {
        this.ts = ts;
    }

    public String getPk_corp() {
        return pk_corp;
    }

    public void setPk_corp(String pk_corp) {
        this.pk_corp = pk_corp;
    }

    @Override
    public String getParentPKFieldName() {
        return "pk_corp";
    }

    @Override
    public String getPKFieldName() {
        return "pk_corpsholder";
    }

    @Override
    public String getTableName() {
        return "ynt_corpsholder";
    }
}
