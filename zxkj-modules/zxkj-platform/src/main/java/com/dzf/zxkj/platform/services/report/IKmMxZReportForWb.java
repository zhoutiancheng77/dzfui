package com.dzf.zxkj.platform.services.report;

import com.dzf.zxkj.common.exception.BusinessException;
import com.dzf.zxkj.platform.vo.sys.QueryParamVO;

/**
 * 外币的科目明细账
 * @author zhangj
 *
 */
public interface IKmMxZReportForWb {

	
	public Object[] getKmMxZVOs2(QueryParamVO vo) throws BusinessException;
	
}
