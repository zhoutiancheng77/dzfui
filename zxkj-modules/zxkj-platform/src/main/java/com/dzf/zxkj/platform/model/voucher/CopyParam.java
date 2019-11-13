package com.dzf.zxkj.platform.model.voucher;

import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;

public class CopyParam {
    // 复制期间
    private String sourcePeriod;
    // 公司
    private String corps;
    // 复制到期间
    private String targetPeriod;
    // 复制凭证日期取值 对应日期/指定日期
    private String targetDateType;
    // 指定日期
    private String targetDate;
    // 期间损益时，强制复制
    private String force;
    private TzpzHVO[] sourceVoucher;

    public String getSourcePeriod() {
        return sourcePeriod;
    }

    public void setSourcePeriod(String sourcePeriod) {
        this.sourcePeriod = sourcePeriod;
    }

    public String getCorps() {
        return corps;
    }

    public void setCorps(String corps) {
        this.corps = corps;
    }

    public String getTargetPeriod() {
        return targetPeriod;
    }

    public void setTargetPeriod(String targetPeriod) {
        this.targetPeriod = targetPeriod;
    }

    public String getTargetDateType() {
        return targetDateType;
    }

    public void setTargetDateType(String targetDateType) {
        this.targetDateType = targetDateType;
    }

    public String getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(String targetDate) {
        this.targetDate = targetDate;
    }

    public String getForce() {
        return force;
    }

    public void setForce(String force) {
        this.force = force;
    }

    public TzpzHVO[] getSourceVoucher() {
        return sourceVoucher;
    }

    public void setSourceVoucher(TzpzHVO[] sourceVoucher) {
        this.sourceVoucher = sourceVoucher;
    }
}
