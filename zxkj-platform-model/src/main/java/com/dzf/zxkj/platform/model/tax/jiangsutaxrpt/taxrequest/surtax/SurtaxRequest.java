package com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.surtax;

public class SurtaxRequest {
    // 纳税人信息
    private TaxpayerInfo sb10516001vo_01;
    // 各税种明细
    private SurtaxDetail[] sb10516001vo_02;
    // 代理人人信息
    private AgentInfo sb10516001vo_03;
    // 普惠
    private UniversalTaxPrivilege sb10516001vo_04;
    // 抵免
    private TaxCredit sb10516001vo_05;

    public TaxpayerInfo getSb10516001vo_01() {
        return sb10516001vo_01;
    }

    public void setSb10516001vo_01(TaxpayerInfo sb10516001vo_01) {
        this.sb10516001vo_01 = sb10516001vo_01;
    }

    public SurtaxDetail[] getSb10516001vo_02() {
        return sb10516001vo_02;
    }

    public void setSb10516001vo_02(SurtaxDetail[] sb10516001vo_02) {
        this.sb10516001vo_02 = sb10516001vo_02;
    }

    public AgentInfo getSb10516001vo_03() {
        return sb10516001vo_03;
    }

    public void setSb10516001vo_03(AgentInfo sb10516001vo_03) {
        this.sb10516001vo_03 = sb10516001vo_03;
    }

    public UniversalTaxPrivilege getSb10516001vo_04() {
        return sb10516001vo_04;
    }

    public void setSb10516001vo_04(UniversalTaxPrivilege sb10516001vo_04) {
        this.sb10516001vo_04 = sb10516001vo_04;
    }

    public TaxCredit getSb10516001vo_05() {
        return sb10516001vo_05;
    }

    public void setSb10516001vo_05(TaxCredit sb10516001vo_05) {
        this.sb10516001vo_05 = sb10516001vo_05;
    }
}
