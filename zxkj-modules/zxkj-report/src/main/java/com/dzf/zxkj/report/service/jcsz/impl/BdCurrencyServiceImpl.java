package com.dzf.zxkj.report.service.jcsz.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.common.utils.IDefaultValue;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.BdCurrencyVO;
import com.dzf.zxkj.report.service.jcsz.IBDCurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 币种
 */
@Service
public class BdCurrencyServiceImpl implements IBDCurrencyService {

    @Autowired
    private SingleObjectBO singleObjectBO;

    public BdCurrencyVO[] queryCurrency() throws DZFWarpException {
        SQLParameter sp = new SQLParameter();
        sp.addParam(IDefaultValue.DefaultGroup);
        String condition = " pk_corp = ? and nvl(dr,0) = 0 order by currencycode  ";
        BdCurrencyVO[] vos = (BdCurrencyVO[]) singleObjectBO.queryByCondition(BdCurrencyVO.class, condition, sp);
        if (vos == null || vos.length == 0)
            return null;
        return vos;
    }

    @Override
    public BdCurrencyVO queryCurrencyVOByPk(String pk_currency) throws DZFWarpException {
        return StringUtil.isEmpty(pk_currency) ? null : (BdCurrencyVO) singleObjectBO.queryByPrimaryKey(BdCurrencyVO.class, pk_currency);
    }

    @Override
    public BdCurrencyVO[] queryCurrencyByCorp(String pk_corp) throws DZFWarpException {
        if (StringUtil.isEmpty(pk_corp)) {
            return null;
        }
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);
        return (BdCurrencyVO[]) singleObjectBO.queryByCondition(BdCurrencyVO.class, "nvl(dr,0)=0 and pk_corp = ?", sp);
    }

}
