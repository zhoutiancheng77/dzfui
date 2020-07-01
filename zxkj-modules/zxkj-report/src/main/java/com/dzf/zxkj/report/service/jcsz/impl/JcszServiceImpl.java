package com.dzf.zxkj.report.service.jcsz.impl;

import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.query.QueryPageVO;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.platform.model.batchprint.BatchPrintSetVo;
import com.dzf.zxkj.platform.model.bdset.*;
import com.dzf.zxkj.platform.model.gzgl.SalaryReportVO;
import com.dzf.zxkj.platform.model.icset.*;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.pzgl.VoucherParamVO;
import com.dzf.zxkj.platform.model.pzgl.VoucherPrintParam;
import com.dzf.zxkj.platform.model.qcset.QcYeCurrency;
import com.dzf.zxkj.platform.model.qcset.SsphRes;
import com.dzf.zxkj.platform.model.sys.CorpTaxVo;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.model.sys.YntParameterSet;
import com.dzf.zxkj.platform.model.zcgl.AssetDepreciaTionVO;
import com.dzf.zxkj.report.service.jcsz.IJcszService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class JcszServiceImpl implements IJcszService {
    @Override
    public CorpVO queryCorpByPk(String pk_corp) {
        return null;
    }

    @Override
    public Integer getAccountSchema(String pk_corp) {
        return null;
    }

    @Override
    public IncomeWarningVO[] queryIncomeWarningVOs(String pk_corp) {
        return new IncomeWarningVO[0];
    }

    @Override
    public IncomeWarningVO[] queryFseInfo(IncomeWarningVO[] ivos, String pk_corp, String enddate) {
        return new IncomeWarningVO[0];
    }

    @Override
    public String queryAccountRule(String pk_corp) {
        return null;
    }

    @Override
    public String getCurrentCorpAccountSchema(String pk_corp) {
        return null;
    }

    @Override
    public YntCpaccountVO[] queryByPk(String pk_corp) {
        return new YntCpaccountVO[0];
    }

    @Override
    public Map<String, YntCpaccountVO> queryMapByPk(String pk_corp) {
        return null;
    }

    @Override
    public String getNewRuleCode(String oldCode, String oldrule, String newrule) {
        return null;
    }

    @Override
    public SsphRes qcyeSsph(String pk_corp) {
        return null;
    }

    @Override
    public String[] getNewCodes(String[] oldcode, String oldrule, String newrule) {
        return new String[0];
    }

    @Override
    public Map<String, AuxiliaryAccountBVO> queryAuxiliaryAccountBVOMap(String pk_corp) {
        return null;
    }

    @Override
    public DZFDouble getTaxValue(CorpVO cpvo, String rptname, String period, int[][] zbs) {
        return null;
    }

    @Override
    public AuxiliaryAccountHVO queryHByCode(String pk_corp, String fzlb) {
        return null;
    }

    @Override
    public AuxiliaryAccountHVO[] queryHByPkCorp(String pk_corp) {
        return new AuxiliaryAccountHVO[0];
    }

    @Override
    public AuxiliaryAccountBVO[] queryBByFzlb(String pk_corp, String fzlb) {
        return new AuxiliaryAccountBVO[0];
    }

    @Override
    public AuxiliaryAccountBVO[] queryAllB(String pk_corp) {
        return new AuxiliaryAccountBVO[0];
    }

    @Override
    public BdCurrencyVO queryCurrencyVOByPk(String pk_currency) {
        return null;
    }

    @Override
    public QcYeCurrency[] queryCurrencyByPkCorp(String pk_corp) {
        return new QcYeCurrency[0];
    }

    @Override
    public List<InventoryVO> queryInventoryVOs(String pk_corp) {
        return null;
    }

    @Override
    public YntParameterSet queryParamterbyCode(String pk_corp, String code) {
        return null;
    }

    @Override
    public GxhszVO queryGxhszVOByPkCorp(String pk_corp) {
        return null;
    }

    @Override
    public Map<String, String> getNewCodeMap(String[] oldcode, String oldrule, String newrule) {
        return null;
    }

    @Override
    public CorpTaxVo queryCorpTaxVO(String pk_corp) {
        return null;
    }

    @Override
    public YntCpaccountVO queryById(String id) {
        return null;
    }

    @Override
    public Map<String, IcbalanceVO> queryLastBanlanceVOs_byMap1(String currentenddate, String pk_corp, String pk_invtory, boolean isafternonn) {
        return null;
    }

    @Override
    public String queryParamterValueByCode(String pk_corp, String paramcode) {
        return null;
    }

    @Override
    public ReturnData checkQjsy(TzpzHVO headVO) {
        return null;
    }

    @Override
    public TzpzHVO saveVoucher(CorpVO corpvo, TzpzHVO hvo) {
        return null;
    }

    @Override
    public String getNewVoucherNo(String pk_corp, DZFDate doperatedate) {
        return null;
    }

    @Override
    public List<XssrVO> queryXssrVO(String pk_corp) {
        return null;
    }

    @Override
    public UserVO queryUserById(String userId) {
        return null;
    }

    @Override
    public QueryPageVO processQueryVoucherPaged(VoucherParamVO paramvo) {
        return null;
    }

    @Override
    public DZFDouble getQuarterlySdsShui(String pk_corp, String period) {
        return null;
    }

    @Override
    public List<CorpTaxVo> queryTaxVoByParam(QueryParamVO paramvo, UserVO uservo) {
        return null;
    }

    @Override
    public SalaryReportVO[] queryGzb(String pk_corp, String beginPeriod, String endPeriod, String billtype) {
        return new SalaryReportVO[0];
    }

    @Override
    public List<IctradeinVO> queryTradeIn(QueryParamVO paramvo) {
        return null;
    }

    @Override
    public List<IntradeoutVO> queryTradeOut(QueryParamVO paramvo) {
        return null;
    }

    @Override
    public List<IntradeHVO> queryIntradeHVOOut(IntradeParamVO paramvo) {
        return null;
    }

    @Override
    public IntradeHVO queryIntradeHVOByID(String id, String pk_corp) {
        return null;
    }

    @Override
    public IntradeHVO queryIntradeHVOByIDIn(String pk_ictrade_h, String pk_corp) {
        return null;
    }

    @Override
    public List<IntradeHVO> queryIntradeHVOIn(IntradeParamVO paramvo) {
        return null;
    }

    @Override
    public AssetDepreciaTionVO[] getZczjMxVOs(QueryParamVO queryParamvo) {
        return new AssetDepreciaTionVO[0];
    }

    @Override
    public Set<String> querypowercorpSet(String userid) {
        return null;
    }

    @Override
    public BdCurrencyVO[] queryCurrency() {
        return new BdCurrencyVO[0];
    }

    @Override
    public List<TzpzHVO> queryByIDs(String ids, VoucherPrintParam param) {
        return null;
    }

    @Override
    public byte[] execPzCoverTask(BatchPrintSetVo setvo, UserVO userVO, CorpVO corpVO) {
        return new byte[0];
    }

    @Override
    public byte[] execPzTask(BatchPrintSetVo setvo, UserVO userVO, CorpVO corpVO) {
        return new byte[0];
    }

    @Override
    public byte[] execCrkPrintTask(BatchPrintSetVo setvo, UserVO userVO, CorpVO corpVO) {
        return new byte[0];
    }
}
