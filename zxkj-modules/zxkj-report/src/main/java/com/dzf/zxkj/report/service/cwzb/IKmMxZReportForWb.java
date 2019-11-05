package com.dzf.zxkj.report.service.cwzb;


import com.dzf.zxkj.common.exception.BusinessException;
import com.dzf.zxkj.common.query.QueryParamVO;

/**
 * 外币的科目明细账
 * @author zhangj
 *
 */
public interface IKmMxZReportForWb {

	
	public Object[] getKmMxZVOs2(QueryParamVO vo) throws BusinessException;
	
}
