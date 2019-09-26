package com.dzf.zxkj.platform.model.sys;

import com.dzf.zxkj.base.model.SuperVO;

@SuppressWarnings("rawtypes")
public class ComboBoxVO extends SuperVO {

    private static final long serialVersionUID = 7836111916612597140L;

    private String id;

    private String name;

    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
