package com.dzf.zxkj.platform.service.zcgl;

import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.lang.DZFDate;

/**
 * 资产管理公共类
 * @author gjw
 *
 */
public interface IAssetcardHelper {

	/**
	 * 校验期间是否已经结账，如果已经结账，则当月不允许进行任何操作
	 * 
	 * @param pk_corp
	 * @param date
	 * @throws BusinessException
	 */
	public void checkPeriodIsSettle(String pk_corp, DZFDate date)
			throws BusinessException;

	/**
	 * 是否启用固定资产模块
	 * 
	 * @param pk_corp
	 * @return
	 * @throws BusinessException
	 */
	public boolean enableAssetModule(String pk_corp) throws BusinessException;

	/**
	 * 当公司没有启用固定资产模块时，处理按钮情况
	 * 
	 * @param pk_corp
	 * @param bos
	 * @throws BusinessException
	 */
	// public void processBtnWhenDisableAssetModule(ToftPanel toftPanel, String
	// pk_corp) throws BusinessException;
}
