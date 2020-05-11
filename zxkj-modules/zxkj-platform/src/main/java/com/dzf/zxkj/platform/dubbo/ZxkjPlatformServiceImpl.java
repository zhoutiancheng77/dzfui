package com.dzf.zxkj.platform.dubbo;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.constant.IVoucherConstants;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.query.QueryPageVO;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.platform.model.bdset.*;
import com.dzf.zxkj.platform.model.gzgl.SalaryReportVO;
import com.dzf.zxkj.platform.model.icset.*;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.pzgl.VoucherParamVO;
import com.dzf.zxkj.platform.model.qcset.QcYeCurrency;
import com.dzf.zxkj.platform.model.qcset.SsphRes;
import com.dzf.zxkj.platform.model.sys.CorpTaxVo;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.model.sys.YntParameterSet;
import com.dzf.zxkj.platform.model.zcgl.AssetDepreciaTionVO;
import com.dzf.zxkj.platform.model.zcgl.ZcMxZVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.platform.service.bdset.*;
import com.dzf.zxkj.platform.service.common.ISecurityService;
import com.dzf.zxkj.platform.service.gzgl.ISalaryReportService;
import com.dzf.zxkj.platform.service.icbill.IPurchInService;
import com.dzf.zxkj.platform.service.icbill.ISaleoutService;
import com.dzf.zxkj.platform.service.icbill.ITradeinService;
import com.dzf.zxkj.platform.service.icbill.ITradeoutService;
import com.dzf.zxkj.platform.service.icreport.IQueryLastNum;
import com.dzf.zxkj.platform.service.icset.IInventoryService;
import com.dzf.zxkj.platform.service.jzcl.IQmclService;
import com.dzf.zxkj.platform.service.pzgl.IVoucherService;
import com.dzf.zxkj.platform.service.qcset.IQcye;
import com.dzf.zxkj.platform.service.report.impl.YntBoPubUtil;
import com.dzf.zxkj.platform.service.sys.*;
import com.dzf.zxkj.platform.service.tax.ICorpTaxService;
import com.dzf.zxkj.platform.service.taxrpt.ITaxBalaceCcrService;
import com.dzf.zxkj.platform.service.zcgl.IZczjmxReport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@org.apache.dubbo.config.annotation.Service(version = "1.0.0", timeout = Integer.MAX_VALUE)
public class ZxkjPlatformServiceImpl implements IZxkjPlatformService {

    @Autowired
    ISaleoutService ic_saleoutserv;
    @Autowired
    private ICpaccountService gl_cpacckmserv;
    @Autowired
    private ICpaccountCodeRuleService gl_accountcoderule;

    @Autowired
    private IBDCurrencyService sys_currentserv;

    @Autowired
    private YntBoPubUtil yntBoPubUtil;

    @Autowired
    private IIncomeWarningService iw_serv;// 预警信息
    @Autowired
    private ICorpService corpService;
    @Autowired
    private IAccountService accountService;
    @Autowired
    private IQcye gl_qcyeserv;

    @Autowired
    private IAuxiliaryAccountService gl_fzhsserv;

    @Autowired
    private IUserService userService;
    @Autowired
    private IParameterSetService sys_parameteract;

    @Autowired
    private IPersonalSetService gl_gxhszserv;

    @Autowired
    private IVoucherService gl_tzpzserv;

    @Autowired
    private IQmclService qmclService;

    @Autowired
    private IBDCorpTaxService sys_corp_tax_serv;
    @Autowired
    private ITaxBalaceCcrService taxBalaceCcrService;
    @Autowired
    private IInventoryService iInventoryService;
    @Autowired
    private ICorpTaxService corpTaxService;

    @Autowired
    private IQueryLastNum queryLastNum;

    @Autowired
    private IXssrTemService xssrTemService;
    @Autowired
    private ISecurityService securityserv;

    @Autowired
    private ISalaryReportService gl_gzbserv;

    @Autowired
    private ITradeoutService ic_tradeoutserv;

    @Autowired
    private ITradeinService ic_tradeinserv;
    @Autowired
    private IPurchInService ic_purchinserv;

    @Autowired
    private IZczjmxReport am_rep_zczjmxserv;

