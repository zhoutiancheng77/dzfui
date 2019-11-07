package com.dzf.zxkj.platform.service.icreport;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.platform.model.report.IcDetailVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;

import java.util.Map;
/**
 * 库存明细账
 * @author wangzhn
 *
 */
public interface IICMxz {
	/**
	 * 查询库存详细信息
	 * @param paramvo
	 * @param corpvo
	 * @return
	 * @throws DZFWarpException
	 */
	Map<String, IcDetailVO> queryDetail(QueryParamVO paramvo, CorpVO corpvo) throws DZFWarpException;
}
