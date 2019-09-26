package com.dzf.zxkj.platform.model;

import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.entity.ICodeName;

import java.io.Serializable;

public class CodeName extends SuperVO implements ICodeName, Serializable {
    private String code;
    private String name;
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getPKFieldName() {
        return null;
    }

    @Override
    public String getParentPKFieldName() {
        return null;
    }

    @Override
    public String getTableName() {
        return null;
    }
}