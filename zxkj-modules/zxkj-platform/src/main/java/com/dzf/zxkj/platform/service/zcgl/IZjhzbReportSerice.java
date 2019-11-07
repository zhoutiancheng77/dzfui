package com.dzf.zxkj.platform.service.zcgl;


import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.platform.model.zcgl.ZjhzbReportVO;

import java.util.List;

/**
 * 折旧汇总表
 * @author 
 *
 */
public interface IZjhzbReportSerice {

	/**
	 * @param vo
	 * 折旧汇总表 查询
	 * @return ZjhzbReportVO[]
	 * @throws BusinessException
	 */
	public List<ZjhzbReportVO> queryZjhzb(QueryParamVO vo) throws DZFWarpException;
}
