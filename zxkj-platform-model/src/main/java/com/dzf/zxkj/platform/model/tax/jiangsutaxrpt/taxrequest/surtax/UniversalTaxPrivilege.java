package com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.surtax;

import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.TaxExcelPos;
import com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.TaxExcelValueCast;

@TaxExcelPos(reportID = "10516001", reportname = "城建税、教育费附加、地方教育附加税（费）申报表")
public class UniversalTaxPrivilege {
    // 流水号
    private String lsh;
    // 本期是否适用增值税小规模减征政策
    @TaxExcelPos(row = 7, col = 6)
    @TaxExcelValueCast
    private String bqsfsyxgmyhzc;

    // 普惠减征比例城建税
    @TaxExcelPos(row = 7, col = 10)
    private DZFDouble phjzblcjs;

    // 普惠减征比例教育附加
    @TaxExcelPos(row = 8, col = 10)
    private DZFDouble phjzbljyfj;

    // 普惠减征比例地方教育附加
    @TaxExcelPos(row = 9, col = 10)
    private DZFDouble phjzbldfjyfj;

    // 本期适用小规模减征政策终止时间
    private String bqsyxgmjzzczzsj;
    // 普惠减免税务事项代码
    private String phjmswsxdm;
    // 普惠减征比例
    private DZFDouble phjzbl;
    // 普惠减免性质名称
    private String phjmxzmc;
    // 本期适用小规模减征政策起始时间
    private String bqsyxgmjzzcqssj;
    // 普惠减免性质代码
    private String phjmxzdm;

    public DZFDouble getPhjzblcjs() {
        return phjzblcjs;
    }

    public void setPhjzblcjs(DZFDouble phjzblcjs) {
        this.phjzblcjs = phjzblcjs;
    }

    public String getBqsyxgmjzzczzsj() {
        return bqsyxgmjzzczzsj;
    }

    public void setBqsyxgmjzzczzsj(String bqsyxgmjzzczzsj) {
        this.bqsyxgmjzzczzsj = bqsyxgmjzzczzsj;
    }

    public String getPhjmswsxdm() {
        return phjmswsxdm;
    }

    public void setPhjmswsxdm(String phjmswsxdm) {
        this.phjmswsxdm = phjmswsxdm;
    }

    public String getLsh() {
        return lsh;
    }

    public void setLsh(String lsh) {
        this.lsh = lsh;
    }

    public DZFDouble getPhjzbljyfj() {
        return phjzbljyfj;
    }

    public void setPhjzbljyfj(DZFDouble phjzbljyfj) {
        this.phjzbljyfj = phjzbljyfj;
    }

    public DZFDouble getPhjzbl() {
        return phjzbl;
    }

    public void setPhjzbl(DZFDouble phjzbl) {
        this.phjzbl = phjzbl;
    }

    public String getPhjmxzmc() {
        return phjmxzmc;
    }

    public void setPhjmxzmc(String phjmxzmc) {
        this.phjmxzmc = phjmxzmc;
    }

    public String getBqsyxgmjzzcqssj() {
        return bqsyxgmjzzcqssj;
    }

    public void setBqsyxgmjzzcqssj(String bqsyxgmjzzcqssj) {
        this.bqsyxgmjzzcqssj = bqsyxgmjzzcqssj;
    }

    public DZFDouble getPhjzbldfjyfj() {
        return phjzbldfjyfj;
    }

    public void setPhjzbldfjyfj(DZFDouble phjzbldfjyfj) {
        this.phjzbldfjyfj = phjzbldfjyfj;
    }

    public String getBqsfsyxgmyhzc() {
        return bqsfsyxgmyhzc;
    }

    public void setBqsfsyxgmyhzc(String bqsfsyxgmyhzc) {
        this.bqsfsyxgmyhzc = bqsfsyxgmyhzc;
    }

    public String getPhjmxzdm() {
        return phjmxzdm;
    }

    public void setPhjmxzdm(String phjmxzdm) {
        this.phjmxzdm = phjmxzdm;
    }
}
