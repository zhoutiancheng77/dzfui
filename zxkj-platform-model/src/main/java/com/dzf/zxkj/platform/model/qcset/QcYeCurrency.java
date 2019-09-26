package com.dzf.zxkj.platform.model.qcset;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.fasterxml.jackson.annotation.JsonProperty;

public class QcYeCurrency extends SuperVO<QcYeCurrency> {
    @JsonProperty("name")
    private String currencyname;
    @JsonProperty("id")
    private String pk_currency;

    private DZFDouble exrate;

    private Integer convmode;// 折算模式[0---原币 乘  1 -----原币 除]

    public String getCurrencyname() {
        return currencyname;
    }

    public void setCurrencyname(String currencyname) {
        this.currencyname = currencyname;
    }

    public String getPk_currency() {
        return pk_currency;
    }

    public void setPk_currency(String pk_currency) {
        this.pk_currency = pk_currency;
    }

    public DZFDouble getExrate() {
        return exrate;
    }

    public void setExrate(DZFDouble exrate) {
        this.exrate = exrate;
    }

    public Integer getConvmode() {
        return convmode;
    }

    public void setConvmode(Integer convmode) {
        this.convmode = convmode;
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
