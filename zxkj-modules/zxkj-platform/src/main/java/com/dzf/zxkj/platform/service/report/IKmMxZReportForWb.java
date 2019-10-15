package com.dzf.zxkj.platform.service.report;

import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.platform.vo.sys.QueryParamVO;

/**
 * 外币的科目明细账
 * @author zhangj
 *
 */
public interface IKmMxZReportForWb {

	
	public Object[] getKmMxZVOs2(QueryParamVO vo) throws BusinessException;
	
}
