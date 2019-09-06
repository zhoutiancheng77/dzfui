package com.dzf.zxkj.platform.services.bdset;

import com.dzf.zxkj.common.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.bdset.BdCurrencyVO;

/**
 * @Auther: dandelion
 * @Date: 2019-09-06
 * @Description:
 */
public interface IBdCurrencyService {
    //查询币种信息
    BdCurrencyVO[] queryCurrency() throws DZFWarpException;

    //保存币种信息
    void save(BdCurrencyVO vo) throws DZFWarpException;

    //删除币种信息
    void delete(BdCurrencyVO vo) throws DZFWarpException;

    BdCurrencyVO queryCurrencyVOByPk(String pk_currency) throws DZFWarpException;
}
