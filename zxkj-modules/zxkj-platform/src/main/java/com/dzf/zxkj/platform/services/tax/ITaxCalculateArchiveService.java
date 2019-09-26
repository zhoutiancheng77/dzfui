package com.dzf.zxkj.platform.services.tax;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.jzcl.SurTaxTemplate;
import com.dzf.zxkj.platform.model.report.FseJyeVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.tax.SurtaxArchiveVO;
import com.dzf.zxkj.platform.model.tax.SurtaxVO;
import com.dzf.zxkj.platform.model.tax.TaxCalculateArchiveVO;
import com.dzf.zxkj.platform.model.tax.TaxCalculateVO;

import java.util.List;

public interface ITaxCalculateArchiveService {

    TaxCalculateArchiveVO[] query(String pk_corp)
            throws DZFWarpException;

    void save(String pk_corp, TaxCalculateArchiveVO[] taxs)
            throws DZFWarpException;

    List<FseJyeVO> getTaxAmount(CorpVO corpVO, String period)
            throws DZFWarpException;

    TaxCalculateVO getTax(String pk_corp, String period, boolean reFetch)
            throws DZFWarpException;

    TaxCalculateVO saveTax(TaxCalculateVO taxCalVO, String pk_corp, String userID)
            throws DZFWarpException;

    SurtaxArchiveVO[] getOtherTaxArchives();

    SurTaxTemplate[] querySurtaxTemplate(String pk_corp);

    SurTaxTemplate saveSurtaxTemplate(String pk_corp, SurTaxTemplate temp);

    TaxCalculateVO createVoucher(TaxCalculateVO taxCalVO, Integer taxType, String pk_corp, String userID)
            throws DZFWarpException;

    SurtaxVO createVoucherByOtherTax(SurtaxVO taxVO, String pk_corp, String userID)
            throws DZFWarpException;

    SurtaxVO saveOtherTax(SurtaxVO taxVO, String pk_corp, String period, String userID)
            throws DZFWarpException;
    void updateOtherTaxOnVoucherDelete(String id) throws DZFWarpException;

    /**
     *
     * 反增值税结转时更新税费计算状态
     * @param pk_corp
     * @param period
     * @throws DZFWarpException
     */
    void updateAddTaxUnCarryover(String pk_corp, String period) throws DZFWarpException;

    /**
     *
     * 反附加税结转时更新税费计算状态
     * @param pk_corp
     * @param period
     * @throws DZFWarpException
     */
    void updateSurtaxUnCarryover(String pk_corp, String period) throws DZFWarpException;

    /**
     *
     * 反所得税结转时更新税费计算状态
     * @param pk_corp
     * @param period
     * @throws DZFWarpException
     */
    void updateIncomeTaxUnCarryover(String pk_corp, String period) throws DZFWarpException;
}
