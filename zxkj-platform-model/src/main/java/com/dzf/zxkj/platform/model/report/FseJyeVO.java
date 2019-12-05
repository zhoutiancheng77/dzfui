package com.dzf.zxkj.platform.model.report;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDouble;

/**
 * 发生余额表
 *
 * @author zhangj
 */
public class FseJyeVO extends SuperVO {

    // 打印时 标题区间
    private String titlePeriod;

    private String gs;

    /**
     * 币种pk
     */
    private String pk_currency;
    /**
     * 币种名称
     */
    private String currency;
    private String pk_km;
    private String pk_km_parent;//父科目id
    private String kmlb;
    private String kmbm;
    private String kmmc;
    private String fx;
    private Integer index;
    private DZFDouble ncjf;
    private DZFDouble ncdf;//年初借方
    private DZFDouble qcjf;//年初贷方
    private DZFDouble qcdf;
    private DZFDouble fsjf;
    private DZFDouble fsdf;
    private DZFDouble qmjf;
    private DZFDouble qmdf;
    //----------原币
    private DZFDouble ybncjf;
    private DZFDouble ybncdf;//年初借方
    private DZFDouble ybqcjf;//年初贷方
    private DZFDouble ybqcdf;
    private DZFDouble ybfsjf;
    private DZFDouble ybfsdf;
    private DZFDouble ybqmjf;
    private DZFDouble ybqmdf;
    private Integer direction;
    private Integer bs;
    private String rq;// 期间

    private String bsh;// 凭证数
    private String bills;// 附单据数
    private String pzqj;// 凭证期间

    private DZFDouble endfsjf;
    private DZFDouble endfsdf;

    private DZFDouble jftotal;// 本年累计发生借方
    private DZFDouble dftotal;// 本年累计发生贷方

    //-----------原币
    private DZFDouble ybendfsjf;
    private DZFDouble ybendfsdf;

    private DZFDouble ybjftotal;// 本年累计发生借方
    private DZFDouble ybdftotal;// 本年累计发生贷方

    // 当前区间最后一个月的期初，发生
    private DZFDouble lastmqcjf;
    private DZFDouble lastmqcdf;
    private DZFDouble lastmfsjf;
    private DZFDouble lastmfsdf;

    // 科目层次
    private Integer alevel;
    private String endrq;

    private String fzlbcode;//辅助类别编码（1_2_3等）

    public String getPk_km_parent() {
        return pk_km_parent;
    }

    public void setPk_km_parent(String pk_km_parent) {
        this.pk_km_parent = pk_km_parent;
    }

    public String getPk_currency() {
        return pk_currency;
    }

