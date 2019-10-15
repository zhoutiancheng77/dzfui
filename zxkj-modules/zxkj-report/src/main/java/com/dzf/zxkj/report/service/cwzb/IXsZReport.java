package com.dzf.zxkj.report.service.cwzb;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.query.KmReoprtQueryParamVO;
import com.dzf.zxkj.platform.model.report.XsZVO;

public interface IXsZReport {
	/**
	 * 序时账
	 * @param period
	 * @param pk_corp
	 * @return
	 * @throws BusinessException
	 */
	public XsZVO[] getXSZVOs(String pk_corp, String kms, String kmsx, String zdr, String shr, KmReoprtQueryParamVO queryvo) throws DZFWarpException;
}
