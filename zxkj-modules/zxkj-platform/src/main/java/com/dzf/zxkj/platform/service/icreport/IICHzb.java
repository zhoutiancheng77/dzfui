package com.dzf.zxkj.platform.service.icreport;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.platform.model.report.IcDetailVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;

import java.util.Map;

/**
 * 库存汇总表
 * @author zhw
 *
 */
public interface IICHzb {
	/**
	 * 查询库存收发存
	 * @param paramvo
	 * @param corpvo
	 * @return
	 * @throws DZFWarpException
	 */
	public Map<String, IcDetailVO> queryDetail(QueryParamVO paramvo, CorpVO corpvo) throws DZFWarpException;
}
