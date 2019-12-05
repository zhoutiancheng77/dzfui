package com.dzf.zxkj.platform.model.bdset;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 打印设置
 *
 * @author lbj
 */
public class PrintSettingVO extends SuperVO {

    @JsonProperty("id")
    private String pk_print_setting;

    private String pk_corp;
    // 用户ID  含义：操作人
    private String cuserid;
    // 节点名称
    private String nodename;
    // 设置
    private String print_setting;

    // 设置(主要用户非打印框的设置,目前主要是界面设置)
    private String print_setting1;

    private DZFDateTime ts;
    private Integer dr;

    private String corpids;//不存库 适用于多公司设置

    private String updatecolumn;//不存库，更改字段的个数，逗号分隔

    public String getUpdatecolumn() {
        return updatecolumn;
    }

    public void setUpdatecolumn(String updatecolumn) {
        this.updatecolumn = updatecolumn;
    }

    public String getPrint_setting1() {
        return print_setting1;
    }

    public void setPrint_setting1(String print_setting1) {
        this.print_setting1 = print_setting1;
    }

    public String getPk_print_setting() {
        return pk_print_setting;
    }

    public void setPk_print_setting(String pk_print_setting) {
        this.pk_print_setting = pk_print_setting;
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

    public String getNodename() {
        return nodename;
    }

    public void setNodename(String nodename) {
        this.nodename = nodename;
    }

    public String getPrint_setting() {
        return print_setting;
    }

    public void setPrint_setting(String print_setting) {
        this.print_setting = print_setting;
    }

    public String getCorpids() {
        return corpids;
    }

    public void setCorpids(String corpids) {
        this.corpids = corpids;
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
    public String getPKFieldName() {
        return "pk_print_setting";
    }

    @Override
    public String getParentPKFieldName() {
        return null;
    }

    @Override
    public String getTableName() {
        return "ynt_settings_print";
    }

}
