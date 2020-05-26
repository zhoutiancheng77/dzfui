package com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.surtax;

import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.TaxExcelPos;

@TaxExcelPos(reportID = "10516001", reportname = "城建税、教育费附加、地方教育附加税（费）申报表")
public class TaxCredit {
    // 流水号
    private String lsh;

    // 本期是否适用试点建设培养产教融合型企业抵免政策(Y:是，N：否)
    private String bqsfsycjrhxqyjzzc;

    // 当前新增可抵免金额
    private DZFDouble dqxzktme;

    // 上期留抵可抵免金额
    private DZFDouble sqldkdmje;

    // 结转下期可抵免金额
    private DZFDouble jzxqkdmje;

    // 计税依据修改原因
    private String jsyjxgyy;

    // 当期新增投资额
    private DZFDouble dqxztze;

    // 结转比例
    private DZFDouble jzbl;

    public String getLsh() {
        return lsh;
    }

    public void setLsh(String lsh) {
        this.lsh = lsh;
    }

    public String getBqsfsycjrhxqyjzzc() {
        return bqsfsycjrhxqyjzzc;
    }

    public void setBqsfsycjrhxqyjzzc(String bqsfsycjrhxqyjzzc) {
        this.bqsfsycjrhxqyjzzc = bqsfsycjrhxqyjzzc;
    }

    public DZFDouble getDqxzktme() {
        return dqxzktme;
    }

    public void setDqxzktme(DZFDouble dqxzktme) {
        this.dqxzktme = dqxzktme;
    }

    public DZFDouble getSqldkdmje() {
        return sqldkdmje;
    }

    public void setSqldkdmje(DZFDouble sqldkdmje) {
        this.sqldkdmje = sqldkdmje;
    }

    public DZFDouble getJzxqkdmje() {
        return jzxqkdmje;
    }

    public void setJzxqkdmje(DZFDouble jzxqkdmje) {
        this.jzxqkdmje = jzxqkdmje;
    }

    public String getJsyjxgyy() {
        return jsyjxgyy;
    }

    public void setJsyjxgyy(String jsyjxgyy) {
        this.jsyjxgyy = jsyjxgyy;
    }

    public DZFDouble getDqxztze() {
        return dqxztze;
    }

    public void setDqxztze(DZFDouble dqxztze) {
        this.dqxztze = dqxztze;
    }

    public DZFDouble getJzbl() {
        return jzbl;
    }

    public void setJzbl(DZFDouble jzbl) {
        this.jzbl = jzbl;
    }
}
