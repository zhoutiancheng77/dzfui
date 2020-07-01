package com.dzf.zxkj.report.service.jcsz;

import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.platform.model.bdset.*;
import com.dzf.zxkj.platform.model.gzgl.SalaryReportVO;
import com.dzf.zxkj.platform.model.icset.*;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.qcset.QcYeCurrency;
import com.dzf.zxkj.platform.model.qcset.SsphRes;
import com.dzf.zxkj.platform.model.sys.CorpTaxVo;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.model.sys.YntParameterSet;
import com.dzf.zxkj.platform.model.zcgl.AssetDepreciaTionVO;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IJcszService {

    IncomeWarningVO[] queryIncomeWarningVOs(String pk_corp);

    IncomeWarningVO[] queryFseInfo(IncomeWarningVO[] ivos, String pk_corp, String enddate);

    String getCurrentCorpAccountSchema(String pk_corp);

    SsphRes qcyeSsph(String pk_corp);

    DZFDouble getTaxValue(CorpVO cpvo, String rptname, String period, int[][] zbs);


    BdCurrencyVO queryCurrencyVOByPk(String pk_currency);

    QcYeCurrency[] queryCurrencyByPkCorp(String pk_corp);

    List<InventoryVO> queryInventoryVOs(String pk_corp);

    YntParameterSet queryParamterbyCode(String pk_corp, String code);

    GxhszVO queryGxhszVOByPkCorp(String pk_corp);

    CorpTaxVo queryCorpTaxVO(String pk_corp);

    Map<String, IcbalanceVO> queryLastBanlanceVOs_byMap1(String currentenddate, String pk_corp, String pk_invtory, boolean isafternonn);

    String queryParamterValueByCode(String pk_corp, String paramcode);

    void checkQjsy(TzpzHVO headVO);

    UserVO queryUserById(String userId);

    DZFDouble getQuarterlySdsShui(String pk_corp, String period);

    List<CorpTaxVo> queryTaxVoByParam(QueryParamVO paramvo, UserVO uservo);

    SalaryReportVO[] queryGzb(String pk_corp, String beginPeriod, String endPeriod, String billtype);

    List<IctradeinVO> queryTradeIn(QueryParamVO paramvo);

    List<IntradeoutVO> queryTradeOut(QueryParamVO paramvo);

    List<IntradeHVO> queryIntradeHVOOut(IntradeParamVO paramvo);

    IntradeHVO queryIntradeHVOByID(String id, String pk_corp);

    IntradeHVO queryIntradeHVOByIDIn(String pk_ictrade_h, String pk_corp);

    List<IntradeHVO> queryIntradeHVOIn(IntradeParamVO paramvo);

    AssetDepreciaTionVO[] getZczjMxVOs(QueryParamVO queryParamvo);

    Set<String> querypowercorpSet(String userid);

    BdCurrencyVO[] queryCurrency();
}