    @Override
    public CorpVO queryCorpByPk(String pk_corp) {
        try {
            return corpService.queryByPk(pk_corp);
        } catch (DZFWarpException e) {
            log.error(String.format("调用queryCorpByPk异常,参数:{pk_corp=%s},异常信息:%s", pk_corp, e.getMessage()), e);
            return null;
        }
    }

    @Override
    public CorpVO[] queryCorpByPks(String[] pk_corps) {
        try {
            return corpService.queryByPks(pk_corps);
        } catch (DZFWarpException e) {
            log.error(String.format("调用queryCorpByPk异常,参数:{pk_corp=%s},异常信息:%s", pk_corps, e.getMessage()), e);
            return null;
        }
    }

    @Override
    public Integer getAccountSchema(String pk_corp) {
        try {
            return yntBoPubUtil.getAccountSchema(pk_corp);
        } catch (DZFWarpException e) {
            log.error(String.format("调用getAccountSchema异常,参数:{pk_corp=%s},异常信息:%s", pk_corp, e.getMessage()), e);
            return null;
        }
    }

    @Override
    public IncomeWarningVO[] queryIncomeWarningVOs(String pk_corp) {
        try {
            return iw_serv.query(pk_corp);
        } catch (DZFWarpException e) {
            log.error(String.format("调用queryIncomeWarningVOs异常,参数:{pk_corp=%s},异常信息:%s", pk_corp, e.getMessage()), e);
            return null;
        }
    }

    @Override
    public IncomeWarningVO[] queryFseInfo(IncomeWarningVO[] ivos, String pk_corp, String enddate) {
        IncomeWarningVO[] iList = null;
        try {
            iList =iw_serv.queryFseInfo(ivos, pk_corp, enddate);
        } catch (Exception e) {
            log.error(String.format("调用queryFseInfo异常,参数:{pk_corp=%s,enddate=%s},异常信息:%s", pk_corp, enddate, e.getMessage()), e);
        }
        return iList;
    }

    @Override
    public String queryAccountRule(String pk_corp) {
        try {
            return gl_cpacckmserv.queryAccountRule(pk_corp);
        } catch (DZFWarpException e) {
            log.error(String.format("调用queryAccountRuleVOs异常,参数:{pk_corp=%s},异常信息:%s", pk_corp, e.getMessage()), e);
            return null;
        }
    }

    @Override
    public String getCurrentCorpAccountSchema(String pk_corp) {
        try {
            return yntBoPubUtil.getCurrentCorpAccountSchema(pk_corp);
        } catch (DZFWarpException e) {
            log.error(String.format("调用getCurrentCorpAccountSchema异常,参数:{pk_corp=%s},异常信息:%s", pk_corp, e.getMessage()), e);
            return null;
        }
    }

    @Override
    public YntCpaccountVO[] queryByPk(String pk_corp) {
        try {
            return accountService.queryByPk(pk_corp);
        } catch (DZFWarpException e) {
            log.error(String.format("调用queryByPk异常,参数:{pk_corp=%s},异常信息:%s", pk_corp, e.getMessage()), e);
            return null;
        }
    }

    @Override
    public Map<String, YntCpaccountVO> queryMapByPk(String pk_corp) {
        try {
            return accountService.queryMapByPk(pk_corp);
        } catch (DZFWarpException e) {
            log.error(String.format("调用queryMapByPk异常,参数:{pk_corp=%s},异常信息:%s", pk_corp, e.getMessage()), e);
            return null;
        }
    }

    @Override
    public String getNewRuleCode(String oldCode, String oldrule, String newrule) {
        try {
            return gl_accountcoderule.getNewRuleCode(oldCode, oldrule, newrule);
        } catch (DZFWarpException e) {
            log.error(String.format("调用getNewRuleCode异常,参数:{oldCode=%s,oldrule=%s,newrule=%s },异常信息:%s", oldCode, oldrule, newrule, e.getMessage()), e);
            return null;
        }
    }

    @Override
    public SsphRes qcyeSsph(String pk_corp) {
        try {
            return gl_qcyeserv.ssph(pk_corp);
        } catch (DZFWarpException e) {
            log.error(String.format("调用qcyeSsph异常,参数:{pk_corp=%s},异常信息:%s", pk_corp, e.getMessage()), e);
            return null;
        }
    }

