package com.dzf.zxkj.platform.model.sys;

import com.dzf.zxkj.common.model.SuperVO;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class YntArea extends SuperVO<YntArea> {

    @JsonProperty("id")
    private String region_id;
    @JsonProperty("text")
    private String region_name;
    @JsonProperty("children")
    private List<YntArea> childrens;
    @JsonProperty("parentId")
    private String parenter_id;


    public String getParenter_id() {
        return parenter_id;
    }

    public void setParenter_id(String parenter_id) {
        this.parenter_id = parenter_id;
    }

    public String getRegion_id() {
        return region_id;
    }

    public void setRegion_id(String region_id) {
        this.region_id = region_id;
    }

    public String getRegion_name() {
        return region_name;
    }

    public void setRegion_name(String region_name) {
        this.region_name = region_name;
    }

    public List<YntArea> getChildrens() {
        return childrens;
    }

    public void setChildrens(List<YntArea> childrens) {
        this.childrens = childrens;
    }

    @Override
    public String getPKFieldName() {
        return "region_id";
    }

    @Override
    public String getParentPKFieldName() {
        return null;
    }

    @Override
    public String getTableName() {
        return "ynt_area";
    }


}
