package com.dzf.zxkj.platform.model.tax.workbench;


import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;

import java.util.Optional;

public class VoucherFseQryVO extends SuperVO {

    private String pk_corp;
    private String period;
    private DZFDouble mny;
    private Integer direction;
    private String vcode;


    public VoucherFseQryVO() {

    }

    public VoucherFseQryVO(String pk_corp, String period, DZFDouble mny, String vcode) {
        this.pk_corp = pk_corp;
        this.period = period;
        this.mny = mny;
        this.vcode = vcode;
    }

    public Double getMnyValue(){
        return Optional.ofNullable(mny).orElse(DZFDouble.ZERO_DBL).doubleValue();
    }

    public String getPk_corp() {
        return pk_corp;
    }

    public void setPk_corp(String pk_corp) {
        this.pk_corp = pk_corp;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public DZFDouble getMny() {
        return mny;
    }

    public void setMny(DZFDouble mny) {
        this.mny = mny;
    }

    public Integer getDirection() {
        return direction;
    }

    public void setDirection(Integer direction) {
        this.direction = direction;
    }

    public String getVcode() {
        return vcode;
    }

    public void setVcode(String vcode) {
        this.vcode = vcode;
    }

    @Override
    public String getParentPKFieldName() {
        return null;
    }

    @Override
    public String getPKFieldName() {
        return null;
    }

    @Override
    public String getTableName() {
        return null;
    }
}
