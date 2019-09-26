package com.dzf.zxkj.platform.model.sys;


import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 系统功能节点VO
 */
public class SysFunNodeVO extends SuperVO {

    private static final long serialVersionUID = 1L;
    @JsonProperty("id")
    private String pk_funnode;
    @JsonProperty("__parentId")
    private String pk_parent;

    @JsonProperty("filename")
    private String file_dir;

    @JsonProperty("code")
    private String fun_code;

    @JsonProperty("sign_id")
    private String pk_power;

    @JsonProperty("name")
    private String fun_name;

    private String pcode;

    @JsonProperty("sx")
    private Integer show_order;

    @JsonProperty("mark")
    private String memo;

    @JsonProperty("url")
    private String nodeurl;

    private String module;
    private String modulename;
    private String shortname;

    //  是否节点隐藏
    private DZFBoolean ishidenode;

    private Integer funtype;//功能节点使用范围，目前只管理平台使用，1-加盟商与普通代账机构共同使用、2-普通代账机构使用、3-加盟商使用

    public Integer getFuntype() {
        return funtype;
    }

    public void setFuntype(Integer funtype) {
        this.funtype = funtype;
    }

    public DZFBoolean getIshidenode() {
        return ishidenode;
    }

    public void setIshidenode(DZFBoolean ishidenode) {
        this.ishidenode = ishidenode;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getModulename() {
        return modulename;
    }

    public void setModulename(String modulename) {
        this.modulename = modulename;
    }

    public String getShortname() {
        return shortname;
    }

    public void setShortname(String shortname) {
        this.shortname = shortname;
    }


    public String getPk_power() {
        return pk_power;
    }

    public void setPk_power(String pk_power) {
        this.pk_power = pk_power;
    }

    public String getPk_parent() {
        return pk_parent;
    }

    public void setPk_parent(String pk_parent) {
        this.pk_parent = pk_parent;
    }

    public String getFile_dir() {
        return file_dir;
    }

    public void setFile_dir(String file_dir) {
        this.file_dir = file_dir;
    }

    public Integer getShow_order() {
        return show_order;
    }

    public void setShow_order(Integer show_order) {
        this.show_order = show_order;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getNodeurl() {
        return nodeurl;
    }

    public void setNodeurl(String nodeurl) {
        this.nodeurl = nodeurl;
    }

    public String getPcode() {
        return pcode;
    }

    public void setPcode(String pcode) {
        this.pcode = pcode;
    }


    public String getPk_funnode() {
        return pk_funnode;
    }

    public void setPk_funnode(String pk_funnode) {
        this.pk_funnode = pk_funnode;
    }

    public String getFun_code() {
        return fun_code;
    }

    public void setFun_code(String fun_code) {
        this.fun_code = fun_code;
    }

    public String getFun_name() {
        return fun_name;
    }

    public void setFun_name(String fun_name) {
        this.fun_name = fun_name;
    }

    @Override
    public String getParentPKFieldName() {
        return null;
    }

    @Override
    public String getPKFieldName() {
        return "pk_funnode";
    }

    @Override
    public String getTableName() {
        return "sm_funnode";
    }
}