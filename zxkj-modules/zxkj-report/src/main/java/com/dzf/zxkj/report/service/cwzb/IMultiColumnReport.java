package com.dzf.zxkj.report.service.cwzb;


import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.query.KmReoprtQueryParamVO;

/**
 * 多栏账的接口
 * @author zhangj
 *
 */
public interface IMultiColumnReport {

	
	/**
	 * 科目明细账
	 * @param period
	 * @param pk_corp
	 * @return
	 * @throws BusinessException
	 */
	public  Object[] getMulColumns(KmReoprtQueryParamVO vo) throws DZFWarpException;
	
}
