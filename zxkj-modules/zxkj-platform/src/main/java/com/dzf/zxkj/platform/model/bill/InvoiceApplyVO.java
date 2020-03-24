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

    @JsonProperty("uname")
    private String unitname;
    @JsonProperty("incode")
    private String innercode;
    @JsonProperty("fcorp")
    public String fathercorp;

    @JsonProperty("ccrecode")
    public String vsoccrecode;// 纳税人识别号

    @JsonProperty("bodycode")
    public String legalbodycode;// 法人代表

    @JsonProperty("l2")
    public String linkman2;// 客户联系人

    @JsonProperty("e1")
    public String email1;// 电子邮件1

    @JsonProperty("p1")
    public String phone1;// 联系人电话

    @JsonProperty("ovince")
    public Integer vprovince;// 省 所属地区

    public String vprovname;//  省 所属地区名称
    public String vprovcode;//  对应 地区编码

    @JsonProperty("iftype")
    private Integer filetype;//

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

    public String getVprovname() {
        return vprovname;
    }

    public void setVprovname(String vprovname) {
        this.vprovname = vprovname;
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

    public String getUnitname() {
        return unitname;
    }

    public void setUnitname(String unitname) {
        this.unitname = unitname;
    }

    public String getInnercode() {
        return innercode;
    }

    public void setInnercode(String innercode) {
        this.innercode = innercode;
    }

    public String getFathercorp() {
        return fathercorp;
    }

    public void setFathercorp(String fathercorp) {
        this.fathercorp = fathercorp;
    }

    public String getVsoccrecode() {
        return vsoccrecode;
    }

    public void setVsoccrecode(String vsoccrecode) {
        this.vsoccrecode = vsoccrecode;
    }

    public String getLegalbodycode() {
        return legalbodycode;
    }

    public void setLegalbodycode(String legalbodycode) {
        this.legalbodycode = legalbodycode;
    }

    public String getLinkman2() {
        return linkman2;
    }

    public void setLinkman2(String linkman2) {
        this.linkman2 = linkman2;
    }

    public String getEmail1() {
        return email1;
    }

    public void setEmail1(String email1) {
        this.email1 = email1;
    }

    public String getPhone1() {
        return phone1;
    }

    public void setPhone1(String phone1) {
        this.phone1 = phone1;
    }

    public Integer getVprovince() {
        return vprovince;
    }

    public void setVprovince(Integer vprovince) {
        this.vprovince = vprovince;
    }

    public String getVprovcode() {
        return vprovcode;
    }

    public void setVprovcode(String vprovcode) {
        this.vprovcode = vprovcode;
    }

    public Integer getFiletype() {
        return filetype;
    }

    public void setFiletype(Integer filetype) {
        this.filetype = filetype;
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
