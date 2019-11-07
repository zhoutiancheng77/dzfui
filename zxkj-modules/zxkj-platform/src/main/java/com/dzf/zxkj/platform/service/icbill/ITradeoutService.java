package com.dzf.zxkj.platform.service.icbill;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.platform.model.icset.IntradeoutVO;

import java.util.List;

public interface ITradeoutService {

	/**
	 * 查询所有数据
	 * @return
	 */
	List<IntradeoutVO> query(QueryParamVO paramvo)  throws DZFWarpException;
	
	/**
	 * 根据主键查询
	 * @param id
	 * @return
	 */
	IntradeoutVO queryById(String id)  throws DZFWarpException;

}