    @Override
    public String[] getNewCodes(String[] oldcode, String oldrule, String newrule) {
        try {
            return gl_accountcoderule.getNewCodes(oldcode, oldrule, newrule);
        } catch (DZFWarpException e) {
            log.error(String.format("调用getNewCodes异常,异常信息:%s", e.getMessage()), e);
            return null;
        }
    }

    @Override
    public Map<String, AuxiliaryAccountBVO> queryAuxiliaryAccountBVOMap(String pk_corp) {
        try {
            return gl_fzhsserv.queryMap(pk_corp);
        } catch (DZFWarpException e) {
            log.error(String.format("调用queryAuxiliaryAccountBVOMap异常,参数pk_corp:%s,异常信息:%s", pk_corp, e.getMessage()), e);
            return null;
        }
    }

    @Override
    public DZFDouble getTaxValue(CorpVO cpvo, String rptname, String period, int[][] zbs) {
        try {
            return taxBalaceCcrService.getTaxValue(cpvo, rptname, period, zbs);
        } catch (DZFWarpException e) {
            log.error(String.format("调用getTaxValue异常,参数rptname:%s,period:%s,异常信息:%s", rptname, period, e.getMessage()), e);
            return DZFDouble.ZERO_DBL;
        }
    }

    @Override
    public AuxiliaryAccountHVO queryHByCode(String pk_corp, String fzlb) {
        try {
            return gl_fzhsserv.queryHByCode(pk_corp, fzlb);
        } catch (DZFWarpException e) {
            log.error(String.format("调用queryHByCode异常,参数pk_corp:%s,fzlb:%s,异常信息:%s", pk_corp, fzlb, e.getMessage()), e);
            return null;
        }
    }

    @Override
    public AuxiliaryAccountHVO[] queryHByPkCorp(String pk_corp) {
        try {
            return gl_fzhsserv.queryH(pk_corp);
        } catch (DZFWarpException e) {
            log.error(String.format("调用queryHByPkCorp异常,参数pk_corp:%s,异常信息:%s", pk_corp, e.getMessage()), e);
            return null;
        }
    }

    @Override
    public AuxiliaryAccountBVO[] queryBByFzlb(String pk_corp, String fzlb) {
        try {
            return gl_fzhsserv.queryAllBByLb(pk_corp, fzlb);
        } catch (DZFWarpException e) {
            log.error(String.format("调用queryBByFzlb异常,参数pk_corp:%s, fzlb=%s,异常信息:%s", pk_corp, fzlb, e.getMessage()), e);
            return null;
        }
    }

    @Override
    public AuxiliaryAccountBVO[] queryAllB(String pk_corp) {
        try {
            return gl_fzhsserv.queryAllB(pk_corp);
        } catch (DZFWarpException e) {
            log.error(String.format("调用queryAllB异常,参数pk_corp:%s,异常信息:%s", pk_corp, e.getMessage()), e);
            return null;
        }
    }

    @Override
    public BdCurrencyVO queryCurrencyVOByPk(String pk_currency) {
        try {
            return sys_currentserv.queryCurrencyVOByPk(pk_currency);
        } catch (DZFWarpException e) {
            log.error(String.format("调用queryAllB异常,参数pk_currency:%s,异常信息:%s", pk_currency, e.getMessage()), e);
            return null;
        }
    }

    @Override
    public QcYeCurrency[] queryCurrencyByPkCorp(String pk_corp) {
        try {
            return gl_qcyeserv.queryCur(pk_corp);
        } catch (DZFWarpException e) {
            log.error(String.format("调用queryCurrencyByPkCorp异常,参数pk_corp:%s,异常信息:%s", pk_corp, e.getMessage()), e);
            return null;
        }
    }

    @Override
    public List<InventoryVO> queryInventoryVOs(String pk_corp) {
        try {
            return iInventoryService.query(pk_corp);
        } catch (DZFWarpException e) {
            log.error(String.format("调用queryInventoryVOs异常,参数pk_corp:%s,异常信息:%s", pk_corp, e.getMessage()), e);
            return null;
        }
    }

