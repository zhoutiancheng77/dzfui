package com.dzf.zxkj.platform.model.sys;

import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 证件信息
 */
@SuppressWarnings({"rawtypes", "serial"})
public class CorpCredenVO extends SuperVO {

    @JsonProperty("credenid")
    private String pk_credential; // 主键

    @JsonProperty("corp_id")
    private String pk_corp; // 客户主键

    @JsonProperty("kname")
    private String corpkname; // 公司名称

    @JsonProperty("vcname")
    private String vcredname; // 证件名称

    @JsonProperty("vcode")
    private String vcredcode; // 证件编号

    @JsonProperty("dexpdate")
    private DZFDate dexpiredate; // 证件到期日

    @JsonProperty("expday")
    private Integer iexpireday; // 提前几天提醒

    @JsonProperty("perid")
    private String vpersionid;// 提醒对象

    private Integer dr; // 删除标记

    private DZFDateTime ts; // 时间

    private Integer isendmsg;// 消息通知

    private String fathercorp;

    @JsonProperty("vdman")
    private String vdealman;// 处理人

    @JsonProperty("t_vdman")
    private String t_vdealman;// 处理人名称

    @JsonProperty("dealdate")
    private DZFDate ddealdate;// 处理日期

    @JsonProperty("itype")
    private Integer idealtype;// 处理类型 1-已派发任务；2-已更新到期日；3-忽略提醒;	查询用的4、已逾期；5、已派任务；0全部

    @JsonProperty("ignore")
    private DZFBoolean isignore;// 忽略提醒；

    @JsonProperty("vmemo")
    private String vmemo;// 备注

    @JsonProperty("incode")
    private String innercode;//公司编码

    public String getInnercode() {
        return innercode;
    }

    public void setInnercode(String innercode) {
        this.innercode = innercode;
    }

    public String getT_vdealman() {
        return t_vdealman;
    }

    public void setT_vdealman(String t_vdealman) {
        this.t_vdealman = t_vdealman;
    }

    public String getVdealman() {
        return vdealman;
    }

    public void setVdealman(String vdealman) {
        this.vdealman = vdealman;
    }

    public DZFDate getDdealdate() {
        return ddealdate;
    }

    public void setDdealdate(DZFDate ddealdate) {
        this.ddealdate = ddealdate;
    }

    public Integer getIdealtype() {
        return idealtype;
    }

    public void setIdealtype(Integer idealtype) {
        this.idealtype = idealtype;
    }

    public DZFBoolean getIsignore() {
        return isignore;
    }

    public void setIsignore(DZFBoolean isignore) {
        this.isignore = isignore;
    }

    public String getVmemo() {
        return vmemo;
    }

    public void setVmemo(String vmemo) {
        this.vmemo = vmemo;
    }

    public Integer getIsendmsg() {
        return isendmsg;
    }

    public void setIsendmsg(Integer isendmsg) {
        this.isendmsg = isendmsg;
    }

    public String getFathercorp() {
        return fathercorp;
    }

    public void setFathercorp(String fathercorp) {
        this.fathercorp = fathercorp;
    }

    public String getCorpkname() {
        return corpkname;
    }

    public void setCorpkname(String corpkname) {
        this.corpkname = corpkname;
    }

    public String getPk_credential() {
        return pk_credential;
    }

    public void setPk_credential(String pk_credential) {
        this.pk_credential = pk_credential;
    }

    public String getVcredname() {
        return vcredname;
    }

    public void setVcredname(String vcredname) {
        this.vcredname = vcredname;
    }

    public String getVcredcode() {
        return vcredcode;
    }

    public void setVcredcode(String vcredcode) {
        this.vcredcode = vcredcode;
    }

    public DZFDate getDexpiredate() {
        return dexpiredate;
    }

    public void setDexpiredate(DZFDate dexpiredate) {
        this.dexpiredate = dexpiredate;
    }

    public Integer getIexpireday() {
        return iexpireday;
    }

    public void setIexpireday(Integer iexpireday) {
        this.iexpireday = iexpireday;
    }

    public String getVpersionid() {
        return vpersionid;
    }

    public void setVpersionid(String vpersionid) {
        this.vpersionid = vpersionid;
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

    public String getPk_corp() {
        return pk_corp;
    }

    public void setPk_corp(String pk_corp) {
        this.pk_corp = pk_corp;
    }

    @Override
    public String getParentPKFieldName() {
        return "pk_corp";
    }

    @Override
    public String getPKFieldName() {
        return "pk_credential";
    }

    @Override
    public String getTableName() {
        return "ynt_credential";
    }
}
