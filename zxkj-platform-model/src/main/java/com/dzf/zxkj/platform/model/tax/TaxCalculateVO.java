package com.dzf.zxkj.platform.model.tax;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDouble;

import java.util.List;

/**
 * 税收计算
 *
 * @author lbj
 */
public class TaxCalculateVO extends SuperVO {
    /** 增值税 */
    public static int TYPE_ADDTAX = 1;
    /** 附加税 */
    public static int TYPE_SURTAX = 2;
    /** 所得税 */
    public static int TYPE_INCOMETAX = 3;
    private String pk_corp;
    private String period;
    // 进项税
    private List<AddValueTaxVO> intax;
    private List<AddValueTaxDiffVO> intax_diff;
    // 销项税
    private List<AddValueTaxVO> outtax;
    private List<AddValueTaxDiffVO> outtax_diff;
    // 增值税
    private AddValueTaxCalVO addtax_info;
    // 附加税
    private List<SurtaxVO> surtax;
    // 所得税
    private IncomeTaxVO incometax;
    // 期间、结转类型等设置
    private TaxSettingVO settings;
    // 税种状态
    // 增值税
    private TaxStatusVO addValueTaxStatus;
    private TaxStatusVO surtaxStatus;
    private TaxStatusVO incomeTaxStatus;
    // 主营业务收入
    private DZFDouble incomeMny;
    private Boolean shouldRefetch;

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

    public List<AddValueTaxVO> getIntax() {
        return intax;
    }

    public void setIntax(List<AddValueTaxVO> intax) {
        this.intax = intax;
    }

    public List<AddValueTaxDiffVO> getIntax_diff() {
        return intax_diff;
    }

    public void setIntax_diff(List<AddValueTaxDiffVO> intax_diff) {
        this.intax_diff = intax_diff;
    }

    public List<AddValueTaxVO> getOuttax() {
        return outtax;
    }

    public void setOuttax(List<AddValueTaxVO> outtax) {
        this.outtax = outtax;
    }

    public List<AddValueTaxDiffVO> getOuttax_diff() {
        return outtax_diff;
    }

    public void setOuttax_diff(List<AddValueTaxDiffVO> outtax_diff) {
        this.outtax_diff = outtax_diff;
    }

    public AddValueTaxCalVO getAddtax_info() {
        return addtax_info;
    }

    public void setAddtax_info(AddValueTaxCalVO addtax_info) {
        this.addtax_info = addtax_info;
    }

    public List<SurtaxVO> getSurtax() {
        return surtax;
    }

    public void setSurtax(List<SurtaxVO> surtax) {
        this.surtax = surtax;
    }

    public IncomeTaxVO getIncometax() {
        return incometax;
    }

    public void setIncometax(IncomeTaxVO incometax) {
        this.incometax = incometax;
    }

    public TaxSettingVO getSettings() {
        return settings;
    }

    public void setSettings(TaxSettingVO settings) {
        this.settings = settings;
    }

    public TaxStatusVO getAddValueTaxStatus() {
        return addValueTaxStatus;
    }

    public void setAddValueTaxStatus(TaxStatusVO addValueTaxStatus) {
        this.addValueTaxStatus = addValueTaxStatus;
    }

    public TaxStatusVO getSurtaxStatus() {
        return surtaxStatus;
    }

    public void setSurtaxStatus(TaxStatusVO surtaxStatus) {
        this.surtaxStatus = surtaxStatus;
    }

    public TaxStatusVO getIncomeTaxStatus() {
        return incomeTaxStatus;
    }

    public void setIncomeTaxStatus(TaxStatusVO incomeTaxStatus) {
        this.incomeTaxStatus = incomeTaxStatus;
    }

    public DZFDouble getIncomeMny() {
        return incomeMny;
    }

    public void setIncomeMny(DZFDouble incomeMny) {
        this.incomeMny = incomeMny;
    }

    public Boolean getShouldRefetch() {
        return shouldRefetch;
    }

    public void setShouldRefetch(Boolean shouldRefetch) {
        this.shouldRefetch = shouldRefetch;
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
