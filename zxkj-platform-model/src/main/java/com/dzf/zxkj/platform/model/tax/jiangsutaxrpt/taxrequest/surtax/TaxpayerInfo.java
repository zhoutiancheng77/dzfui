package com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.surtax;

import com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.TaxExcelPos;

@TaxExcelPos(reportID = "10516001", reportname = "城建税、教育费附加、地方教育附加税（费）申报表")
public class TaxpayerInfo {
    // 税款所属期限起
    private String skssqq;
    // 单位还是个人(0：单位，1：个体)
    private String sfdwgt;
    // 纳税人识别号（统一社会信用代码）
    private String nsrsbh;
    // 纳税人名称
    private String nsrmc;
    // 联系电话
    private String lxdh;
    // 行业代码
    private String hydm;
    // 登记注册类型代码
    private String djzclxdm;
    // 身份证件号码
    private String sfzjhm;
    // 税款所属期限止
    private String skssqz;
    // 填表日期
    private String tbrq;
    // 身份证件类型代码
    private String sfzjlxdm;

    public String getSkssqq() {
        return skssqq;
    }

    public void setSkssqq(String skssqq) {
        this.skssqq = skssqq;
    }

    public String getSfdwgt() {
        return sfdwgt;
    }

    public void setSfdwgt(String sfdwgt) {
        this.sfdwgt = sfdwgt;
    }

    public String getNsrsbh() {
        return nsrsbh;
    }

    public void setNsrsbh(String nsrsbh) {
        this.nsrsbh = nsrsbh;
    }

    public String getNsrmc() {
        return nsrmc;
    }

    public void setNsrmc(String nsrmc) {
        this.nsrmc = nsrmc;
    }

    public String getLxdh() {
        return lxdh;
    }

    public void setLxdh(String lxdh) {
        this.lxdh = lxdh;
    }

    public String getHydm() {
        return hydm;
    }

    public void setHydm(String hydm) {
        this.hydm = hydm;
    }

    public String getDjzclxdm() {
        return djzclxdm;
    }

    public void setDjzclxdm(String djzclxdm) {
        this.djzclxdm = djzclxdm;
    }

    public String getSfzjhm() {
        return sfzjhm;
    }

    public void setSfzjhm(String sfzjhm) {
        this.sfzjhm = sfzjhm;
    }

    public String getSkssqz() {
        return skssqz;
    }

    public void setSkssqz(String skssqz) {
        this.skssqz = skssqz;
    }

    public String getTbrq() {
        return tbrq;
    }

    public void setTbrq(String tbrq) {
        this.tbrq = tbrq;
    }

    public String getSfzjlxdm() {
        return sfzjlxdm;
    }

    public void setSfzjlxdm(String sfzjlxdm) {
        this.sfzjlxdm = sfzjlxdm;
    }
}
