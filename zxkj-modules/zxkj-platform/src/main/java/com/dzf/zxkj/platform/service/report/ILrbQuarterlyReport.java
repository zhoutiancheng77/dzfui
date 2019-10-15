package com.dzf.zxkj.platform.service.report;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.model.report.LrbquarterlyVO;
import com.dzf.zxkj.base.query.QueryParamVO;

import java.util.Map;

public interface ILrbQuarterlyReport {

	/**
	 * 利润表取数
	 * @param period
	 * @param pk_corp
	 * @return
	 * @throws BusinessException
	 */
	public LrbquarterlyVO[] getLRBquarterlyVOs(QueryParamVO paramVO) throws DZFWarpException;
	
	
	/**
	 * 从科目明细账取数据
	 * @param paramVO
	 * @return
	 * @throws DZFWarpException
	 */
	public Map<String, LrbquarterlyVO[]> getLRBquarterlyVOs(QueryParamVO vo, Object[] objs) throws  DZFWarpException ;


	/**
	 * 每个月的利润表取数
	 * @param paramVO
	 * @return
	 * @throws BusinessException
	 */
	public Map<String, DZFDouble> getYearLRBquarterlyVOs(String year, String pk_corp) throws  DZFWarpException ;
	
}
