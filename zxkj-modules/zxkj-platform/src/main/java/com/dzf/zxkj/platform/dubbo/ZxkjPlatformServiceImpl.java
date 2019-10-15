package com.dzf.zxkj.platform.dubbo;

import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.model.bdset.*;
import com.dzf.zxkj.platform.model.icset.IcbalanceVO;
import com.dzf.zxkj.platform.model.icset.InventoryVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzBVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.qcset.FzhsqcVO;
import com.dzf.zxkj.platform.model.qcset.SsphRes;
import com.dzf.zxkj.platform.model.report.XjllQcyeVO;
import com.dzf.zxkj.platform.model.sys.CorpTaxVo;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.YntParameterSet;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.platform.service.bdset.ICpaccountCodeRuleService;
import com.dzf.zxkj.platform.service.bdset.ICpaccountService;
import com.dzf.zxkj.platform.service.bdset.IIncomeWarningService;
import com.dzf.zxkj.platform.service.report.impl.YntBoPubUtil;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

@Service(version = "1.0.0", timeout = Integer.MAX_VALUE)
public class ZxkjPlatformServiceImpl implements IZxkjPlatformService {

    @Autowired
    private ICpaccountService gl_cpacckmserv;
    @Autowired
    private ICpaccountCodeRuleService gl_accountcoderule;

    @Autowired
    private YntBoPubUtil yntBoPubUtil;

    @Autowired
    private IIncomeWarningService iw_serv;// 预警信息

    @Override
    public CorpVO queryCorpByPk(String pk_corp) {
        return null;
    }

    @Override
    public Integer getAccountSchema(String pk_corp) {
        return yntBoPubUtil.getAccountSchema(pk_corp);
    }

    @Override
    public IncomeWarningVO[] queryIncomeWarningVOs(String pk_corp) {
        return iw_serv.query(pk_corp);
    }

    @Override
    public IncomeWarningVO[] queryFseInfo(IncomeWarningVO[] ivos, String pk_corp, String enddate) {
        iw_serv.queryFseInfo(ivos, pk_corp, enddate);
        return ivos;
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
    public BdtradecashflowVO[] queryBdtradecashflowVOList(String pk_trade_accountschema, String hc) {
        return new BdtradecashflowVO[0];
    }

    @Override
    public List<XjllQcyeVO> queryXjllQcyeVOList(String pk_corp, String year) {
        return null;
    }

    @Override
    public List<TzpzBVO> queryTzpzBVObyHVOPk(List<String> tzpzHVOPks) {
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
    public List<TzpzBVO> queryVoucher(String pk_corp, String account_code, String end_date, String auaccount_detail) {
        return null;
    }

    @Override
    public List<TzpzBVO> queryVoucher(String pk_corp, String account_code, String end_date, String auaccount_detail, String auaccount_type) {
        return null;
    }

    @Override
    public Map<String, FzhsqcVO> queryFzhsqcMap(String pk_corp, String account_code, String auaccount_type, String auaccount_detail) {
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
    public BdCurrencyVO queryCurrencyVOByPk(String pk_currency) {
        return null;
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

}
