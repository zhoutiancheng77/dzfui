package com.dzf.zxkj.platform.model.sys;

import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 附件
 */
@SuppressWarnings({"rawtypes", "serial"})
public class CorpDocVO extends SuperVO {

    @JsonProperty("doc_id")
    private String pk_doc; // 主键

    @JsonProperty("corp_id")
    private String pk_corp; // 客户主键

    @JsonProperty("doc_name")
    private String docName; // 附件名称(中文)

    @JsonProperty("doc_temp")
    private String docTemp; // 附件名称(下载用 非中文)

    @JsonProperty("doc_owner")
    private String docOwner; // 上传人

    @JsonProperty("doc_time")
    private DZFDateTime docTime; // 上传时间

    private Integer dr; // 删除标记

    private DZFDateTime ts; // 时间

    @JsonProperty("fpath")
    private String vfilepath;// 文件存储路径

    @JsonProperty("vurllink")
    private String vurllink;// 广告链接路径

    @JsonProperty("isads")
    private DZFBoolean isads;// 是否广告

    private DZFBoolean isscan;//是否可浏览

    private DZFBoolean isdownload;//是否下载

    @JsonProperty("iftype")
    private Integer filetype;//1-营业执照正本、2-营业执照副本

    private String sys_type;

    public Integer getFiletype() {
        return filetype;
    }

    public void setFiletype(Integer filetype) {
        this.filetype = filetype;
    }

    public String getSys_type() {
        return sys_type;
    }

    public void setSys_type(String sys_type) {
        this.sys_type = sys_type;
    }

    public DZFBoolean getIsscan() {
        return isscan;
    }

    public void setIsscan(DZFBoolean isscan) {
        this.isscan = isscan;
    }

    public DZFBoolean getIsdownload() {
        return isdownload;
    }

    public void setIsdownload(DZFBoolean isdownload) {
        this.isdownload = isdownload;
    }

    public String getDocTemp() {
        return docTemp;
    }

    public void setDocTemp(String docTemp) {
        this.docTemp = docTemp;
    }

    public String getDocName() {
        return docName;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }

    public DZFDateTime getDocTime() {
        return docTime;
    }

    public void setDocTime(DZFDateTime docTime) {
        this.docTime = docTime;
    }

    public String getDocOwner() {
        return docOwner;
    }

    public void setDocOwner(String docOwner) {
        this.docOwner = docOwner;
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

    public String getPk_doc() {
        return pk_doc;
    }

    public void setPk_doc(String pk_doc) {
        this.pk_doc = pk_doc;
    }

    public String getPk_corp() {
        return pk_corp;
    }

    public void setPk_corp(String pk_corp) {
        this.pk_corp = pk_corp;
    }

    public String getVfilepath() {
        return vfilepath;
    }

    public void setVfilepath(String vfilepath) {
        this.vfilepath = vfilepath;
    }

    public String getVurllink() {
        return vurllink;
    }

    public void setVurllink(String vurllink) {
        this.vurllink = vurllink;
    }

    public DZFBoolean getIsads() {
        return isads;
    }

    public void setIsads(DZFBoolean isads) {
        this.isads = isads;
    }

    @Override
    public String getParentPKFieldName() {
        return "pk_corp";
    }

    @Override
    public String getPKFieldName() {
        return "pk_doc";
    }

    @Override
    public String getTableName() {
        return "ynt_corpdoc";
    }
}
