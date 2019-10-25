package com.dzf.zxkj.platform.model.qcset;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.fasterxml.jackson.annotation.JsonProperty;

public class YntXjllqcyePageVO extends SuperVO {

    @JsonProperty("id")
    private String pk_xjllqcye;
    @JsonProperty("djh")
    private String vbillno;
    @JsonProperty("gsid")
    private String pk_corp;
    @JsonProperty("cpsn")
    private String coperatorid;
    @JsonProperty("dopdate")
    private String doperatedate;
    @JsonProperty("vbillzt")
    private Integer vbillstatus;
    @JsonProperty("sprid")
    private String vapproveid;
    @JsonProperty("sprq")
    private String dapprovedate;
    @JsonProperty("spbz")
    private String vapprovenote;
    @JsonProperty("djlxid")
    private String pk_billtype;
    @JsonProperty("extime")
    private String ts;
    private Integer dr;
    @JsonProperty("xjlxmid")
    private String pk_project;
    @JsonProperty("xmmc")
    private String projectname;    //名称
    @JsonProperty("xjlxmbm")
    private String vcode;
    @JsonProperty("xjlxmmc")
    private String vname;
    @JsonProperty("fx")
    private Integer vdirect;
    @JsonProperty("bz")
    private String dcurrency;
    @JsonProperty("je")
    private DZFDouble nmny;

    /**
     * 四个季度金额
     * 这版增加的期初拆分拿掉 2019-07-18 modify by  gzx 产品需求变更
     */
    private DZFDouble q1 = DZFDouble.ZERO_DBL; //废弃 不使用
    private DZFDouble q2 = DZFDouble.ZERO_DBL;//废弃 不使用
    private DZFDouble q3 = DZFDouble.ZERO_DBL;//废弃 不使用
    private DZFDouble q4 = DZFDouble.ZERO_DBL;//废弃 不使用
    private String isSet;//废弃 不使用

    public String getIsSet() {
        return isSet;
    }

    public void setIsSet(String isSet) {
        this.isSet = isSet;
    }

    @JsonProperty("v1")
    private String vdef1;
    @JsonProperty("v2")
    private String vdef2;
    @JsonProperty("v3")
    private String vdef3;
    @JsonProperty("v4")
    private String vdef4;
    @JsonProperty("v5")
    private String vdef5;
    @JsonProperty("v6")
    private String vdef6;
    @JsonProperty("v7")
    private String vdef7;
    @JsonProperty("v8")
    private String vdef8;
    @JsonProperty("v9")
    private String vdef9;
    @JsonProperty("v10")
    private String vdef10;

    public DZFDouble getQ1() {
        return q1;
    }

    public void setQ1(DZFDouble q1) {
        this.q1 = q1;
    }

    public DZFDouble getQ2() {
        return q2;
    }

    public void setQ2(DZFDouble q2) {
        this.q2 = q2;
    }

    public DZFDouble getQ3() {
        return q3;
    }

    public void setQ3(DZFDouble q3) {
        this.q3 = q3;
    }

    public DZFDouble getQ4() {
        return q4;
    }

    public void setQ4(DZFDouble q4) {
        this.q4 = q4;
    }

    public String getProjectname() {
        return projectname;
    }

    public void setProjectname(String projectname) {
        this.projectname = projectname;
    }

    public String getPk_xjllqcye() {
        return pk_xjllqcye;
    }

    public void setPk_xjllqcye(String pk_xjllqcye) {
        this.pk_xjllqcye = pk_xjllqcye;
    }

    public String getVbillno() {
        return vbillno;
    }

    public void setVbillno(String vbillno) {
        this.vbillno = vbillno;
    }

    public String getPk_corp() {
        return pk_corp;
    }

    public void setPk_corp(String pk_corp) {
        this.pk_corp = pk_corp;
    }

    public String getCoperatorid() {
        return coperatorid;
    }

    public void setCoperatorid(String coperatorid) {
        this.coperatorid = coperatorid;
    }

    public String getDoperatedate() {
        return doperatedate;
    }

    public void setDoperatedate(String doperatedate) {
        this.doperatedate = doperatedate;
    }

    public Integer getVbillstatus() {
        return vbillstatus;
    }

    public void setVbillstatus(Integer vbillstatus) {
        this.vbillstatus = vbillstatus;
    }

    public String getVapproveid() {
        return vapproveid;
    }

    public void setVapproveid(String vapproveid) {
        this.vapproveid = vapproveid;
    }

    public String getDapprovedate() {
        return dapprovedate;
    }

    public void setDapprovedate(String dapprovedate) {
        this.dapprovedate = dapprovedate;
    }

    public String getVapprovenote() {
        return vapprovenote;
    }

    public void setVapprovenote(String vapprovenote) {
        this.vapprovenote = vapprovenote;
    }

    public String getPk_billtype() {
        return pk_billtype;
    }

    public void setPk_billtype(String pk_billtype) {
        this.pk_billtype = pk_billtype;
    }

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }

    public Integer getDr() {
        return dr;
    }

    public void setDr(Integer dr) {
        this.dr = dr;
    }

    public String getPk_project() {
        return pk_project;
    }

    public void setPk_project(String pk_project) {
        this.pk_project = pk_project;
    }

    public String getVcode() {
        return vcode;
    }

    public void setVcode(String vcode) {
        this.vcode = vcode;
    }

    public String getVname() {
        return vname;
    }

    public void setVname(String vname) {
        this.vname = vname;
    }

    public Integer getVdirect() {
        return vdirect;
    }

    public void setVdirect(Integer vdirect) {
        this.vdirect = vdirect;
    }

    public String getDcurrency() {
        return dcurrency;
    }

    public void setDcurrency(String dcurrency) {
        this.dcurrency = dcurrency;
    }

    public DZFDouble getNmny() {
        return nmny;
    }

    public void setNmny(DZFDouble nmny) {
        this.nmny = nmny;
    }

    public String getVdef1() {
        return vdef1;
    }

    public void setVdef1(String vdef1) {
        this.vdef1 = vdef1;
    }

    public String getVdef2() {
        return vdef2;
    }

    public void setVdef2(String vdef2) {
        this.vdef2 = vdef2;
    }

    public String getVdef3() {
        return vdef3;
    }

    public void setVdef3(String vdef3) {
        this.vdef3 = vdef3;
    }

    public String getVdef4() {
        return vdef4;
    }

    public void setVdef4(String vdef4) {
        this.vdef4 = vdef4;
    }

    public String getVdef5() {
        return vdef5;
    }

    public void setVdef5(String vdef5) {
        this.vdef5 = vdef5;
    }

    public String getVdef6() {
        return vdef6;
    }

    public void setVdef6(String vdef6) {
        this.vdef6 = vdef6;
    }

    public String getVdef7() {
        return vdef7;
    }

    public void setVdef7(String vdef7) {
        this.vdef7 = vdef7;
    }

    public String getVdef8() {
        return vdef8;
    }

    public void setVdef8(String vdef8) {
        this.vdef8 = vdef8;
    }

    public String getVdef9() {
        return vdef9;
    }

    public void setVdef9(String vdef9) {
        this.vdef9 = vdef9;
    }

    public String getVdef10() {
        return vdef10;
    }

    public void setVdef10(String vdef10) {
        this.vdef10 = vdef10;
    }

    @Override
    public String getParentPKFieldName() {
        return null;
    }

    @Override
    public String getPKFieldName() {
        return "pk_xjllqcye";
    }

    @Override
    public String getTableName() {
        return "ynt_xjllqcye";
    }

}
