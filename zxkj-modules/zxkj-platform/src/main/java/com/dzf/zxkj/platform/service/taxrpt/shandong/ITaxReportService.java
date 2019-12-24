package com.dzf.zxkj.platform.service.taxrpt.shandong;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.tax.TaxReportVO;
import com.dzf.zxkj.platform.service.taxrpt.spreadjs.SpreadTool;

import java.util.Map;

public interface ITaxReportService {

	/**
	 * 
	 * @param corpVO
	 *            公司
	 * @param vos
	 *            对照信息
	 * @param table
	 *            验证登录返回信息
	 * @return
	 * @throws DZFWarpException
	 */
	public Object sendTaxReport(CorpVO corpVO, Map objMapReport, SpreadTool spreadtool,
								TaxReportVO reportvo) throws DZFWarpException;

}
