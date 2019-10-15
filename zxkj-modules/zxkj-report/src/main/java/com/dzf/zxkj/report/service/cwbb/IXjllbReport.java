package com.dzf.zxkj.report.service.cwbb;


import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.query.QueryParamVO;
import com.dzf.zxkj.platform.model.qcset.YntXjllqcyePageVO;
import com.dzf.zxkj.platform.model.report.XjllMxvo;
import com.dzf.zxkj.platform.model.report.XjllbVO;

import java.util.List;
import java.util.Map;

public interface IXjllbReport {
	/**
	 * 现金流量表
	 * @param vo
	 * @return
	 * @throws Exception
	 */
	public XjllbVO[] query(QueryParamVO vo) throws DZFWarpException;
	
	
	/**
	 * 截止到当前期间的每个月的现金流量信息
	 * @param vo
	 * @return
	 * @throws DZFWarpException
	 */
	public Map<String,XjllbVO[]> queryEveryPeriod(QueryParamVO vo) throws DZFWarpException;
	
	
	
	/**
	 * 现金流量明细数据
	 * @param period
	 * @param pk_corp
	 * @return
	 * @throws BusinessException
	 */
	public XjllMxvo[] getXJllMX(String period, String pk_corp, String hc) throws  DZFWarpException ;


	/**
	 * 财务报税导出不同地区的格式
	 * @param qj
	 * @param corpIds
	 * @param qjlx
	 * @return
	 * @throws DZFWarpException
	 */
	public XjllbVO[] getXjllDataForCwBs(String qj, String corpIds, String qjlx) throws DZFWarpException;


	/**
	 * 获取现金流量期初数据
	 * @return
	 * @throws DZFWarpException
	 */
	public List<YntXjllqcyePageVO> bulidXjllQcData(List<YntXjllqcyePageVO> listvo, String pk_corp) throws DZFWarpException;
	
	
	
}
