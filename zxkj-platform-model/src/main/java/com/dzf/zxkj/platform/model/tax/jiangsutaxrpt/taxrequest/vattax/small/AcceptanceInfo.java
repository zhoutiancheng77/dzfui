package com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.vattax.small;

import com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.TaxExcelPos;

//增值税纳税申报表（适用小规模纳税人）Data1_02
@TaxExcelPos(reportID = "10102001", reportname = "增值税纳税申报表")
public class AcceptanceInfo {
    // 是否自行申报（Y是，N否）【必填】
    @TaxExcelPos(row = 30, col = 2)
    private String sfdlsb;
    // 代理人名称
    @TaxExcelPos(row = 31, col = 2)
    private String dlrmc;
    // 办理人员身份证件类型代码
    @TaxExcelPos(row = 32, col = 2, splitIndex = 0)
    private String dlrsfzjzldm;
    // 办理人员身份证件类型名称
    @TaxExcelPos(row = 32, col = 2, splitIndex = 1)
    private String dlrsfzjzlmc;
    // 办理人员身份证件号码
    @TaxExcelPos(row = 32, col = 6)
    private String dlrsfzjhm;
    // 经办人姓名
    private String sqr;
    // 受理人姓名
    private String slr;
    // 接收日期
    @TaxExcelPos(row = 31, col = 6)
    private String slrq;

    public String getSfdlsb() {
        return sfdlsb;
    }

    public void setSfdlsb(String sfdlsb) {
        if ("是".equals(sfdlsb)) {
            sfdlsb = "Y";
        } else if ("否".equals(sfdlsb)) {
            sfdlsb = "N";
        }
        this.sfdlsb = sfdlsb;
    }

    public String getDlrsfzjzlmc() {
        return dlrsfzjzlmc;
    }

    public void setDlrsfzjzlmc(String dlrsfzjzlmc) {
        this.dlrsfzjzlmc = dlrsfzjzlmc;
    }

    public String getDlrmc() {
        return dlrmc;
    }

    public void setDlrmc(String dlrmc) {
        this.dlrmc = dlrmc;
    }

    public String getDlrsfzjzldm() {
        return dlrsfzjzldm;
    }

    public void setDlrsfzjzldm(String dlrsfzjzldm) {
        this.dlrsfzjzldm = dlrsfzjzldm;
    }

    public String getDlrsfzjhm() {
        return dlrsfzjhm;
    }

    public void setDlrsfzjhm(String dlrsfzjhm) {
        this.dlrsfzjhm = dlrsfzjhm;
    }

    public String getSqr() {
        return sqr;
    }

    public void setSqr(String sqr) {
        this.sqr = sqr;
    }

    public String getSlr() {
        return slr;
    }

    public void setSlr(String slr) {
        this.slr = slr;
    }

    public String getSlrq() {
        return slrq;
    }

    public void setSlrq(String slrq) {
        this.slrq = slrq;
    }
}
