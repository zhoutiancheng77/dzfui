package com.dzf.zxkj.platform.model.bill;

import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.model.SuperVO;
import com.fasterxml.jackson.annotation.JsonProperty;

public class InvoiceApplyVO extends SuperVO {

    @JsonProperty("id")
    private String pk_invoice_apply;//主键
    @JsonProperty("pk_gs")
    private String pk_corp;//
    @JsonProperty("userid")
    private String cuserid;//操作人
    @JsonProperty("cdate")
    private DZFDate createdate;//创建时间
    @JsonProperty("mdate")
    private DZFDateTime modifydatetime;//修改时间
    @JsonProperty("status")
    private Integer istatus;//状态
    @JsonProperty("mode")
    private Integer imode;//开通模式 0 自开服务
    @JsonProperty("bz")
    private String memo;//备注

    private DZFDateTime ts;// ts
    private Integer dr;// dr
    public String getPk_invoice_apply() {
        return pk_invoice_apply;
    }

    public void setPk_invoice_apply(String pk_invoice_apply) {
        this.pk_invoice_apply = pk_invoice_apply;
    }

    public String getPk_corp() {
        return pk_corp;
    }

    public void setPk_corp(String pk_corp) {
        this.pk_corp = pk_corp;
    }

    public String getCuserid() {
        return cuserid;
    }

    public void setCuserid(String cuserid) {
        this.cuserid = cuserid;
    }

    public DZFDate getCreatedate() {
        return createdate;
    }

    public void setCreatedate(DZFDate createdate) {
        this.createdate = createdate;
    }

    public DZFDateTime getModifydatetime() {
        return modifydatetime;
    }

    public void setModifydatetime(DZFDateTime modifydatetime) {
        this.modifydatetime = modifydatetime;
    }

    public Integer getIstatus() {
        return istatus;
    }

    public void setIstatus(Integer istatus) {
        this.istatus = istatus;
    }

    public Integer getImode() {
        return imode;
    }

    public void setImode(Integer imode) {
        this.imode = imode;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
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
        return "pk_invoice_apply";
    }

    @Override
    public String getTableName() {
        return "ynt_invoice_apply";
    }
}
