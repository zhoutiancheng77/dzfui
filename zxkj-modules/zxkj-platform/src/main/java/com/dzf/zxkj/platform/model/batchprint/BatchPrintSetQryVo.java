package com.dzf.zxkj.platform.model.batchprint;

import com.dzf.zxkj.common.model.SuperVO;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 归档任务成功查询
 */
public class BatchPrintSetQryVo extends SuperVO {

    @JsonProperty("cid")
    private String pk_corp;

    @JsonProperty("khmc")
    private String cname;

    private String gz_bs;// 关账标识

    private String pz_bs;// 凭证标识

    private String fsye_bs;// 发生额余额表标识

    private String xjrj_bs;// 现金银行日记账标识

    private String kmzz_bs;// 科目总账标识

    private  String kmmx_bs;// 科目明细账标识

    private String zcfz_bs;// 资产负债表标识

    private String lrb_bs;// 利润表标识

    private String mly_bs;// 目录页标识

    private String pzfp_bs;// 凭证封皮标识

    private String mxzfp_bs;// 总账明细账封皮

    public String getPk_corp() {
        return pk_corp;
    }

    public void setPk_corp(String pk_corp) {
        this.pk_corp = pk_corp;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public String getPz_bs() {
        return pz_bs;
    }

    public void setPz_bs(String pz_bs) {
        this.pz_bs = pz_bs;
    }

    public String getFsye_bs() {
        return fsye_bs;
    }

    public void setFsye_bs(String fsye_bs) {
        this.fsye_bs = fsye_bs;
    }

    public String getXjrj_bs() {
        return xjrj_bs;
    }

    public void setXjrj_bs(String xjrj_bs) {
        this.xjrj_bs = xjrj_bs;
    }

    public String getKmzz_bs() {
        return kmzz_bs;
    }

    public void setKmzz_bs(String kmzz_bs) {
        this.kmzz_bs = kmzz_bs;
    }

    public String getKmmx_bs() {
        return kmmx_bs;
    }

    public void setKmmx_bs(String kmmx_bs) {
        this.kmmx_bs = kmmx_bs;
    }

    public String getZcfz_bs() {
        return zcfz_bs;
    }

    public void setZcfz_bs(String zcfz_bs) {
        this.zcfz_bs = zcfz_bs;
    }

    public String getLrb_bs() {
        return lrb_bs;
    }

    public void setLrb_bs(String lrb_bs) {
        this.lrb_bs = lrb_bs;
    }

    public String getMly_bs() {
        return mly_bs;
    }

    public void setMly_bs(String mly_bs) {
        this.mly_bs = mly_bs;
    }

    public String getPzfp_bs() {
        return pzfp_bs;
    }

    public void setPzfp_bs(String pzfp_bs) {
        this.pzfp_bs = pzfp_bs;
    }

    public String getGz_bs() {
        return gz_bs;
    }

    public void setGz_bs(String gz_bs) {
        this.gz_bs = gz_bs;
    }

    public String getMxzfp_bs() {
        return mxzfp_bs;
    }

    public void setMxzfp_bs(String mxzfp_bs) {
        this.mxzfp_bs = mxzfp_bs;
    }

    @Override
    public String getPKFieldName() {
        return null;
    }

    @Override
    public String getParentPKFieldName() {
        return null;
    }

    @Override
    public String getTableName() {
        return null;
    }
}
