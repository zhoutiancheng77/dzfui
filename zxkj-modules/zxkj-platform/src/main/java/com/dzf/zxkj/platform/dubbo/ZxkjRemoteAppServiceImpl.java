package com.dzf.zxkj.platform.dubbo;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.bdset.BdCurrencyVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.bdset.ICpaccountCodeRuleService;
import com.dzf.zxkj.platform.service.bdset.ICpaccountService;
import com.dzf.zxkj.platform.service.report.impl.YntBoPubUtil;
import com.dzf.zxkj.platform.service.sys.IBDCurrencyService;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import com.dzf.zxkj.report.service.IZxkjRemoteAppService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


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
            log.error(String.format("调用getAccountSchema异常,参数pk_currency:%s,异常信息:%s", e.getMessage()), e);
            return null;
        }
    }
    @Override
    public String queryAccountRule(String pk_corp){
        try {
            return iCpaccountService.queryAccountRule(pk_corp);
        } catch (DZFWarpException e) {
            log.error(String.format("调用queryAccountRule异常,参数pk_currency:%s,异常信息:%s", e.getMessage()), e);
            return null;
        }
    }
    @Override
    public String getNewRuleCode(String oldCode, String oldrule, String newrule){
        try {
            return iCpaccountCodeRuleService.getNewRuleCode(oldCode,oldrule,newrule);
        } catch (DZFWarpException e) {
            log.error(String.format("调用getNewRuleCode异常,参数pk_currency:%s,异常信息:%s", e.getMessage()), e);
            return null;
        }
    }
}
