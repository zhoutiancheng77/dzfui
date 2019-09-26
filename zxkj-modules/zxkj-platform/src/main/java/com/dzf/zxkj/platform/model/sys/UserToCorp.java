package com.dzf.zxkj.platform.model.sys;

import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFBoolean;

/**
 * 用户公司关联表
 *
 * @author zhangj
 */
public class UserToCorp extends SuperVO {

    public static final String TABLE_NAME = "ynt_corp_user";
    public static final String PK_CORP_USER = "pk_corp_user";

    private String pk_corp_user;//  主键
    private String pk_user;// 用户主键
    private String pk_tempcorp;//  临时公司主键
    private DZFBoolean ismanage;// 是否管理员
    private String pk_corp;// 签约公司主键
    private DZFBoolean bdata;
    private DZFBoolean baccount;
    private Integer istate;

    //环信信息
    private String user_code;

    private String userName;//用户名称

    private String hxusername;//环信用户名称

    private String password;

    private String nickName;


    public String getHxusername() {
        return hxusername;
    }

    public void setHxusername(String hxusername) {
        this.hxusername = hxusername;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getUser_code() {
        return user_code;
    }

    public void setUser_code(String user_code) {
        this.user_code = user_code;
    }

    public String getPk_corp_user() {
        return pk_corp_user;
    }

    public void setPk_corp_user(String pk_corp_user) {
        this.pk_corp_user = pk_corp_user;
    }

    public String getPk_user() {
        return pk_user;
    }

    public void setPk_user(String pk_user) {
        this.pk_user = pk_user;
    }

    public String getPk_tempcorp() {
        return pk_tempcorp;
    }

    public void setPk_tempcorp(String pk_tempcorp) {
        this.pk_tempcorp = pk_tempcorp;
    }

    public DZFBoolean getIsmanage() {
        return ismanage;
    }

    public void setIsmanage(DZFBoolean ismanage) {
        this.ismanage = ismanage;
    }

    public String getPk_corp() {
        return pk_corp;
    }

    public void setPk_corp(String pk_corp) {
        this.pk_corp = pk_corp;
    }

    @Override
    public String getParentPKFieldName() {
        return null;
    }

    @Override
    public String getPKFieldName() {
        return PK_CORP_USER;
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    public DZFBoolean getBdata() {
        return bdata;
    }

    public void setBdata(DZFBoolean bdata) {
        this.bdata = bdata;
    }

    public DZFBoolean getBaccount() {
        return baccount;
    }

    public void setBaccount(DZFBoolean baccount) {
        this.baccount = baccount;
    }

    public Integer getIstate() {
        return istate;
    }

    public void setIstate(Integer istate) {
        this.istate = istate;
    }

}
