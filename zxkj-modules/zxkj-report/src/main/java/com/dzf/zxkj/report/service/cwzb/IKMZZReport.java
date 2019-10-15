package com.dzf.zxkj.report.service.cwzb;


import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.platform.model.jzcl.KmZzVO;

/**
 * 科目总账处理
 * @author zhangj
 *
 */
public interface IKMZZReport {

	/**
	 * 科目总账
	 * @param period
	 * @param pk_corp
	 * @return
	 * @throws BusinessException
	 */
	public KmZzVO[] getKMZZVOs(QueryParamVO vo, Object[] kmmx_objs) throws DZFWarpException;
}
