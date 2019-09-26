package com.dzf.zxkj.platform.model.sys;

import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings({"rawtypes", "serial"})

public class MsgremindsetVO extends SuperVO {
    @JsonProperty("pk_sms_set")
    private String pk_msgremindset; // 主键

    @JsonProperty("corp")
    private String pk_corp; // 公司

    @JsonProperty("busiproc")
    private DZFBoolean isbusiproc; // 业务流转短信提醒

    @JsonProperty("arrears")
    private DZFBoolean isarrears; // 欠费短信提醒

    @JsonProperty("contractexpire")
    private DZFBoolean iscontractexpire;// 合同到期短信提醒

    private String coperatorid; // 制单人

    private String doperatedate; // 制单日期

    private Integer dr;

    private DZFDateTime ts;

    @JsonProperty("jfday")
    private Integer jfreminday;// 欠费提前几天提醒

    @JsonProperty("jftimes")
    private Integer jfremindtimes;// 欠费提醒次数

    @JsonProperty("htday")
    private Integer htreminday;// 合同提前几天提醒

    @JsonProperty("httimes")
    private Integer htremindtimes;// 合同提醒次数

    @JsonProperty("ispwd")
    private DZFBoolean pwdstrategy;//密码修改策略

    @JsonProperty("pwdnum")
    private Integer strategynum;//密码策略天数

    @JsonProperty("rednum")
    private Integer remindnum;//密码提醒天数

    @JsonProperty("ismngred")
    private DZFBoolean ismngremind;//管理员是否受控

    @JsonProperty("srole")
    private DZFBoolean isrole; // 选择角色

    @JsonProperty("suser")
    private DZFBoolean isuser; // 选择用户

    @JsonProperty("spuser")
    private DZFBoolean ispuser; // 选择有客户权限的用户

    @JsonProperty("roleid")
    private String roleid; //角色主键

    @JsonProperty("userid")
    private String userid; //用户主键

    @JsonProperty("rolename")
    private String rolename; //角色名称 

    @JsonProperty("username")
    private String username; //用户名称

    private DZFBoolean ischarge;//合同收款完成

    private DZFBoolean isexpire;//合同到期

    private Integer dqday;//到期天数

    @JsonProperty("mremind")
    private Integer monthremind;//按月

    @JsonProperty("qremind")
    private Integer quarterremind;//按季

    @JsonProperty("yremind")
    private Integer yearremind;//按年

    public Integer getMonthremind() {
        return monthremind;
    }

    public void setMonthremind(Integer monthremind) {
        this.monthremind = monthremind;
    }

    public Integer getQuarterremind() {
        return quarterremind;
    }

    public void setQuarterremind(Integer quarterremind) {
        this.quarterremind = quarterremind;
    }

    public Integer getYearremind() {
        return yearremind;
    }

    public void setYearremind(Integer yearremind) {
        this.yearremind = yearremind;
    }

    public Integer getDqday() {
        return dqday;
    }

    public void setDqday(Integer dqday) {
        this.dqday = dqday;
    }

    public DZFBoolean getIscharge() {
        return ischarge;
    }

    public void setIscharge(DZFBoolean ischarge) {
        this.ischarge = ischarge;
    }

    public DZFBoolean getIsexpire() {
        return isexpire;
    }

    public void setIsexpire(DZFBoolean isexpire) {
        this.isexpire = isexpire;
    }

    public Integer getRemindnum() {
        return remindnum;
    }

    public void setRemindnum(Integer remindnum) {
        this.remindnum = remindnum;
    }

    public DZFBoolean getIsmngremind() {
        return ismngremind;
    }

    public void setIsmngremind(DZFBoolean ismngremind) {
        this.ismngremind = ismngremind;
    }

    public DZFBoolean getIsrole() {
        return isrole;
    }

    public void setIsrole(DZFBoolean isrole) {
        this.isrole = isrole;
    }

    public DZFBoolean getIsuser() {
        return isuser;
    }

    public void setIsuser(DZFBoolean isuser) {
        this.isuser = isuser;
    }

    public DZFBoolean getIspuser() {
        return ispuser;
    }

    public void setIspuser(DZFBoolean ispuser) {
        this.ispuser = ispuser;
    }

    public String getRoleid() {
        return roleid;
    }

    public void setRoleid(String roleid) {
        this.roleid = roleid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getRolename() {
        return rolename;
    }

    public void setRolename(String rolename) {
        this.rolename = rolename;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public DZFBoolean getPwdstrategy() {
        return pwdstrategy;
    }

    public void setPwdstrategy(DZFBoolean pwdstrategy) {
        this.pwdstrategy = pwdstrategy;
    }

    public Integer getStrategynum() {
        return strategynum;
    }

    public void setStrategynum(Integer strategynum) {
        this.strategynum = strategynum;
    }

    public String getPk_msgremindset() {
        return pk_msgremindset;
    }

    public void setPk_msgremindset(String pk_msgremindset) {
        this.pk_msgremindset = pk_msgremindset;
    }

    public String getPk_corp() {
        return pk_corp;
    }

    public void setPk_corp(String pk_corp) {
        this.pk_corp = pk_corp;
    }

    public DZFBoolean getIsbusiproc() {
        return isbusiproc;
    }

    public void setIsbusiproc(DZFBoolean isbusiproc) {
        this.isbusiproc = isbusiproc;
    }

    public DZFBoolean getIsarrears() {
        return isarrears;
    }

    public void setIsarrears(DZFBoolean isarrears) {
        this.isarrears = isarrears;
    }

    public DZFBoolean getIscontractexpire() {
        return iscontractexpire;
    }

    public void setIscontractexpire(DZFBoolean iscontractexpire) {
        this.iscontractexpire = iscontractexpire;
    }

    public String getCoperatorid() {
        return coperatorid;
    }

    public void setCoperatorid(String coperatorid) {
        this.coperatorid = coperatorid;
    }

    public String getDoperatedate() {
        return doperatedate;
    }

    public void setDoperatedate(String doperatedate) {
        this.doperatedate = doperatedate;
    }

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

    public Integer getJfremindtimes() {
        return jfremindtimes;
    }

    public void setJfremindtimes(Integer jfremindtimes) {
        this.jfremindtimes = jfremindtimes;
    }

    public Integer getHtremindtimes() {
        return htremindtimes;
    }

    public void setHtremindtimes(Integer htremindtimes) {
        this.htremindtimes = htremindtimes;
    }

    public Integer getJfreminday() {
        return jfreminday;
    }

    public void setJfreminday(Integer jfreminday) {
        this.jfreminday = jfreminday;
    }

    public Integer getHtreminday() {
        return htreminday;
    }

    public void setHtreminday(Integer htreminday) {
        this.htreminday = htreminday;
    }

    @Override
    public String getParentPKFieldName() {
        return null;
    }

    @Override
    public String getPKFieldName() {
        return "pk_msgremindset";
    }

    @Override
    public String getTableName() {
        return "ynt_msgremindset";
    }
}
