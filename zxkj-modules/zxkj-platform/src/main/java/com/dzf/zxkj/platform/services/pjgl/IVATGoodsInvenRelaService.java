package com.dzf.zxkj.platform.services.pjgl;

import com.dzf.zxkj.common.exception.DZFWarpException;

public interface IVATGoodsInvenRelaService {

	/**
	 * 查询存货的对照表
	 * @param pks
	 * @param pk_corp
	 * @throws DZFWarpException
	 */
	public void deleteCasCadeGoods(String[] pks, String pk_corp) throws DZFWarpException;
}
