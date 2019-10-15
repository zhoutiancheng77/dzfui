package com.dzf.zxkj.report.service.cwbb;


import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.query.QueryParamVO;
import com.dzf.zxkj.platform.model.report.YyFpVO;

import java.util.List;

public interface IYyFpService {

	public List<YyFpVO> queryList(QueryParamVO paramvo) throws DZFWarpException;

}
