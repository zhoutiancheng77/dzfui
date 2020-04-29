package com.dzf.zxkj.platform.vo.tax.jiangsutaxrpt.taxrequest.financialtax.ordinary;

import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.TaxExcelPos;

@TaxExcelPos(
        reportID = "C2001",
        reportname = "资产负债表",
        rowBegin = 4,
        rowEnd = 43,
        col = 4
)
public class BalanceSheet01 {
    @TaxExcelPos(
            col = 0
    )
    private String xmmc1;//项目名称（资产）
    @TaxExcelPos(
            col = 1
    )
    private String hc1;//资产栏次（资产）
    @TaxExcelPos(
            col = 2
    )
    private DZFDouble qmyezc;//期末余额（资产）
    @TaxExcelPos(
            col = 3
    )
    private DZFDouble snncyezc;//上年年末余额（资产）
    @TaxExcelPos(
            col = 4
    )
    private String xmmc2;//项目名称（负债及所有者权益（或股东权益））
    @TaxExcelPos(
            col = 5
    )
    private String hc2;//行次（负债及所有者权益（或股东权益））
    @TaxExcelPos(
            col = 6
    )
    private DZFDouble qmyeqy;//期末余额（负债及所有者权益（或股东权益））
    @TaxExcelPos(
            col = 7
    )
    private DZFDouble snncyeqy;//上年年末余额（负债及所有者权益（或股东权益））
    @TaxExcelPos(
            expression = "this.getXH()"
    )
    private String mxxh;

    public String getMxxh() {
        return mxxh;
    }

    public void setMxxh(String mxxh) {
        this.mxxh = mxxh;
    }

    public String getXmmc1() {
        return xmmc1;
    }

    public void setXmmc1(String xmmc1) {
        this.xmmc1 = xmmc1;
    }

    public String getHc1() {
        return hc1;
    }

    public void setHc1(String hc1) {
        this.hc1 = hc1;
    }

    public DZFDouble getQmyezc() {
        return qmyezc;
    }

    public void setQmyezc(DZFDouble qmyezc) {
        this.qmyezc = qmyezc;
    }

    public DZFDouble getSnncyezc() {
        return snncyezc;
    }

    public void setSnncyezc(DZFDouble snncyezc) {
        this.snncyezc = snncyezc;
    }

    public String getXmmc2() {
        return xmmc2;
    }

    public void setXmmc2(String xmmc2) {
        this.xmmc2 = xmmc2;
    }

    public String getHc2() {
        return hc2;
    }

    public void setHc2(String hc2) {
        this.hc2 = hc2;
    }

    public DZFDouble getQmyeqy() {
        return qmyeqy;
    }

    public void setQmyeqy(DZFDouble qmyeqy) {
        this.qmyeqy = qmyeqy;
    }

    public DZFDouble getSnncyeqy() {
        return snncyeqy;
    }

    public void setSnncyeqy(DZFDouble snncyeqy) {
        this.snncyeqy = snncyeqy;
    }
}