    @Override
    public YntParameterSet queryParamterbyCode(String pk_corp, String code) {
        try {
            return sys_parameteract.queryParamterbyCode(pk_corp, code);
        } catch (DZFWarpException e) {
            log.error(String.format("调用queryParamterbyCode异常,参数pk_corp:%s, code:%s,异常信息:%s", pk_corp, code, e.getMessage()), e);
            return null;
        }
    }

    @Override
    public GxhszVO queryGxhszVOByPkCorp(String pk_corp) {
        try {
            return gl_gxhszserv.query(pk_corp);
        } catch (DZFWarpException e) {
            log.error(String.format("调用queryGxhszVOByPkCorp异常,参数pk_corp:%s,异常信息:%s", pk_corp, e.getMessage()), e);
            return null;
        }
    }

    @Override
    public Map<String, String> getNewCodeMap(String[] oldcode, String oldrule, String newrule) {
        try {
            return gl_accountcoderule.getNewCodeMap(oldcode, oldrule, newrule);
        } catch (DZFWarpException e) {
            log.error(String.format("调用getNewCodeMap异常,参数oldrule:%s,newrule:%s,异常信息:%s", oldrule, newrule, e.getMessage()), e);
            return null;
        }
    }

    @Override
    public CorpTaxVo queryCorpTaxVO(String pk_corp) {
        try {
            return corpTaxService.queryCorpTaxVO(pk_corp);
        } catch (DZFWarpException e) {
            log.error(String.format("调用queryCorpTaxVO异常,参数pk_corp:%s,异常信息:%s", pk_corp, e.getMessage()), e);
            return null;
        }
    }

    @Override
    public YntCpaccountVO queryById(String id) {
        try {
            return gl_cpacckmserv.queryById(id);
        } catch (Exception e) {
            log.error(String.format("调用queryById异常,参数id:%s,异常信息:%s", id, e.getMessage()), e);
            return null;
        }
    }

    @Override
    public Map<String, IcbalanceVO> queryLastBanlanceVOs_byMap1(String currentenddate, String pk_corp, String pk_invtory, boolean isafternonn) {
        try {
            return queryLastNum.queryLastBanlanceVOs_byMap1(currentenddate, pk_corp, pk_invtory, isafternonn);
        } catch (DZFWarpException e) {
            log.error(String.format("调用queryLastBanlanceVOs_byMap1异常,参数currentenddate:%s,pk_corp:%s,异常信息:%s", currentenddate, pk_corp, e.getMessage()), e);
            return null;
        }
    }

    @Override
    public String queryParamterValueByCode(String pk_corp, String paramcode) {
        try {
            return sys_parameteract.queryParamterValueByCode(pk_corp, paramcode);
        } catch (DZFWarpException e) {
            log.error(String.format("调用queryParamterValueByCode异常,参数paramcode:%s,pk_corp:%s,异常信息:%s", paramcode, pk_corp, e.getMessage()), e);
            return null;
        }
    }
    //特殊处理
    @Override
    public ReturnData checkQjsy(TzpzHVO headVO) {
        try {
            gl_tzpzserv.checkQjsy(headVO);
        } catch (DZFWarpException e) {
            ReturnData returnData = new ReturnData(IVoucherConstants.EXE_RECONFM_CODE);
            returnData.setMessage(e.getMessage());
            log.error(String.format("调用checkQjsy异常,异常信息:%s", e.getMessage()), e);
            return returnData;
        }
        return ReturnData.ok();
    }

    @Override
    public TzpzHVO saveVoucher(CorpVO corpvo, TzpzHVO hvo) {
        try {
            return gl_tzpzserv.saveVoucher(corpvo, hvo);
        } catch (DZFWarpException e) {
            log.error(String.format("调用saveVoucher异常,异常信息:%s", e.getMessage()), e);
            return null;
        }
    }

    @Override
    public String getNewVoucherNo(String pk_corp, DZFDate doperatedate) {
        try {
            return yntBoPubUtil.getNewVoucherNo(pk_corp,doperatedate);
        } catch (DZFWarpException e) {
            log.error(String.format("调用getNewVoucherNo异常,异常信息:%s", e.getMessage()), e);
            return null;
        }
    }

