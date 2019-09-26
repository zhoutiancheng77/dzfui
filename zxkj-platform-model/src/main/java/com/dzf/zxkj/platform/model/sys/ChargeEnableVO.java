package com.dzf.zxkj.platform.model.sys;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 收费启动设置
 * Created by shiyan on 2017/10/25.
 */
public class ChargeEnableVO extends SuperVO {

    @JsonProperty("pk")
    private String pk_chargeenable; //主键

    @JsonProperty("ccdate")
    private DZFDate coperatordate; //启动日期

    @JsonProperty("ccid")
    private String coperatorid; //设置人id

    @JsonProperty("ccname")
    private String coperatorname; //设置人名称

    @JsonProperty("chtype")
    private String chargetype; //收费产品类型

    @JsonProperty("chname")
    private String chargename; //收费产品名称

    public String getChargetype() {
        return chargetype;
    }

    public void setChargetype(String chargetype) {
        this.chargetype = chargetype;
    }

    public String getChargename() {
        return chargename;
    }

    public void setChargename(String chargename) {
        this.chargename = chargename;
    }
    
    private Integer dr;

    private DZFDateTime ts;

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

    public String getPk_chargeenable() {
        return pk_chargeenable;
    }

    public void setPk_chargeenable(String pk_chargeenable) {
        this.pk_chargeenable = pk_chargeenable;
    }

    public DZFDate getCoperatordate() {
        return coperatordate;
    }

    public void setCoperatordate(DZFDate coperatordate) {
        this.coperatordate = coperatordate;
    }

    public String getCoperatorid() {
        return coperatorid;
    }

    public void setCoperatorid(String coperatorid) {
        this.coperatorid = coperatorid;
    }

    public String getCoperatorname() {
        return coperatorname;
    }

    public void setCoperatorname(String coperatorname) {
        this.coperatorname = coperatorname;
    }

    @Override
    public String getParentPKFieldName() {
        return null;
    }

    @Override
    public String getPKFieldName() {
        return "pk_chargeenable";
    }

    @Override
    public String getTableName() {
        return "wz_chargeenable";
    }
}
