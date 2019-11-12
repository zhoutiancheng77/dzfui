package com.dzf.zxkj.report.service.cwzb;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.query.QueryCondictionVO;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.platform.model.report.NumMnyDetailVO;
import com.dzf.zxkj.platform.model.report.NumMnyGlVO;

import java.util.List;

/**
 * 数量金额报表接口
 *
 */
public interface INummnyReport {
	/**
	 * 数量金额明细账
	 * @param pk_corp2 
	 */
	public List<NumMnyDetailVO> getNumMnyDetailVO(String startDate, String enddate,
												  String pk_inventory, QueryParamVO paramvo, String pk_corp, String user_id, String pk_bz, String xsfzhs, DZFDate begdate) throws DZFWarpException;
	/**
	 * 数量金额总账
	 */
	public List<NumMnyGlVO> getNumMnyGlVO(QueryCondictionVO paramVo) throws  DZFWarpException ;

}