    @Override
    public List<XssrVO> queryXssrVO(String pk_corp) {
        try {
            return xssrTemService.query(pk_corp);
        } catch (DZFWarpException e) {
            log.error(String.format("调用queryXssrVO异常,异常信息:%s", e.getMessage()), e);
            return null;
        }
    }

    @Override
    public UserVO queryUserById(String userId) {
        try {
            return userService.queryUserById(userId);
        } catch (DZFWarpException e) {
            log.error(String.format("调用queryUserById异常,异常信息:%s", e.getMessage()), e);
            return null;
        }
    }

    @Override
    public QueryPageVO processQueryVoucherPaged(VoucherParamVO paramvo) {
        try {
            return gl_tzpzserv.processQueryVoucherPaged(paramvo);
        } catch (DZFWarpException e) {
            log.error(String.format("调用processQueryVoucherPaged异常,异常信息:%s", e.getMessage()), e);
            return null;
        }
    }

    @Override
    public DZFDouble getQuarterlySdsShui(String pk_corp, String period) {
        try {
            return qmclService.getQuarterlySdsShui(pk_corp, period);
        } catch (DZFWarpException e) {
            log.error(String.format("调用getQuarterlySdsShui异常,异常信息:%s", e.getMessage()), e);
            return null;
        }
    }

    @Override
    public List<CorpTaxVo> queryTaxVoByParam(QueryParamVO paramvo, UserVO uservo) {
        try {
            return sys_corp_tax_serv.queryTaxVoByParam(paramvo, uservo);
        } catch (DZFWarpException e) {
            log.error(String.format("调用queryTaxVoByParam异常,异常信息:%s", e.getMessage()), e);
            return null;
        }
    }
    @Override
    public void checkSecurityData(SuperVO[] vos,String[] corps, String cuserid, boolean isCheckData){
        securityserv.checkSecurityData(vos,corps,cuserid,isCheckData);
    }

    @Override
    public SalaryReportVO[] queryGzb(String pk_corp, String beginPeriod, String endPeriod, String billtype) {
        try {
            return gl_gzbserv.query(pk_corp, beginPeriod,endPeriod, billtype);
        } catch (DZFWarpException e) {
            log.error(String.format("调用queryGzb异常,异常信息:%s", e.getMessage()), e);
            return null;
        }
    }

    @Override
    public List<IctradeinVO> queryTradeIn(QueryParamVO paramvo) {
        try {
            return ic_tradeinserv.query(paramvo);
        } catch (DZFWarpException e) {
            log.error(String.format("调用queryTradeIn异常,异常信息:%s", e.getMessage()), e);
            return null;
        }
    }

    @Override
    public List<IntradeoutVO> queryTradeOut(QueryParamVO paramvo) {
        try {
            return ic_tradeoutserv.query(paramvo);
        } catch (DZFWarpException e) {
            log.error(String.format("调用queryTradeOut异常,异常信息:%s", e.getMessage()), e);
            return null;
        }
    }

    @Override
    public IntradeHVO queryIntradeHVOByID(String pk_ictrade_h, String pk_corp) {
        try {
            return ic_saleoutserv.queryIntradeHVOByID(pk_ictrade_h, pk_corp);
        } catch (DZFWarpException e) {
            log.error(String.format("调用queryIntradeHVOByID异常,异常信息:%s", e.getMessage()), e);
            return null;
        }
    }

    @Override
    public IntradeHVO queryIntradeHVOByIDIn(String pk_ictrade_h, String pk_corp) {
        try {
            return ic_purchinserv.queryIntradeHVOByID(pk_ictrade_h, pk_corp);
        } catch (DZFWarpException e) {
            log.error(String.format("调用queryIntradeHVOByIDIn异常,异常信息:%s", e.getMessage()), e);
            return null;
        }
    }

    @Override
    public AssetDepreciaTionVO[] getZczjMxVOs(QueryParamVO queryParamvo) {
        try {
            return am_rep_zczjmxserv.getZczjMxVOs(queryParamvo, null);
        } catch (DZFWarpException e) {
            log.error(String.format("调用queryIntradeHVOByIDIn异常,异常信息:%s", e.getMessage()), e);
            return null;
        }
    }

}