    public void setPk_currency(String pk_currency) {
        this.pk_currency = pk_currency;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getFzlbcode() {
        return fzlbcode;
    }

    public void setFzlbcode(String fzlbcode) {
        this.fzlbcode = fzlbcode;
    }

    public DZFDouble getYbncjf() {
        return ybncjf;
    }

    public void setYbncjf(DZFDouble ybncjf) {
        this.ybncjf = ybncjf;
    }

    public DZFDouble getYbncdf() {
        return ybncdf;
    }

    public void setYbncdf(DZFDouble ybncdf) {
        this.ybncdf = ybncdf;
    }

    public DZFDouble getYbqcjf() {
        return ybqcjf;
    }

    public void setYbqcjf(DZFDouble ybqcjf) {
        this.ybqcjf = ybqcjf;
    }

    public DZFDouble getYbqcdf() {
        return ybqcdf;
    }

    public void setYbqcdf(DZFDouble ybqcdf) {
        this.ybqcdf = ybqcdf;
    }

    public DZFDouble getYbfsjf() {
        return ybfsjf;
    }

    public void setYbfsjf(DZFDouble ybfsjf) {
        this.ybfsjf = ybfsjf;
    }

    public DZFDouble getYbfsdf() {
        return ybfsdf;
    }

    public void setYbfsdf(DZFDouble ybfsdf) {
        this.ybfsdf = ybfsdf;
    }

    public DZFDouble getYbqmjf() {
        return ybqmjf;
    }

    public void setYbqmjf(DZFDouble ybqmjf) {
        this.ybqmjf = ybqmjf;
    }

    public DZFDouble getYbqmdf() {
        return ybqmdf;
    }

    public void setYbqmdf(DZFDouble ybqmdf) {
        this.ybqmdf = ybqmdf;
    }

    public DZFDouble getYbendfsjf() {
        return ybendfsjf;
    }

    public void setYbendfsjf(DZFDouble ybendfsjf) {
        this.ybendfsjf = ybendfsjf;
    }

    public DZFDouble getYbendfsdf() {
        return ybendfsdf;
    }

    public void setYbendfsdf(DZFDouble ybendfsdf) {
        this.ybendfsdf = ybendfsdf;
    }

    public DZFDouble getYbjftotal() {
        return ybjftotal;
    }

    public void setYbjftotal(DZFDouble ybjftotal) {
        this.ybjftotal = ybjftotal;
    }

    public DZFDouble getYbdftotal() {
        return ybdftotal;
    }

    public void setYbdftotal(DZFDouble ybdftotal) {
        this.ybdftotal = ybdftotal;
    }

    public DZFDouble getNcjf() {
        return ncjf;
    }

    public void setNcjf(DZFDouble ncjf) {
        this.ncjf = ncjf;
    }

    public DZFDouble getNcdf() {
        return ncdf;
    }

    public void setNcdf(DZFDouble ncdf) {
        this.ncdf = ncdf;
    }

    public String getTitlePeriod() {
        return titlePeriod;
    }

    public void setTitlePeriod(String titlePeriod) {
        this.titlePeriod = titlePeriod;
    }

    public String getGs() {
        return gs;
    }

    public void setGs(String gs) {
        this.gs = gs;
    }

    public String getRq() {
        return rq;
    }

    public void setRq(String rq) {
        this.rq = rq;
    }

    public Integer getDirection() {
        return direction;
    }

    public void setDirection(Integer direction) {
        this.direction = direction;
    }

    public String getEndrq() {
        return endrq;
    }

    public void setEndrq(String endrq) {
        this.endrq = endrq;
    }

    public DZFDouble getEndfsjf() {
        return endfsjf;
    }

    public void setEndfsjf(DZFDouble endfsjf) {
        this.endfsjf = endfsjf;
    }

    public DZFDouble getEndfsdf() {
        return endfsdf;
    }

    public void setEndfsdf(DZFDouble endfsdf) {
        this.endfsdf = endfsdf;
    }

    public DZFDouble getJftotal() {
        return jftotal;
    }

    public void setJftotal(DZFDouble jftotal) {
        this.jftotal = jftotal;
    }

    public DZFDouble getDftotal() {
        return dftotal;
    }

    public void setDftotal(DZFDouble dftotal) {
        this.dftotal = dftotal;
    }

    @Override
    public String getPKFieldName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getParentPKFieldName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getTableName() {
        // TODO Auto-generated method stub
        return null;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public DZFDouble getFsdf() {
        return fsdf;
    }

    public void setFsdf(DZFDouble fsdf) {
        this.fsdf = fsdf;
    }

    public DZFDouble getFsjf() {
        return fsjf;
    }

    public void setFsjf(DZFDouble fsjf) {
        this.fsjf = fsjf;
    }

    public String getKmbm() {
        return kmbm;
    }

    public void setKmbm(String kmbm) {
        this.kmbm = kmbm;
    }

    public String getKmlb() {
        return kmlb;
    }

    public void setKmlb(String kmlb) {
        this.kmlb = kmlb;
    }

    public String getKmmc() {
        return kmmc;
    }

    public void setKmmc(String kmmc) {
        this.kmmc = kmmc;
    }

    public DZFDouble getQcdf() {
        return qcdf;
    }

    public void setQcdf(DZFDouble qcdf) {
        this.qcdf = qcdf;
    }

    public DZFDouble getQcjf() {
        return qcjf;
    }

    public void setQcjf(DZFDouble qcjf) {
        this.qcjf = qcjf;
    }

    public DZFDouble getQmdf() {
        return qmdf;
    }

    public void setQmdf(DZFDouble qmdf) {
        this.qmdf = qmdf;
    }

    public DZFDouble getQmjf() {
        return qmjf;
    }

    public void setQmjf(DZFDouble qmjf) {
        this.qmjf = qmjf;
    }

    public String getPk_km() {
        return pk_km;
    }

    public void setPk_km(String pk_km) {
        this.pk_km = pk_km;
    }

    public String getFx() {
        return fx;
    }

    public void setFx(String fx) {
        this.fx = fx;
    }

    public Integer getAlevel() {
        return alevel;
    }

    public void setAlevel(Integer alevel) {
        this.alevel = alevel;
    }

    public Integer getBs() {
        return bs;
    }

    public void setBs(Integer bs) {
        this.bs = bs;
    }

    public String getBsh() {
        return bsh;
    }

    public void setBsh(String bsh) {
        this.bsh = bsh;
    }

    public String getBills() {
        return bills;
    }

    public void setBills(String bills) {
        this.bills = bills;
    }

    public String getPzqj() {
        return pzqj;
    }

    public void setPzqj(String pzqj) {
        this.pzqj = pzqj;
    }

    public DZFDouble getLastmqcjf() {
        return lastmqcjf;
    }

    public void setLastmqcjf(DZFDouble lastmqcjf) {
        this.lastmqcjf = lastmqcjf;
    }

    public DZFDouble getLastmqcdf() {
        return lastmqcdf;
    }

    public void setLastmqcdf(DZFDouble lastmqcdf) {
        this.lastmqcdf = lastmqcdf;
    }

    public DZFDouble getLastmfsjf() {
        return lastmfsjf;
    }

    public void setLastmfsjf(DZFDouble lastmfsjf) {
        this.lastmfsjf = lastmfsjf;
    }

    public DZFDouble getLastmfsdf() {
        return lastmfsdf;
    }

    public void setLastmfsdf(DZFDouble lastmfsdf) {
        this.lastmfsdf = lastmfsdf;
    }

}
