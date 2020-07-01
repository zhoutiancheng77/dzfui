package com.dzf.zxkj.report.service.jcsz;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.bdset.BdCurrencyVO;

/**
 * 币种
 *
 */
public interface IBDCurrencyService {
	
	//查询币种信息
	 BdCurrencyVO[] queryCurrency() throws DZFWarpException;
	 BdCurrencyVO queryCurrencyVOByPk(String pk_currency) throws DZFWarpException;
	// 根据公司id获取币种信息
	 BdCurrencyVO[] queryCurrencyByCorp(String pk_corp) throws DZFWarpException;
	
	
	
}
