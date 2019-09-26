package com.dzf.zxkj.platform.model.sys;

import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDate;

public class DatatruansVO extends SuperVO {
    private String pk_corp;
    private String pk_funnode;
    private String fathercorp;
    private Integer usercount;
    private String versiontype;
    private DZFDate duedate;//
    
    public DZFDate getDuedate() {
        return duedate;
    }

    public void setDuedate(DZFDate duedate) {
        this.duedate = duedate;
    }

    public String getVersiontype() {
        return versiontype;
    }

    public void setVersiontype(String versiontype) {
        this.versiontype = versiontype;
    }

    public Integer getUsercount() {
        return usercount;
    }

    public void setUsercount(Integer usercount) {
        this.usercount = usercount;
    }

    public String getFathercorp() {
        return fathercorp;
    }

    public void setFathercorp(String fathercorp) {
        this.fathercorp = fathercorp;
    }

    public String getPk_funnode() {
        return pk_funnode;
    }

    public void setPk_funnode(String pk_funnode) {
        this.pk_funnode = pk_funnode;
    }

    public String getPk_corp() {
        return pk_corp;
    }

    public void setPk_corp(String pk_corp) {
        this.pk_corp = pk_corp;
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
