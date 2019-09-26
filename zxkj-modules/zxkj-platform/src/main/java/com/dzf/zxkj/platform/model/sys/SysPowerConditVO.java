package com.dzf.zxkj.platform.model.sys;


/**
 * 系统管理查询VO
 */
public class SysPowerConditVO {
    private String rolenm;
    private String rolecd;
    private int roletype;
    private String roleid;
    private String corp_id;
    private String ucode;
    private String uname;
    private String entime;
    private String ilock;
    private String crtcorp_id;
    private String iaccount;//会计
    private String idata;//数据
    private Integer companyproperty;//公司属性 1=会计工厂
    private String pk_dept;//部门
    private String utype;//加盟商系统用户---6

    public String getUtype() {
        return utype;
    }

    public void setUtype(String utype) {
        this.utype = utype;
    }

    public String getPk_dept() {
        return pk_dept;
    }

    public void setPk_dept(String pk_dept) {
        this.pk_dept = pk_dept;
    }

    public Integer getCompanyproperty() {
        return companyproperty;
    }

    public void setCompanyproperty(Integer companyproperty) {
        this.companyproperty = companyproperty;
    }

    public String getIaccount() {
        return iaccount;
    }

    public void setIaccount(String iaccount) {
        this.iaccount = iaccount;
    }

    public String getIdata() {
        return idata;
    }

    public void setIdata(String idata) {
        this.idata = idata;
    }

    public String getCrtcorp_id() {
        return crtcorp_id;
    }

    public void setCrtcorp_id(String crtcorp_id) {
        this.crtcorp_id = crtcorp_id;
    }

    public String getCorp_id() {
        return corp_id;
    }

    public void setCorp_id(String corp_id) {
        this.corp_id = corp_id;
    }

    public String getRoleid() {
        return roleid;
    }

    public void setRoleid(String roleid) {
        this.roleid = roleid;
    }

    public String getUcode() {
        return ucode;
    }

    public void setUcode(String ucode) {
        this.ucode = ucode;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getEntime() {
        return entime;
    }

    public void setEntime(String entime) {
        this.entime = entime;
    }

    public String getIlock() {
        return ilock;
    }

    public void setIlock(String ilock) {
        this.ilock = ilock;
    }

    public String getRolenm() {
        return rolenm;
    }

    public void setRolenm(String rolenm) {
        this.rolenm = rolenm;
    }

    public String getRolecd() {
        return rolecd;
    }

    public void setRolecd(String rolecd) {
        this.rolecd = rolecd;
    }

    public int getRoletype() {
        return roletype;
    }

    public void setRoletype(int roletype) {
        this.roletype = roletype;
    }


}