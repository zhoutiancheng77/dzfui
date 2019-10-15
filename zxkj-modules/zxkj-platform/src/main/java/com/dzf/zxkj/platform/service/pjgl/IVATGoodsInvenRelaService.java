package com.dzf.zxkj.platform.service.pjgl;

import com.dzf.zxkj.base.exception.DZFWarpException;

public interface IVATGoodsInvenRelaService {

	/**
	 * 查询存货的对照表
	 * @param pks
	 * @param pk_corp
	 * @throws DZFWarpException
	 */
	public void deleteCasCadeGoods(String[] pks, String pk_corp) throws DZFWarpException;
}
