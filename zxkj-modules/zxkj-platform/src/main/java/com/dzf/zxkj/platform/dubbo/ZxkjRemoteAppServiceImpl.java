package com.dzf.zxkj.platform.dubbo;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountBVO;
import com.dzf.zxkj.platform.model.bdset.BdCurrencyVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.bdset.IAuxiliaryAccountService;
import com.dzf.zxkj.platform.service.bdset.ICpaccountCodeRuleService;
import com.dzf.zxkj.platform.service.bdset.ICpaccountService;
import com.dzf.zxkj.platform.service.report.impl.YntBoPubUtil;
import com.dzf.zxkj.platform.service.sys.IAccountService;
import com.dzf.zxkj.platform.service.sys.IBDCurrencyService;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import com.dzf.zxkj.platform.service.sys.IParameterSetService;
import com.dzf.zxkj.report.service.IZxkjRemoteAppService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;


@Slf4j
@Service
@org.apache.dubbo.config.annotation.Service(version = "1.0.0", timeout = Integer.MAX_VALUE)
public class ZxkjRemoteAppServiceImpl implements IZxkjRemoteAppService {
    @Autowired
    private ICorpService corpService;
    @Autowired
    private YntBoPubUtil yntBoPubUtil;
    @Autowired
    private ICpaccountService iCpaccountService;
    @Autowired
    private ICpaccountCodeRuleService iCpaccountCodeRuleService;
    @Autowired
    private IAuxiliaryAccountService gl_fzhsserv;
    @Autowired
    private IAccountService accountService;
    @Autowired
    private IParameterSetService sys_parameteract;
    @Override
    public CorpVO queryByPk(String pk_corp) {
        try {
            return corpService.queryByPk(pk_corp);
        } catch (DZFWarpException e) {
            log.error(String.format("调用queryByPk异常,异常信息:%s", e.getMessage()), e);
            return null;
        }
    }
    @Override
    public Integer getAccountSchema(String pk_corp){
        try {
            return yntBoPubUtil.getAccountSchema(pk_corp);
        } catch (DZFWarpException e) {
            log.error(String.format("调用getAccountSchema异常,异常信息:%s", e.getMessage()), e);
            return null;
        }
    }
    @Override
    public String queryAccountRule(String pk_corp){
        try {
            return iCpaccountService.queryAccountRule(pk_corp);
        } catch (DZFWarpException e) {
            log.error(String.format("调用queryAccountRule异常,异常信息:%s", e.getMessage()), e);
            return null;
        }
    }
    @Override
    public String getNewRuleCode(String oldCode, String oldrule, String newrule){
        try {
            return iCpaccountCodeRuleService.getNewRuleCode(oldCode,oldrule,newrule);
        } catch (DZFWarpException e) {
            log.error(String.format("调用getNewRuleCode异常,异常信息:%s", e.getMessage()), e);
            return null;
        }
    }
    @Override
    public AuxiliaryAccountBVO[] queryAllBByLb(String pk_corp, String fzlb){
        try {
            return gl_fzhsserv.queryAllBByLb(pk_corp,fzlb);
        } catch (DZFWarpException e) {
            log.error(String.format("调用queryAllBByLb异常,异常信息:%s", e.getMessage()), e);
            return null;
        }
    }
    @Override
    public Map<String, YntCpaccountVO> queryMapByPk(String pk_corp){
        try {
            return accountService.queryMapByPk(pk_corp);
        } catch (DZFWarpException e) {
            log.error(String.format("调用queryMapByPk异常,异常信息:%s", e.getMessage()), e);
            return null;
        }
    }
    @Override
    public YntCpaccountVO[] queryYCVoByPk(String pk_corp){
        try {
            return accountService.queryByPk(pk_corp);
        } catch (DZFWarpException e) {
            log.error(String.format("调用queryYCVoByPk异常,异常信息:%s", e.getMessage()), e);
            return null;
        }
    }
    @Override
    public String queryParamterValueByCode(String pk_corp, String paramcode){
        try {
            return sys_parameteract.queryParamterValueByCode(pk_corp,paramcode);
        } catch (DZFWarpException e) {
            log.error(String.format("调用queryParamterValueByCode异常,异常信息:%s", e.getMessage()), e);
            return null;
        }
    }
}
