package com.dzf.zxkj.platform.vo.tax.jiangsutaxrpt.taxrequest.financialtax.ordinary;

import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.TaxExcelPos;

@TaxExcelPos(
        reportID = "C2002",
        reportname = "利润表",
        rowBegin = 4,
        rowEnd = 41,
        col = 0
)
public class ProfitStatement01 {
    @TaxExcelPos(
            col = 0
    )
    private String xmmc1;//项目名称
    @TaxExcelPos(
            expression = "this.getXH()"
    )
    private String mxxh;//项目序号
    @TaxExcelPos(
            col = 2
    )
    private String hc1;//行次
    @TaxExcelPos(
            col = 3
    )
    private DZFDouble bqje;//本期金额
    @TaxExcelPos(
            col = 4
    )
    private DZFDouble sqje;//上期金额

    public String getXmmc1() {
        return xmmc1;
    }

    public void setXmmc1(String xmmc1) {
        this.xmmc1 = xmmc1;
    }

    public String getMxxh() {
        return mxxh;
    }

    public void setMxxh(String mxxh) {
        this.mxxh = mxxh;
    }

    public String getHc1() {
        return hc1;
    }

    public void setHc1(String hc1) {
        this.hc1 = hc1;
    }

    public DZFDouble getBqje() {
        return bqje;
    }

    public void setBqje(DZFDouble bqje) {
        this.bqje = bqje;
    }

    public DZFDouble getSqje() {
        return sqje;
    }

    public void setSqje(DZFDouble sqje) {
        this.sqje = sqje;
    }
}
