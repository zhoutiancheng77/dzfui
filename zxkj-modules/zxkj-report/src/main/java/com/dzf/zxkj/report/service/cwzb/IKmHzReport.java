package com.dzf.zxkj.report.service.cwzb;


import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.query.QueryParamVO;

import java.util.List;

/**
 * 科目汇总表
 * @author zhangj
 *
 */
public interface IKmHzReport {
	
	/**
	 * 科目汇总
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	public List<Object> getKMHzVOs(QueryParamVO vo) throws DZFWarpException;

}
