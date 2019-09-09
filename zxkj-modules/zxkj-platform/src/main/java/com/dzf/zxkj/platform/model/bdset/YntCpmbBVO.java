package com.dzf.zxkj.platform.model.bdset;

import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.custom.type.DZFDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Entity;

@Entity
public class YntCpmbBVO extends SuperVO<YntCpmbBVO> {

    @JsonProperty("childid")
    private String pk_corp_assettemplate_b;  //子表主键
    @JsonProperty("mainid")
    private String pk_corp_assettemplate;    //子表外键，主表主键
    @JsonProperty("userid")
    private String coperatorid;
    @JsonProperty("opdate")
    private String doperatedate;
    @JsonProperty("zys")
    private String abstracts;                //摘要
    @JsonProperty("cpid")
    private String pk_corp;
    @JsonProperty("accid")
    private String pk_account; //取数科目id
    //
    private String vcode;
    private String vname;
    @JsonProperty("kind")
    private Integer accountkind;          //取数类别
    @JsonProperty("dir")
    private Integer accountdirect;        //方向
    private Integer direct;
    private Integer vlevel;
    private DZFDateTime ts;                       //时间戳
    private Integer dr;

    @JsonProperty("kmmc")
    private String subjname; //取数科目名称


    public String getPk_corp_assettemplate_b() {
        return pk_corp_assettemplate_b;
    }

    public void setPk_corp_assettemplate_b(String pk_corp_assettemplate_b) {
        this.pk_corp_assettemplate_b = pk_corp_assettemplate_b;
    }

    public String getPk_corp_assettemplate() {
        return pk_corp_assettemplate;
    }

    public void setPk_corp_assettemplate(String pk_corp_assettemplate) {
        this.pk_corp_assettemplate = pk_corp_assettemplate;
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

    public DZFDateTime getTs() {
        return ts;
    }

    public void setTs(DZFDateTime ts) {
        this.ts = ts;
    }

    public String getPk_account() {
        return pk_account;
    }

    public void setPk_account(String pk_account) {
        this.pk_account = pk_account;
    }

    public String getAbstracts() {
        return abstracts;
    }

    public void setAbstracts(String abstracts) {
        this.abstracts = abstracts;
    }

    public String getPk_corp() {
        return pk_corp;
    }

    public void setPk_corp(String pk_corp) {
        this.pk_corp = pk_corp;
    }

    public String getVcode() {
        return vcode;
    }

    public void setVcode(String vcode) {
        this.vcode = vcode;
    }

    public String getVname() {
        return vname;
    }

    public void setVname(String vname) {
        this.vname = vname;
    }

    public Integer getAccountkind() {
        return accountkind;
    }

    public void setAccountkind(Integer accountkind) {
        this.accountkind = accountkind;
    }

    public Integer getAccountdirect() {
        return accountdirect;
    }

    public void setAccountdirect(Integer accountdirect) {
        this.accountdirect = accountdirect;
    }

    public Integer getDirect() {
        return direct;
    }

    public void setDirect(Integer direct) {
        this.direct = direct;
    }

    public Integer getVlevel() {
        return vlevel;
    }

    public void setVlevel(Integer vlevel) {
        this.vlevel = vlevel;
    }

    public Integer getDr() {
        return dr;
    }

    public void setDr(Integer dr) {
        this.dr = dr;
    }

    public String getSubjname() {
        return subjname;
    }

    public void setSubjname(String subjname) {
        this.subjname = subjname;
    }

    @Override
    public String getParentPKFieldName() {
        return "pk_corp_assettemplate";
    }

    @Override
    public String getPKFieldName() {
        return "pk_corp_assettemplate_b";
    }

    @Override
    public String getTableName() {
        return "ynt_cpmb_b";
    }
}
