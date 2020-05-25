package com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.surtax;

import com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.TaxExcelPos;

@TaxExcelPos(reportID = "10516001", reportname = "城建税、教育费附加、地方教育附加税（费）申报表")
public class AgentInfo {
    // 流水号
    private String lsh;
    // 经办人
    private String sqr;
    // 经办人身份证号码
    private String dlrsfzjhm;
    // 代理机构名称
    private String dlrmc;
    // 代理机构统一社会信用代码
    private String smr;

    public String getLsh() {
        return lsh;
    }

    public void setLsh(String lsh) {
        this.lsh = lsh;
    }

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
