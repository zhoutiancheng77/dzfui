package com.dzf.zxkj.platform.model.qcset;

import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDouble;

public class SsphRes extends SuperVO implements java.io.Serializable {

    private boolean success = false;
    private String msg = "";
    private DZFDouble yearjf;// 本年借方期初累计;
    private DZFDouble yeardf;// 本年贷方期初累计;
    private DZFDouble yearce;// 差额;
    private String yearres;// 结果;

    private DZFDouble monthjf;// 本月借方期初累计;
    private DZFDouble monthdf;// 本月借方期初累计;
    private DZFDouble monthce;// 差额;
    private String monthres;// 结果;

    private DZFDouble yearlr;// 本年利润+利润分配;
    private DZFDouble yearlrlj;// 净利润本年累计;
    private DZFDouble yearlrce;// 差额;
    private String yearlrres;// 结果;

    public DZFDouble getYearlr() {
        return yearlr;
    }

    public void setYearlr(DZFDouble yearlr) {
        this.yearlr = yearlr;
    }

    public DZFDouble getYearlrlj() {
        return yearlrlj;
    }

    public void setYearlrlj(DZFDouble yearlrlj) {
        this.yearlrlj = yearlrlj;
    }

    public DZFDouble getYearlrce() {
        return yearlrce;
    }

    public void setYearlrce(DZFDouble yearlrce) {
        this.yearlrce = yearlrce;
    }

    public String getYearlrres() {
        return yearlrres;
    }

    public void setYearlrres(String yearlrres) {
        this.yearlrres = yearlrres;
    }

    public DZFDouble getYearjf() {
        return yearjf;
    }

    public void setYearjf(DZFDouble yearjf) {
        this.yearjf = yearjf;
    }

    public DZFDouble getYeardf() {
        return yeardf;
    }

    public void setYeardf(DZFDouble yeardf) {
        this.yeardf = yeardf;
    }

    public DZFDouble getYearce() {
        return yearce;
    }

    public void setYearce(DZFDouble yearce) {
        this.yearce = yearce;
    }

    public String getYearres() {
        return yearres;
    }

    public void setYearres(String yearres) {
        this.yearres = yearres;
    }

    public DZFDouble getMonthjf() {
        return monthjf;
    }

    public void setMonthjf(DZFDouble monthjf) {
        this.monthjf = monthjf;
    }

    public DZFDouble getMonthdf() {
        return monthdf;
    }

    public void setMonthdf(DZFDouble monthdf) {
        this.monthdf = monthdf;
    }

    public DZFDouble getMonthce() {
        return monthce;
    }

    public void setMonthce(DZFDouble monthce) {
        this.monthce = monthce;
    }

    public String getMonthres() {
        return monthres;
    }

    public void setMonthres(String monthres) {
        this.monthres = monthres;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
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

}
