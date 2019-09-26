package com.dzf.zxkj.platform.model.tax;

// 税种结转状态
public class TaxStatusVO {
    private Boolean carryover;
    private Boolean saved;
    private String voucherID;
    private String voucherNumber;

    public Boolean getCarryover() {
        return carryover;
    }

    public void setCarryover(Boolean carryover) {
        this.carryover = carryover;
    }

    public Boolean getSaved() {
        return saved;
    }

    public void setSaved(Boolean saved) {
        this.saved = saved;
    }

    public String getVoucherID() {
        return voucherID;
    }

    public void setVoucherID(String voucherID) {
        this.voucherID = voucherID;
    }

    public String getVoucherNumber() {
        return voucherNumber;
    }

    public void setVoucherNumber(String voucherNumber) {
        this.voucherNumber = voucherNumber;
    }
}
