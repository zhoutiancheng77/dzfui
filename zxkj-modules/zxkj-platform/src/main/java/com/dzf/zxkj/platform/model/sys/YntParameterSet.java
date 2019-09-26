package com.dzf.zxkj.platform.model.sys;

import com.dzf.zxkj.base.model.SuperVO;
import com.fasterxml.jackson.annotation.JsonProperty;

/*
 * 参数设置vo
 */
public class YntParameterSet extends SuperVO {
    @JsonProperty("id")
    private String pk_parameter;
    @JsonProperty("pbm")
    private String parameterbm;
    @JsonProperty("pname")//参数名称
    private String parametername;
    @JsonProperty("pvalue")//参数取值范围
    private String parametervalue;
    private String edittime;
    @JsonProperty("pdvalue")//参数取值
    private Integer pardetailvalue;
    private String detail;
    private String pk_corp;
    private Integer issync;//0同步；1未同步
    private Integer dr;

    private Integer plevel;//参数对应的公司级别0：集团级别1：会计公司级别2：小企业级别
    public static String pkFieldName = "pk_parameter";


    public String getPk_parameter() {
        return pk_parameter;
    }

    public void setPk_parameter(String pk_parameter) {
        this.pk_parameter = pk_parameter;
    }

    public String getParameterbm() {
        return parameterbm;
    }

    public void setParameterbm(String parameterbm) {
        this.parameterbm = parameterbm;
    }

    public String getParametername() {
        return parametername;
    }

    public void setParametername(String parametername) {
        this.parametername = parametername;
    }

    public String getParametervalue() {
        return parametervalue;
    }

    public void setParametervalue(String parametervalue) {
        this.parametervalue = parametervalue;
    }

    public String getEdittime() {
        return edittime;
    }

    public void setEdittime(String edittime) {
        this.edittime = edittime;
    }


    public Integer getPardetailvalue() {
        return pardetailvalue;
    }

    public void setPardetailvalue(Integer pardetailvalue) {
        this.pardetailvalue = pardetailvalue;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getPk_corp() {
        return pk_corp;
    }

    public void setPk_corp(String pk_corp) {
        this.pk_corp = pk_corp;
    }

    @Override
    public String getPKFieldName() {
        // TODO Auto-generated method stub
        return "pk_parameter";
    }

    @Override
    public String getParentPKFieldName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getTableName() {
        // TODO Auto-generated method stub
        return "ynt_parameter";
    }

    public Integer getDr() {
        return dr;
    }

    public void setDr(Integer dr) {
        this.dr = dr;
    }


    public Integer getIssync() {
        return issync;
    }

    public void setIssync(Integer issync) {
        this.issync = issync;
    }

    public Integer getPlevel() {
        return plevel;
    }

    public void setPlevel(Integer plevel) {
        this.plevel = plevel;
    }


}
