package com.dzf.zxkj.platform.service.zncs;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.pjgl.VatInvoiceSetVO;

public interface IVatInvoiceService {

	/**
	 * 根据公司、类型查询合并规则
	 * @param pk_corp
	 * @param type
	 * @return
	 * @throws DZFWarpException
	 */
	public VatInvoiceSetVO[] queryByType(String pk_corp, String type) throws DZFWarpException;
	
	/**
	 * 更新规则
	 * @param pk_corp
	 * @param vo
	 * @param fields
	 * @throws DZFWarpException
	 */
	public void updateVO(String pk_corp, VatInvoiceSetVO vo, String[] fields) throws DZFWarpException;
}
