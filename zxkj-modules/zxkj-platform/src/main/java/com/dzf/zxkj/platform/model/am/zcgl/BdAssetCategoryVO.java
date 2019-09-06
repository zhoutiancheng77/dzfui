package com.dzf.zxkj.platform.model.am.zcgl;

import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @Auther: dandelion
 * @Date: 2019-09-05
 * @Description:
 */
public class BdAssetCategoryVO extends SuperVO {
    private static final long serialVersionUID = 2903221711497305256L;
    private DZFDateTime ts;
    private String vdef9;
    @JsonProperty("cpid")
    private String coperatorid;
    private String vdef1;
    private String vdef8;
    private String vdef10;
    @JsonProperty("prop")
    private Integer assetproperty;
    private String vdef2;
    private String vdef5;
    @JsonProperty("name")
    private String catename;
    @JsonProperty("level")
    private Integer catelevel;
    @JsonProperty("memo")
    private String memo;
    private String vdef3;
    private String vdef6;
    @JsonProperty("dpdate")
    private DZFDate doperatedate;
    private Integer dr;
    private String vdef4;
    @JsonProperty("code")
    private String catecode;
    @JsonProperty("id")
    private String pk_assetcategory;
    private String vdef7;
    private DZFBoolean endflag;
    @JsonProperty("corp")
    private String pk_corp;
    private String text;
    private Integer codeid;
    @JsonProperty("ulimit")
    private Integer uselimit;
    @JsonProperty("zjfs")
    private Integer zjtype;
    @JsonProperty("czl")
    private DZFDouble salvageratio;
    public static final String VDEF9 = "vdef9";
    public static final String COPERATORID = "coperatorid";
    public static final String VDEF1 = "vdef1";
    public static final String VDEF8 = "vdef8";
    public static final String VDEF10 = "vdef10";
    public static final String ASSETPROPERTY = "assetproperty";
    public static final String VDEF2 = "vdef2";
    public static final String VDEF5 = "vdef5";
    public static final String CATENAME = "catename";
    public static final String CATELEVEL = "catelevel";
    public static final String MEMO = "memo";
    public static final String VDEF3 = "vdef3";
    public static final String VDEF6 = "vdef6";
    public static final String DOPERATEDATE = "doperatedate";
    public static final String VDEF4 = "vdef4";
    public static final String CATECODE = "catecode";
    public static final String PK_ASSETCATEGORY = "pk_assetcategory";
    public static final String VDEF7 = "vdef7";

    public Integer getCodeid() {
        return this.codeid;
    }

    public void setCodeid(Integer codeid) {
        this.codeid = codeid;
    }

    public String getPk_corp() {
        return this.pk_corp;
    }

    public void setPk_corp(String pk_corp) {
        this.pk_corp = pk_corp;
    }

    public String toString() {
        return this.getCatecode() + " " + this.getCatename();
    }

    public DZFDateTime getTs() {
        return this.ts;
    }

    public void setTs(DZFDateTime newTs) {
        this.ts = newTs;
    }

    public String getVdef9() {
        return this.vdef9;
    }

    public void setVdef9(String newVdef9) {
        this.vdef9 = newVdef9;
    }

    public String getCoperatorid() {
        return this.coperatorid;
    }

    public void setCoperatorid(String newCoperatorid) {
        this.coperatorid = newCoperatorid;
    }

    public String getVdef1() {
        return this.vdef1;
    }

    public void setVdef1(String newVdef1) {
        this.vdef1 = newVdef1;
    }

    public String getVdef8() {
        return this.vdef8;
    }

    public void setVdef8(String newVdef8) {
        this.vdef8 = newVdef8;
    }

    public String getVdef10() {
        return this.vdef10;
    }

    public void setVdef10(String newVdef10) {
        this.vdef10 = newVdef10;
    }

    public Integer getAssetproperty() {
        return this.assetproperty;
    }

    public void setAssetproperty(Integer newAssetproperty) {
        this.assetproperty = newAssetproperty;
    }

    public String getVdef2() {
        return this.vdef2;
    }

    public void setVdef2(String newVdef2) {
        this.vdef2 = newVdef2;
    }

    public String getVdef5() {
        return this.vdef5;
    }

    public void setVdef5(String newVdef5) {
        this.vdef5 = newVdef5;
    }

    public String getCatename() {
        return this.catename;
    }

    public void setCatename(String newCatename) {
        this.catename = newCatename;
    }

    public Integer getCatelevel() {
        return this.catelevel;
    }

    public void setCatelevel(Integer newCatelevel) {
        this.catelevel = newCatelevel;
    }

    public String getMemo() {
        return this.memo;
    }

    public void setMemo(String newMemo) {
        this.memo = newMemo;
    }

    public String getVdef3() {
        return this.vdef3;
    }

    public void setVdef3(String newVdef3) {
        this.vdef3 = newVdef3;
    }

    public String getVdef6() {
        return this.vdef6;
    }

    public void setVdef6(String newVdef6) {
        this.vdef6 = newVdef6;
    }

    public DZFDate getDoperatedate() {
        return this.doperatedate;
    }

    public void setDoperatedate(DZFDate newDoperatedate) {
        this.doperatedate = newDoperatedate;
    }

    public Integer getDr() {
        return this.dr;
    }

    public void setDr(Integer newDr) {
        this.dr = newDr;
    }

    public String getVdef4() {
        return this.vdef4;
    }

    public void setVdef4(String newVdef4) {
        this.vdef4 = newVdef4;
    }

    public String getCatecode() {
        return this.catecode;
    }

    public void setCatecode(String newCatecode) {
        this.catecode = newCatecode;
    }

    public String getPk_assetcategory() {
        return this.pk_assetcategory;
    }

    public void setPk_assetcategory(String newPk_assetcategory) {
        this.pk_assetcategory = newPk_assetcategory;
    }

    public String getVdef7() {
        return this.vdef7;
    }

    public void setVdef7(String newVdef7) {
        this.vdef7 = newVdef7;
    }

    public DZFBoolean getEndflag() {
        return this.endflag;
    }

    public void setEndflag(DZFBoolean endflag) {
        this.endflag = endflag;
    }

    public String getParentPKFieldName() {
        return null;
    }

    public String getPKFieldName() {
        return "pk_assetcategory";
    }

    public String getTableName() {
        return "ynt_category";
    }

    public Integer getUselimit() {
        return this.uselimit;
    }

    public void setUselimit(Integer uselimit) {
        this.uselimit = uselimit;
    }

    public BdAssetCategoryVO() {
    }

    public String getText() {
        this.text = this.getCatecode() + "_" + this.getCatename();
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getZjtype() {
        return this.zjtype;
    }

    public void setZjtype(Integer zjtype) {
        this.zjtype = zjtype;
    }

    public DZFDouble getSalvageratio() {
        return this.salvageratio;
    }

    public void setSalvageratio(DZFDouble salvageratio) {
        this.salvageratio = salvageratio;
    }
}
