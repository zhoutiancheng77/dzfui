package com.dzf.zxkj.platform.model.sys;

import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;

/**
 * 大账房 、客如云关联关系信息表
 */
public class KryDZFRelationVO extends SuperVO {

    private String pk_krydzfrelation;//唯一主键
    private String kryshopid;//唯一索引---非空
    private String kryshopname;
    private String kryuserid;//非空 ----客如云的用户编码
    private String kryusrename;//客如云的用户名称
    private String kryclientid;//非空
    private String krysecret;//非空
    private String pk_corp;//唯一索引--非空
    private String cuserid;
    private DZFDateTime ts;
    private Integer dr;
    private DZFDate lastqrydate;//[默认当年]【服务器年度-01-01】
    private String def1;
    private String def2;
    private String def3;
    private String def4;
    private String def5;

    public String getPk_krydzfrelation() {
        return pk_krydzfrelation;
    }

    public void setPk_krydzfrelation(String pk_krydzfrelation) {
        this.pk_krydzfrelation = pk_krydzfrelation;
    }

    public String getKryshopid() {
        return kryshopid;
    }

    public void setKryshopid(String kryshopid) {
        this.kryshopid = kryshopid;
    }

    public String getKryshopname() {
        return kryshopname;
    }

    public void setKryshopname(String kryshopname) {
        this.kryshopname = kryshopname;
    }

    public String getKryuserid() {
        return kryuserid;
    }

    public void setKryuserid(String kryuserid) {
        this.kryuserid = kryuserid;
    }

    public String getKryusrename() {
        return kryusrename;
    }

    public void setKryusrename(String kryusrename) {
        this.kryusrename = kryusrename;
    }

    public String getKryclientid() {
        return kryclientid;
    }

    public void setKryclientid(String kryclientid) {
        this.kryclientid = kryclientid;
    }

    public String getKrysecret() {
        return krysecret;
    }

    public void setKrysecret(String krysecret) {
        this.krysecret = krysecret;
    }

    public String getPk_corp() {
        return pk_corp;
    }

    public void setPk_corp(String pk_corp) {
        this.pk_corp = pk_corp;
    }

    public String getCuserid() {
        return cuserid;
    }

    public void setCuserid(String cuserid) {
        this.cuserid = cuserid;
    }

    public DZFDateTime getTs() {
        return ts;
    }

    public void setTs(DZFDateTime ts) {
        this.ts = ts;
    }

    public Integer getDr() {
        return dr;
    }

    public void setDr(Integer dr) {
        this.dr = dr;
    }

    public DZFDate getLastqrydate() {
        return lastqrydate;
    }

    public void setLastqrydate(DZFDate lastqrydate) {
        this.lastqrydate = lastqrydate;
    }

    public String getDef1() {
        return def1;
    }

    public void setDef1(String def1) {
        this.def1 = def1;
    }

    public String getDef2() {
        return def2;
    }

    public void setDef2(String def2) {
        this.def2 = def2;
    }

    public String getDef3() {
        return def3;
    }

    public void setDef3(String def3) {
        this.def3 = def3;
    }

    public String getDef4() {
        return def4;
    }

    public void setDef4(String def4) {
        this.def4 = def4;
    }

    public String getDef5() {
        return def5;
    }

    public void setDef5(String def5) {
        this.def5 = def5;
    }

    @Override
    public String getPKFieldName() {
        return "pk_krydzfrelation";
    }

    @Override
    public String getParentPKFieldName() {
        return null;
    }

    @Override
    public String getTableName() {
        return "ynt_krydzfrelation";
    }
}