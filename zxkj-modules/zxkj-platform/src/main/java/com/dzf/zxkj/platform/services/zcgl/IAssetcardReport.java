package com.dzf.zxkj.platform.services.zcgl;

import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.common.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.platform.model.zcgl.ZcMxZVO;
import com.dzf.zxkj.platform.model.zcgl.ZcZzVO;
import com.dzf.zxkj.platform.vo.sys.QueryParamVO;

public interface IAssetcardReport {
	/**
	 * 查询资产总账
	 * @param pk_corp
	 * @param beginDate
	 * @param endDate
	 * @param where
	 * @return
	 * @throws BusinessException
	 */
	public ZcZzVO[] queryAssetcardTotal(String pk_corp, DZFDate beginDate, DZFDate endDate, String where, SQLParameter sp, String zclb, String zcsx, String zcname) throws DZFWarpException;
	/**
	 * 查询资产明细账
	 * @param pk_corp
	 * @param beginDate
	 * @param endDate
	 * @param where
	 * @return
	 * @throws BusinessException
	 */
	public ZcMxZVO[] queryAssetcardDetail(QueryParamVO queryParamvo) throws DZFWarpException;
}
