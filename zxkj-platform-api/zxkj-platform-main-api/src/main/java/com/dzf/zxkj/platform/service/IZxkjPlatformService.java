package com.dzf.zxkj.platform.service;

import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.query.QueryPageVO;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.platform.model.bdset.*;
import com.dzf.zxkj.platform.model.icset.IcbalanceVO;
import com.dzf.zxkj.platform.model.icset.InventoryVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.pzgl.VoucherParamVO;
import com.dzf.zxkj.platform.model.qcset.QcYeCurrency;
import com.dzf.zxkj.platform.model.qcset.SsphRes;
import com.dzf.zxkj.platform.model.sys.CorpTaxVo;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.model.sys.YntParameterSet;

import java.util.List;
import java.util.Map;

public interface IZxkjPlatformService {

    CorpVO queryCorpByPk(String pk_corp);

    CorpVO[] queryCorpByPks(String[] pk_corps);

    Integer getAccountSchema(String pk_corp);

    IncomeWarningVO[] queryIncomeWarningVOs(String pk_corp);

    IncomeWarningVO[] queryFseInfo(IncomeWarningVO[] ivos, String pk_corp, String enddate);

    String queryAccountRule(String pk_corp);

    String getCurrentCorpAccountSchema(String pk_corp);

    YntCpaccountVO[] queryByPk(String pk_corp);

    Map<String, YntCpaccountVO> queryMapByPk(String pk_corp);

    String getNewRuleCode(String oldCode, String oldrule, String newrule);

    SsphRes qcyeSsph(String pk_corp);

    String[] getNewCodes(String[] oldcode, String oldrule, String newrule);

    Map<String, AuxiliaryAccountBVO> queryAuxiliaryAccountBVOMap(String pk_corp);

//    List<TzpzBVO> queryVoucher(String pk_corp, String account_code, String end_date, String auaccount_detail);
//
//    List<TzpzBVO> queryVoucher(String pk_corp, String account_code, String end_date, String auaccount_detail, String auaccount_type);

    DZFDouble getTaxValue(CorpVO cpvo, String rptname, String period, int[][] zbs);

    AuxiliaryAccountHVO queryHByCode(String pk_corp, String fzlb);

    AuxiliaryAccountHVO[] queryHByPkCorp(String pk_corp);

    AuxiliaryAccountBVO[] queryBByFzlb(String pk_corp, String fzlb);

    AuxiliaryAccountBVO[] queryAllB( String pk_corp);

    BdCurrencyVO queryCurrencyVOByPk(String pk_currency);

    QcYeCurrency[] queryCurrencyByPkCorp(String pk_corp);

    List<InventoryVO> queryInventoryVOs(String pk_corp);

    YntParameterSet queryParamterbyCode(String pk_corp, String code);

    GxhszVO queryGxhszVOByPkCorp(String pk_corp);

    Map<String, String> getNewCodeMap(String[] oldcode, String oldrule, String newrule);

    CorpTaxVo queryCorpTaxVO(String pk_corp);

    YntCpaccountVO queryById(String id);

    Map<String, IcbalanceVO> queryLastBanlanceVOs_byMap1(String currentenddate, String pk_corp, String pk_invtory, boolean isafternonn);

    String queryParamterValueByCode(String pk_corp, String paramcode);

    ReturnData checkQjsy(TzpzHVO headVO);

    TzpzHVO saveVoucher(CorpVO corpvo, TzpzHVO hvo);

    String getNewVoucherNo(String pk_corp, DZFDate doperatedate);

    List<XssrVO> queryXssrVO(String pk_corp);

    UserVO queryUserById(String userId);

    QueryPageVO processQueryVoucherPaged(VoucherParamVO paramvo);

    DZFDouble getQuarterlySdsShui(String pk_corp, String period);

    List<CorpTaxVo> queryTaxVoByParam(QueryParamVO paramvo, UserVO uservo);

    void checkSecurityData(SuperVO[] vos,String[] corps, String cuserid, boolean isCheckData);

}
