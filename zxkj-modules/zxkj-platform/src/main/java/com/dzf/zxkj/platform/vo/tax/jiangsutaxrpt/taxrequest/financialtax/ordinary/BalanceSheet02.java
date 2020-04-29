package com.dzf.zxkj.platform.vo.tax.jiangsutaxrpt.taxrequest.financialtax.ordinary;

import com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.TaxExcelPos;

@TaxExcelPos(
        reportID = "C2001",
        reportname = "资产负债表",
        rowBegin = 0,
        rowEnd = 0
)
public class BalanceSheet02 {

    private  String sqr;
    private  String dlrsfzjhm;
    private  String dlrmc;
    private  String smr;

    public String getSqr() {
        return sqr;
    }

    public void setSqr(String sqr) {
        this.sqr = sqr;
    }

    public String getDlrsfzjhm() {
        return dlrsfzjhm;
    }

    public void setDlrsfzjhm(String dlrsfzjhm) {
        this.dlrsfzjhm = dlrsfzjhm;
    }

    public String getDlrmc() {
        return dlrmc;
    }

    public void setDlrmc(String dlrmc) {
        this.dlrmc = dlrmc;
    }

    public String getSmr() {
        return smr;
    }

    public void setSmr(String smr) {
        this.smr = smr;
    }
}
