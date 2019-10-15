package com.dzf.zxkj.report.service.cwbb;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.platform.model.report.YwHdVO;

/**
 * 业务活动取数接口
 * @author zhangj
 *
 */
public interface IYwHdReport {
	
	public YwHdVO[] queryYwHdValues(QueryParamVO paramvo) throws DZFWarpException;

}
