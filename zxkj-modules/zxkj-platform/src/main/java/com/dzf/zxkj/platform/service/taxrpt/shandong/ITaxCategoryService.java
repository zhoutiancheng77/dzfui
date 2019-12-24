package com.dzf.zxkj.platform.service.taxrpt.shandong;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.sys.CorpTaxVo;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.model.tax.TaxReportVO;
import com.dzf.zxkj.platform.service.taxrpt.spreadjs.SpreadTool;

import java.util.HashMap;
import java.util.Map;

public interface ITaxCategoryService {

	public Object sendTaxReport(CorpVO corpVO, Map objMapReport, SpreadTool spreadtool, TaxReportVO reportvo,
								UserVO userVO) throws DZFWarpException;

	public HashMap<String, Object> getQcData(CorpVO corpvo, TaxReportVO reportvo) throws DZFWarpException;

	public String[] getCondition(String pk_taxreport, UserVO userVO, TaxReportVO reportvo) throws DZFWarpException;

	/**
	 * 刷新申报状态
	 * 
	 * @param corpvo
	 * @param reportvo
	 * @throws DZFWarpException
	 */
	public void queryDeclareStatus(CorpVO corpvo, CorpTaxVo corptaxvo, TaxReportVO reportvo) throws DZFWarpException;

}
