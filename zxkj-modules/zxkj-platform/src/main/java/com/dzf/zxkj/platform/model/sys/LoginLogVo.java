package com.dzf.zxkj.platform.model.sys;

import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDateTime;

import java.sql.Timestamp;

public class LoginLogVo extends SuperVO {
    private String pk_login;
    private String memo;
    private String loginip;

    public String getPk_login() {
        return pk_login;
    }

    public void setPk_login(String pk_login) {
        this.pk_login = pk_login;
    }

    public String getPk_corp() {
        return pk_corp;
    }

    public void setPk_corp(String pk_corp) {
        this.pk_corp = pk_corp;
    }

    public String getPk_user() {
        return pk_user;
    }

    public void setPk_user(String pk_user) {
        this.pk_user = pk_user;
    }

    public String getLoginsession() {
        return loginsession;
    }

    public void setLoginsession(String loginsession) {
        this.loginsession = loginsession;
    }

    public String getProject_name() {
        return project_name;
    }

    public void setProject_name(String project_name) {
        this.project_name = project_name;
    }

    public Timestamp getLogindate() {
        return logindate;
    }

    public void setLogindate(Timestamp logindate) {
        this.logindate = logindate;
    }

    public Timestamp getLogoutdate() {
        return logoutdate;
    }

    public void setLogoutdate(Timestamp logoutdate) {
        this.logoutdate = logoutdate;
    }

    public Integer getDr() {
        return dr;
    }

    public void setDr(Integer dr) {
        this.dr = dr;
    }

    public Integer getLogouttype() {
        return logouttype;
    }

    public void setLogouttype(Integer logouttype) {
        this.logouttype = logouttype;
    }

    public Integer getLoginstatus() {
        return loginstatus;
    }

    public void setLoginstatus(Integer loginstatus) {
        this.loginstatus = loginstatus;
    }

    public DZFDateTime getTs() {
        return ts;
    }

    public void setTs(DZFDateTime ts) {
        this.ts = ts;
    }

    private String pk_corp;
    private String pk_user;
    private String loginsession;
    private String project_name;
    private Timestamp logindate;
    private Timestamp logoutdate;
    private Integer dr;
    //	1:用户正常退出，2：被其它人强制退出，3：session失效
//	IGlobalConstants.logoutType
    private Integer logouttype;
    private Integer loginstatus;
    private DZFDateTime ts;

    @Override
    public String getParentPKFieldName() {
        return null;
    }

    @Override
    public String getPKFieldName() {
        return "pk_login";
    }

    @Override
    public String getTableName() {
        return "ynt_login_log";
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getLoginip() {
        return loginip;
    }

    public void setLoginip(String loginip) {
        this.loginip = loginip;
    }
}
