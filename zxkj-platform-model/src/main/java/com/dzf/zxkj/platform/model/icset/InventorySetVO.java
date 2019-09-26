package com.dzf.zxkj.platform.model.icset;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 存货设置VO
 *
 * @author zpm
 */
public class InventorySetVO extends SuperVO {

    //主键
    private String pk_glicset;

    @JsonProperty("corpid")
    private String pk_corp;//公司

    private Integer dr;//

    private DZFDateTime ts;

    private int chcbjzfs = -1;//存货成本结转方式
    private int chppjscgz = 0; //存货匹配规则  默认为 存货名称+规格型号+计量单位
    private String zgrkdfkm;//暂估入库贷方科目;
    private String zgkhfz;//供应商辅助;

    private String kcsprkkm;//库存商品生成凭证入库科目
    private String kcspckkm;//库存商品生成凭证出库科目

    private String yclrkkm;//原材料生成凭证入库科目
    private String yclckkm;//原材料生成凭证出库科目

    private String lwrkkm;//提供劳务生成凭证入库科目
    private String lwckkm;//提供劳务生成凭证出库科目

    private String yingshoukm;// 应收
    private String yingfukm;//   应付
    private String yinhangkm;//  银行
    private String xianjinkm;//  现金
    private String jxshuiekm;//  进项税额
    private String xxshuiekm;//  销项税额


    private String def1;
    private String def2;
    private String def3;
    private String def4;
    private String def5;

    private String errorinfo;
    private String loginfo;//操作日志信息

    public int getChcbjzfs() {
        return chcbjzfs;
    }

    public void setChcbjzfs(int chcbjzfs) {
        this.chcbjzfs = chcbjzfs;
    }

    public int getChppjscgz() {
        return chppjscgz;
    }

    public void setChppjscgz(int chppjscgz) {
        this.chppjscgz = chppjscgz;
    }

    public String getZgrkdfkm() {
        return zgrkdfkm;
    }

    public void setZgrkdfkm(String zgrkdfkm) {
        this.zgrkdfkm = zgrkdfkm;
    }

    public String getKcsprkkm() {
        return kcsprkkm;
    }

    public void setKcsprkkm(String kcsprkkm) {
        this.kcsprkkm = kcsprkkm;
    }

    public String getKcspckkm() {
        return kcspckkm;
    }

    public void setKcspckkm(String kcspckkm) {
        this.kcspckkm = kcspckkm;
    }

    public String getYclrkkm() {
        return yclrkkm;
    }

    public void setYclrkkm(String yclrkkm) {
        this.yclrkkm = yclrkkm;
    }

    public String getYclckkm() {
        return yclckkm;
    }

    public void setYclckkm(String yclckkm) {
        this.yclckkm = yclckkm;
    }

    public String getLwrkkm() {
        return lwrkkm;
    }

    public void setLwrkkm(String lwrkkm) {
        this.lwrkkm = lwrkkm;
    }

    public String getLwckkm() {
        return lwckkm;
    }

    public void setLwckkm(String lwckkm) {
        this.lwckkm = lwckkm;
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

    public String getPk_glicset() {
        return pk_glicset;
    }

    public void setPk_glicset(String pk_glicset) {
        this.pk_glicset = pk_glicset;
    }

    public String getPk_corp() {
        return pk_corp;
    }

    public void setPk_corp(String pk_corp) {
        this.pk_corp = pk_corp;
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

    public String getYingshoukm() {
        return yingshoukm;
    }

    public void setYingshoukm(String yingshoukm) {
        this.yingshoukm = yingshoukm;
    }

    public String getYingfukm() {
        return yingfukm;
    }

    public void setYingfukm(String yingfukm) {
        this.yingfukm = yingfukm;
    }

    public String getYinhangkm() {
        return yinhangkm;
    }

    public void setYinhangkm(String yinhangkm) {
        this.yinhangkm = yinhangkm;
    }

    public String getXianjinkm() {
        return xianjinkm;
    }

    public void setXianjinkm(String xianjinkm) {
        this.xianjinkm = xianjinkm;
    }

    public String getJxshuiekm() {
        return jxshuiekm;
    }

    public void setJxshuiekm(String jxshuiekm) {
        this.jxshuiekm = jxshuiekm;
    }

    public String getXxshuiekm() {
        return xxshuiekm;
    }

    public void setXxshuiekm(String xxshuiekm) {
        this.xxshuiekm = xxshuiekm;
    }

    public String getZgkhfz() {
        return zgkhfz;
    }

    public void setZgkhfz(String zgkhfz) {
        this.zgkhfz = zgkhfz;
    }

    public String getErrorinfo() {
        return errorinfo;
    }

    public void setErrorinfo(String errorinfo) {
        this.errorinfo = errorinfo;
    }

    public String getLoginfo() {
        return loginfo;
    }

    public void setLoginfo(String loginfo) {
        this.loginfo = loginfo;
    }

    @Override
    public String getPKFieldName() {
        return "pk_glicset";
    }

    @Override
    public String getParentPKFieldName() {
        return null;
    }

    @Override
    public String getTableName() {
        return "ynt_glicset";
    }
}