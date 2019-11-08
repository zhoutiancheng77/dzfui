package com.dzf.zxkj.platform.service.taxrpt.jiangsurequest;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.tax.TaxReportVO;
import com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.QueryResultVO;
import com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.RptBillVO;

import java.util.List;
import java.util.Map;

public interface ITaxRequestSrv {

	/**
	 * 报表清单查询
	 * @param token
	 * @TaxReportVO reportvo
	 * @return 
	 * @throws DZFWarpException
	 */
	public List<RptBillVO> queryRptBillList(String token, TaxReportVO reportvo) throws DZFWarpException;
		
	/**
	 * 报表期初数据
	 * @param token
	 * @param reportvo	报表vo
	 * @throws DZFWarpException
	 */
	public Map<String, Object> queryQCData(String token, TaxReportVO reportvo) throws DZFWarpException;
	
	/**
	 * 申报提交接口
	 * @param token
 	 * @param token
	 * @param reportvo	报表vo
	 * @param req_data	请求报表内容
	 * @throws DZFWarpException
	 */
	public void rptSubmit(String token, TaxReportVO reportvo, String req_data)
			throws DZFWarpException;
	/**
	 * 申报查询
 	 * @param token
	 * @param reportvo	报表vo
	 * @throws DZFWarpException
	 */
	public QueryResultVO queryRptStatus(String token, TaxReportVO reportvo)
			throws DZFWarpException;
	/**
	 * 申报作废接口
	 * @param token
	 * @param reportvo	报表vo
	 * @param lsh		流水号
	 * @throws DZFWarpException
	 */
	public void cancelRpt(String token, TaxReportVO reportvo, String lsh) throws DZFWarpException;
//	/**
//	 * 获取填报类型清单
//	 * @param nsrsbh
//	 * @param token
//	 * @return
//	 * @throws DZFWarpException
//	 */
//	public TaxTypeListDetailVO[] querySbTypeList(String nsrsbh, String token) throws DZFWarpException;

}
