package com.dzf.zxkj.platform.services.bdset.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.common.exception.DZFWarpException;
import com.dzf.zxkj.custom.type.DZFDate;
import com.dzf.zxkj.custom.type.DZFDouble;
import com.dzf.zxkj.platform.model.bdset.ExrateVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.services.bdset.IYntCpaccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @Auther: dandelion
 * @Date: 2019-09-06
 * @Description:
 */
@Service
public class YntCpaccountServiceImpl implements IYntCpaccountService {

    @Autowired
    private SingleObjectBO singleObjectBO;

    @Override
    public YntCpaccountVO[] get(String pk_corp, int kind) throws DZFWarpException {
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);
        sp.addParam(kind);
        YntCpaccountVO[] cpvos = (YntCpaccountVO[]) singleObjectBO.queryByCondition(YntCpaccountVO.class, " pk_corp = ? and accountkind = ? and nvl(dr,0) = 0 ", sp);
        return sort(cpvos);
    }

    private YntCpaccountVO[] sort(YntCpaccountVO[] yntCpaccountVOS) {
        if (yntCpaccountVOS == null) {
            return new YntCpaccountVO[0];
        }
        Arrays.sort(yntCpaccountVOS, new Comparator<YntCpaccountVO>() {
            @Override
            public int compare(YntCpaccountVO arg0, YntCpaccountVO arg1) {
                return arg0.getAccountcode().compareTo(arg1.getAccountcode());
            }
        });
        return yntCpaccountVOS;
    }

    @Override
    public YntCpaccountVO[] get(String pk_corp) throws DZFWarpException {
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);
        YntCpaccountVO[] cpvos = (YntCpaccountVO[]) singleObjectBO.queryByCondition(YntCpaccountVO.class, " pk_corp = ? and nvl(dr,0) = 0 ", sp);
        return sort(cpvos);
    }

    @Override
    public Map<String, YntCpaccountVO> getMap(String pk_corp) throws DZFWarpException {
        Map<String, YntCpaccountVO> m = new HashMap<String, YntCpaccountVO>();
        YntCpaccountVO[] yvo = get(pk_corp);
        int len = yvo == null ? 0 : yvo.length;
        for (int i = 0; i < len; i++) {
            m.put(yvo[i].getPrimaryKey(), yvo[i]);
        }
        return m;
    }

    @Override
    public String getNewVoucherNo(String pk_corp, DZFDate doperatedate) throws DZFWarpException {
        return null;
    }

    @Override
    public String getCorpAccountPkByTradeAccountPk(String pk_trade_account, String pk_corp) throws DZFWarpException {
        return null;
    }

    @Override
    public String getCorpAccountPkByTradeAccountPkWithMsg(String pk_trade_account, String pk_corp, String msg) throws DZFWarpException {
        return null;
    }

    @Override
    public String getCorpAccountByTradeAccountPk(String pk_trade_account, String pk_corp) throws DZFWarpException {
        return null;
    }

    @Override
    public String getCNYPk() throws DZFWarpException {
        return null;
    }

    @Override
    public DZFDouble getThisPeriodOccurMny(String pk_corp, String period, String pk_accsubj) throws DZFWarpException {
        return null;
    }

    @Override
    public boolean is2007AccountSchema(String pk_corp) throws DZFWarpException {
        return false;
    }

    @Override
    public String getCurrentCorpAccountSchema(String pk_corp) throws DZFWarpException {
        return null;
    }

    @Override
    public DZFDouble getQmMny(String pk_corp, String period, String pk_accsubj) throws DZFWarpException {
        return null;
    }

    @Override
    public String getSqlStrByList(List sqhList, int splitNum, String columnName) {
        return null;
    }

    @Override
    public String getSqlStrByArrays(String[] sqhArrays, int splitNum, String columnName) {
        return null;
    }

    @Override
    public Map<String, YntCpaccountVO> querykm(String pk_corp) throws DZFWarpException {
        return null;
    }

    @Override
    public ExrateVO[] getRateBypk(String pk_currency, String pk_corp) throws DZFWarpException {
        return new ExrateVO[0];
    }

    @Override
    public String createRulecodebyCorp(String bzaccountcode, String pk_corp) throws DZFWarpException {
        return null;
    }

    @Override
    public String getFZHsCode(String pk_corp, String pk_auacount_h) throws DZFWarpException {
        return null;
    }

    @Override
    public String getInventoryCode(String pk_corp) throws DZFWarpException {
        return null;
    }

    @Override
    public String getMeasureCode(String pk_corp) throws DZFWarpException {
        return null;
    }

    @Override
    public String getInvclCode(String pk_corp) throws DZFWarpException {
        return null;
    }

    @Override
    public String getYhzhCode(String pk_corp) throws DZFWarpException {
        return null;
    }

    @Override
    public Integer getAccountSchema(String pk_corp) throws DZFWarpException {
        return null;
    }
}
