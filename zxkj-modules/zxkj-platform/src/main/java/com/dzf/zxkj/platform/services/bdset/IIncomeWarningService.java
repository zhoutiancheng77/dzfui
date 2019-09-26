package com.dzf.zxkj.platform.services.bdset;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.model.bdset.IncomeWarningVO;
import com.dzf.zxkj.platform.model.report.FseJyeVO;

public interface IIncomeWarningService {


    void save(String[] isLoginRemind, String[] isInputRemind, IncomeWarningVO vo, String pk_corp)
            throws DZFWarpException;

    IncomeWarningVO[] query(String pk_corp) throws DZFWarpException;

    IncomeWarningVO[] queryByPrimaryKey(String primaryKey) throws DZFWarpException;

    void delete(IncomeWarningVO vo) throws DZFWarpException;

    FseJyeVO[] queryFseInfo(IncomeWarningVO[] ivos, String pk_corp, String enddate);

    IncomeWarningVO[] queryIncomeWaringVos(String pk_corp, String period, String filflg) throws DZFWarpException;

    DZFDouble getSpecFsValue(String beginPeriod, String endPeriod,
                             String pk_corp, IncomeWarningVO vo) throws DZFWarpException;
}
