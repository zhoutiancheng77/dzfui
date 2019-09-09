package com.dzf.zxkj.platform.services.bdset;

import com.dzf.zxkj.common.exception.DZFWarpException;
import com.dzf.zxkj.custom.type.DZFDate;
import com.dzf.zxkj.custom.type.DZFDouble;
import com.dzf.zxkj.platform.model.bdset.ExrateVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;

import java.util.List;
import java.util.Map;

/**
 * @Auther: dandelion
 * @Date: 2019-09-06
 * @Description:
 */
public interface IYntCpaccountService {
    YntCpaccountVO[] get(String pk_corp, int kind) throws DZFWarpException;

    YntCpaccountVO[] get(String pk_corp) throws DZFWarpException;

    Map<String, YntCpaccountVO> getMap(String pk_corp) throws DZFWarpException;

    String getNewVoucherNo(String pk_corp, DZFDate doperatedate) throws DZFWarpException;

    String getCorpAccountPkByTradeAccountPk(String pk_trade_account, String pk_corp) throws DZFWarpException;

    String getCorpAccountPkByTradeAccountPkWithMsg(String pk_trade_account, String pk_corp, String msg) throws DZFWarpException;

    String getCorpAccountByTradeAccountPk(String pk_trade_account, String pk_corp) throws DZFWarpException;

    String getCNYPk() throws DZFWarpException;

    DZFDouble getThisPeriodOccurMny(String pk_corp, String period, String pk_accsubj) throws DZFWarpException;

    boolean is2007AccountSchema(String pk_corp) throws DZFWarpException;

    String getCurrentCorpAccountSchema(String pk_corp) throws DZFWarpException;

    DZFDouble getQmMny(String pk_corp, String period, String pk_accsubj) throws DZFWarpException;

    String getSqlStrByList(List sqhList, int splitNum, String columnName);

    String getSqlStrByArrays(String[] sqhArrays, int splitNum, String columnName);

    Map<String, YntCpaccountVO> querykm(String pk_corp) throws DZFWarpException;

    ExrateVO[] getRateBypk(String pk_currency, String pk_corp) throws DZFWarpException;

    String createRulecodebyCorp(String bzaccountcode, String pk_corp) throws DZFWarpException;

    String getFZHsCode(String pk_corp, String pk_auacount_h) throws DZFWarpException;

    String getInventoryCode(String pk_corp) throws DZFWarpException;

    String getMeasureCode(String pk_corp) throws DZFWarpException;

    String getInvclCode(String pk_corp) throws DZFWarpException;

    String getYhzhCode(String pk_corp) throws DZFWarpException;

    Integer getAccountSchema(String pk_corp) throws DZFWarpException;
}
