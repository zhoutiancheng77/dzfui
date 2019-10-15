package com.dzf.zxkj.report.service.cwbb;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.query.QueryParamVO;
import com.dzf.zxkj.platform.model.report.XjllquarterlyVo;

import java.util.List;

/**
 * 现金流量季报查询接口
 * @author zhangj
 *
 */
public interface IXjllbQuarterlyReport {
	
	public List<XjllquarterlyVo> getXjllQuartervos(QueryParamVO paramvo, String jd) throws DZFWarpException;

}
