package com.dzf.zxkj.platform.model.sys;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ImpErrorVO extends SuperVO {

    private String pk_impinfo;//
    private String pk_corp;//
    private String vcode;//
    private String vname;//
    private DZFDateTime dimptime;//
    private String deptname;//
    private String verror;//
    private String vstatus;//
    private String vtype;//
    private String vmemo;//
    private String coperatorid;//
    private DZFDate doperatedate;//
    @JsonProperty("dr")
    private Integer dr; // 删除标记

    @JsonProperty("ts")
    private DZFDateTime ts; // 时间戳

    public String getPk_impinfo() {
        return pk_impinfo;
    }

    public void setPk_impinfo(String pk_impinfo) {
        this.pk_impinfo = pk_impinfo;
    }

    public String getPk_corp() {
        return pk_corp;
    }

    public void setPk_corp(String pk_corp) {
        this.pk_corp = pk_corp;
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

    public DZFDateTime getDimptime() {
        return dimptime;
    }

    public void setDimptime(DZFDateTime dimptime) {
        this.dimptime = dimptime;
    }

    public String getDeptname() {
        return deptname;
    }

    public void setDeptname(String deptname) {
        this.deptname = deptname;
    }

    public String getVerror() {
        return verror;
    }

    public void setVerror(String verror) {
        this.verror = verror;
    }

    public String getVstatus() {
        return vstatus;
    }

    public void setVstatus(String vstatus) {
        this.vstatus = vstatus;
    }

    public String getVtype() {
        return vtype;
    }

    public void setVtype(String vtype) {
        this.vtype = vtype;
    }

    public String getVmemo() {
        return vmemo;
    }

    public void setVmemo(String vmemo) {
        this.vmemo = vmemo;
    }

    public String getCoperatorid() {
        return coperatorid;
    }

    public void setCoperatorid(String coperatorid) {
        this.coperatorid = coperatorid;
    }

    public DZFDate getDoperatedate() {
        return doperatedate;
    }

    public void setDoperatedate(DZFDate doperatedate) {
        this.doperatedate = doperatedate;
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

    @Override
    public String getParentPKFieldName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getPKFieldName() {
        // TODO Auto-generated method stub
        return "pk_impinfo";
    }

    @Override
    public String getTableName() {
        // TODO Auto-generated method stub
        return "ynt_impinfo";
    }

}
