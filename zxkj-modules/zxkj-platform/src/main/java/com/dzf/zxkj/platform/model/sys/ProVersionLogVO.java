package com.dzf.zxkj.platform.model.sys;

import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 产品版本更新日志
 * 
 * @author dzf
 *
 */
public class ProVersionLogVO extends SuperVO<ProVersionLogVO> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @JsonProperty("plid")
    private String pk_proversionlog; // 主键

    @JsonProperty("corpid")
    private String pk_corp; // 公司主键

    @JsonProperty("tle")
    private String vtitle; // 标题

    @JsonProperty("tim")
    private String vtime; // 时间

    private String vsyscode; // 系统编码

    @JsonProperty("fname")
    private String vfilename; // 文件名

    private String vurl; // 文件路径

    private DZFBoolean islock; // 锁定

    @JsonProperty("operator")
    private String coperatorid; // 制单人

    @JsonProperty("operatedate")
    private DZFDate doperatedate; // 制单日期
    
    private Integer iorder;
    
    private String vnote;

    private Integer dr;

    private DZFDateTime ts;
    
    private Integer logtype;//1：共用；2：普通代账机构；3：加盟商

    public Integer getLogtype() {
        return logtype;
    }

    public void setLogtype(Integer logtype) {
        this.logtype = logtype;
    }

    public String getVnote() {
        return vnote;
    }

    public void setVnote(String vnote) {
        this.vnote = vnote;
    }

    public Integer getIorder() {
        return iorder;
    }

    public void setIorder(Integer iorder) {
        this.iorder = iorder;
    }

    public String getPk_proversionlog() {
        return pk_proversionlog;
    }

    public void setPk_proversionlog(String pk_proversionlog) {
        this.pk_proversionlog = pk_proversionlog;
    }

    public String getVtitle() {
        return vtitle;
    }

    public void setVtitle(String vtitle) {
        this.vtitle = vtitle;
    }

    public String getVtime() {
        return vtime;
    }

    public void setVtime(String vtime) {
        this.vtime = vtime;
    }

    public String getVsyscode() {
        return vsyscode;
    }

    public void setVsyscode(String vsyscode) {
        this.vsyscode = vsyscode;
    }

    public String getVfilename() {
        return vfilename;
    }

    public void setVfilename(String vfilename) {
        this.vfilename = vfilename;
    }

    public String getVurl() {
        return vurl;
    }

    public void setVurl(String vurl) {
        this.vurl = vurl;
    }

    public DZFBoolean getIslock() {
        return islock;
    }

    public void setIslock(DZFBoolean islock) {
        this.islock = islock;
    }

    public String getCoperatorid() {
        return coperatorid;
    }

    public void setCoperatorid(String coperatorid) {
        this.coperatorid = coperatorid;
    }

    public DZFDate getDoperatedate() {
        return doperatedate;
    }

    public void setDoperatedate(DZFDate doperatedate) {
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

    public String getPk_corp() {
        return pk_corp;
    }

    public void setPk_corp(String pk_corp) {
        this.pk_corp = pk_corp;
    }

    @Override
    public String getPKFieldName() {
        return "pk_proversionlog";
    }

    @Override
    public String getParentPKFieldName() {
        return null;
    }

    @Override
    public String getTableName() {
        return "bd_proversionlog";
    }
}
