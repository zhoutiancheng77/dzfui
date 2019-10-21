package com.dzf.zxkj.platform.service.glic;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.platform.model.glic.IcDetailVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;

import java.util.List;
import java.util.Map;

/**
 * 出入库明细
 * 
 * @author zhangj
 *
 */
public interface ICrkMxService {

	/**
	 * 查询出入库信息
	 * @param paramvo
	 * @param corpvo
	 * @return
	 * @throws DZFWarpException
	 */
	public Map<String, List<IcDetailVO>> queryMx(QueryParamVO paramvo, CorpVO corpvo) throws DZFWarpException;

	/**
	 * 根据出入库单号，查询出入库数据
	 * @param crkcode
	 * @return
	 * @throws DZFWarpException
	 */
	public List<IcDetailVO> queryCrkmx(String crkcode,String pk_corp,String rq) throws DZFWarpException;
	
	
	/**
	 * 批量查询出入库信息
	 * @param ‘crkcode’
	 * @param pk_corp
	 * @return
	 * @throws DZFWarpException
	 */
	public Map<String, List<IcDetailVO>> queryCrkmxs(String[] crkcodes,String[] tzpzids ,String pk_corp,String rq) throws DZFWarpException;
}
