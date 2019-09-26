package com.dzf.zxkj.platform.model.tax;

import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;

public class TaxSettingVO {
    /** 月报 */
    public static int PERIOD_MONTH = 0;
    /** 季报 */
    public static int PERIOD_QUARTER = 1;
    // 增值税期间类型
    private Integer addTaxPeriodType;
    // 所得税期间类型
    private Integer incomeTaxPeriodType;
    // 所得税征收方式
    private Integer incomeTaxLevyType;
    // 所得税类型
    private Integer incomeTaxType;
    // 开始生产经营日期
    private DZFDate start_production_date;
    // 所得税核对税率
    private DZFDouble incomeTaxFixedRate;
    // 所得税优惠政策
    private String incomeTaxPreferPolicy;
    // 城建税
    private DZFDouble cityBuildRate;
    // 地方教育费附加
    private DZFDouble localEduRate;

    public Integer getAddTaxPeriodType() {
        return addTaxPeriodType;
    }

    public void setAddTaxPeriodType(Integer addTaxPeriodType) {
        this.addTaxPeriodType = addTaxPeriodType;
    }

    public Integer getIncomeTaxPeriodType() {
        return incomeTaxPeriodType;
    }

    public void setIncomeTaxPeriodType(Integer incomeTaxPeriodType) {
        this.incomeTaxPeriodType = incomeTaxPeriodType;
    }

    public Integer getIncomeTaxLevyType() {
        return incomeTaxLevyType;
    }

    public void setIncomeTaxLevyType(Integer incomeTaxLevyType) {
        this.incomeTaxLevyType = incomeTaxLevyType;
    }

    public Integer getIncomeTaxType() {
        return incomeTaxType;
    }

    public void setIncomeTaxType(Integer incomeTaxType) {
        this.incomeTaxType = incomeTaxType;
    }

    public DZFDate getStart_production_date() {
        return start_production_date;
    }

    public void setStart_production_date(DZFDate start_production_date) {
        this.start_production_date = start_production_date;
    }

    public DZFDouble getIncomeTaxFixedRate() {
        return incomeTaxFixedRate;
    }

    public void setIncomeTaxFixedRate(DZFDouble incomeTaxFixedRate) {
        this.incomeTaxFixedRate = incomeTaxFixedRate;
    }

    public String getIncomeTaxPreferPolicy() {
        return incomeTaxPreferPolicy;
    }

    public void setIncomeTaxPreferPolicy(String incomeTaxPreferPolicy) {
        this.incomeTaxPreferPolicy = incomeTaxPreferPolicy;
    }

    public DZFDouble getCityBuildRate() {
        return cityBuildRate;
    }

    public void setCityBuildRate(DZFDouble cityBuildRate) {
        this.cityBuildRate = cityBuildRate;
    }

    public DZFDouble getLocalEduRate() {
        return localEduRate;
    }

    public void setLocalEduRate(DZFDouble localEduRate) {
        this.localEduRate = localEduRate;
    }
}
