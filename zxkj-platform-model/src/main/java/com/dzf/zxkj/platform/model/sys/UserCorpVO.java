package com.dzf.zxkj.platform.model.sys;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 用户分配客户表
 * <p>
 */
public class UserCorpVO extends SuperVO {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public String pk_user_corp;
    public String cuserid;
    @JsonProperty("corpid")
    public String pk_corp;// 代账机构ID
    @JsonProperty("corpkid")
    public String pk_corpk;// 客户ID
    private Integer dr; // 删除标记
    private DZFDateTime ts; // 时间戳
    
    /**以下字段只页面展示使用**/
    @JsonProperty("incode")
    private String innercode;
    @JsonProperty("uname")
    private String unitname;
    
    private String corpkids;
    
    private String userids;
    
    public String getCorpkids() {
        return corpkids;
    }

    public void setCorpkids(String corpkids) {
        this.corpkids = corpkids;
    }

    public String getUserids() {
        return userids;
    }

    public void setUserids(String userids) {
        this.userids = userids;
    }

    public String getInnercode() {
        return innercode;
    }

    public void setInnercode(String innercode) {
        this.innercode = innercode;
    }

    public String getUnitname() {
        return unitname;
    }

    public void setUnitname(String unitname) {
        this.unitname = unitname;
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

    public String getCuserid() {
        return cuserid;
    }

    public String getPk_corp() {
        return pk_corp;
    }

    public void setCuserid(String newCuserid) {

        cuserid = newCuserid;
    }

    public void setPk_corp(String newPk_corp) {

        pk_corp = newPk_corp;
    }

    public String getParentPKFieldName() {

        return null;
    }

    public String getPKFieldName() {

        return "pk_user_corp";
    }

    public String getTableName() {

        return "sm_user_corp";
    }

    public UserCorpVO() {
        super();
    }

    public UserCorpVO(String newPk_user_corp) {
        super();

        // 为主键字段赋值:
        pk_user_corp = newPk_user_corp;
    }

    public String getPrimaryKey() {

        return pk_user_corp;
    }

    public void setPrimaryKey(String newPk_user_corp) {

        pk_user_corp = newPk_user_corp;
    }

    public String getPk_user_corp() {
        return pk_user_corp;
    }

    public void setPk_user_corp(String pk_user_corp) {
        this.pk_user_corp = pk_user_corp;
    }

    public String getPk_corpk() {
        return pk_corpk;
    }

    public void setPk_corpk(String pk_corpk) {
        this.pk_corpk = pk_corpk;
    }

    public String getEntityName() {

        return "UserCorp";
    }
}