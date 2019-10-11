package com.dzf.zxkj.platform.service;

import com.dzf.zxkj.platform.model.bdset.BdtradecashflowVO;
import com.dzf.zxkj.platform.model.bdset.IncomeWarningVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzBVO;
import com.dzf.zxkj.platform.model.qcset.SsphRes;
import com.dzf.zxkj.platform.model.report.XjllQcyeVO;

import java.util.List;

public interface IZxkjPlatformService {
    Integer getAccountSchema(String pk_corp);

    IncomeWarningVO[] queryIncomeWarningVOs(String pk_corp);

    IncomeWarningVO[] queryFseInfo(IncomeWarningVO[] ivos, String pk_corp, String enddate);

    String queryAccountRule(String pk_corp) throws Exception;

    String getCurrentCorpAccountSchema(String pk_corp)
            throws Exception ;
    YntCpaccountVO[] queryByPk(String pk_corp) throws Exception;

    String getNewRuleCode(String oldCode,String oldrule,String newrule)throws Exception;

    BdtradecashflowVO[] queryBdtradecashflowVOList(String pk_trade_accountschema,String hc);

    List<XjllQcyeVO> queryXjllQcyeVOList(String pk_corp, String year);

    List<TzpzBVO> queryTzpzBVObyHVOPk(List<String> tzpzHVOPks);

    SsphRes qcyeSsph(String pk_corp);

    String[] getNewCodes(String[] oldcode,String oldrule,String newrule);
}
