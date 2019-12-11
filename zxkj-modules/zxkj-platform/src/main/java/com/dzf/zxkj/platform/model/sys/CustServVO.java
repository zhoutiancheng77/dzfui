package com.dzf.zxkj.platform.model.sys;

import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.model.SuperVO;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CustServVO extends SuperVO {

    @JsonProperty("pk_gs")
    public String pk_corp;

    private DZFBoolean ischannel;

    @JsonProperty("uname")
    private String unitname;

    @JsonProperty("isfactory")
    public DZFBoolean isfactory;// 是否会计工厂

    @JsonProperty("chtype")
    private Integer channeltype;// 加盟类型 1-普通加盟商；2-金牌加盟商

    private Integer iversion;//版本

    @JsonProperty("uid")
    private String cuserid;

    public String getCuserid() {
        return cuserid;
    }

    public void setCuserid(String cuserid) {
        this.cuserid = cuserid;
    }

    public String getUnitname() {
        return unitname;
    }

    public void setUnitname(String unitname) {
        this.unitname = unitname;
    }

    public Integer getChanneltype() {
        return channeltype;
    }

    public void setChanneltype(Integer channeltype) {
        this.channeltype = channeltype;
    }

    public DZFBoolean getIsfactory() {
        return isfactory;
    }

    public void setIsfactory(DZFBoolean isfactory) {
        this.isfactory = isfactory;
    }

    public CustServVO() {
    }

    public String getEntityName() {
        return "Corp";
    }

    public String getParentPKFieldName() {
        return null;
    }

    public String getPk_corp() {
        return pk_corp;
    }

    public String getPKFieldName() {
        return "pk_corp";
    }

    public String getTableName() {
        return null;
    }

    public DZFBoolean getIschannel() {
        return ischannel;
    }

    public void setIschannel(DZFBoolean ischannel) {
        this.ischannel = ischannel;
    }

    public void setPk_corp(String pk_corp) {
        this.pk_corp = pk_corp;
    }

    public Integer getIversion() {
        return iversion;
    }

    public void setIversion(Integer iversion) {
        this.iversion = iversion;
    }
}
