package com.dzf.zxkj.platform.service.icbill;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.platform.model.icset.IctradeinVO;

import java.util.List;

public interface ITradeinService {

	
	/**
	 * 查询所有数据
	 * @return
	 */
	List<IctradeinVO> query(QueryParamVO paramvo ) throws DZFWarpException;
	
	/**
	 * 根据主键查询
	 * @param id
	 * @return
	 */
	IctradeinVO queryById(String id) throws  DZFWarpException ;
	
}
