package com.dzf.zxkj.platform.service.taxrpt;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.sys.CorpTaxVo;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.model.tax.TaxPaymentVO;
import com.dzf.zxkj.platform.model.tax.TaxReportDetailVO;
import com.dzf.zxkj.platform.model.tax.TaxReportVO;
import com.dzf.zxkj.platform.model.tax.TaxRptTempletVO;
import com.dzf.zxkj.platform.service.taxrpt.spreadjs.SpreadTool;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface ITaxRptService {
	/**
	 * 查询填报类型列表
	 */
	public List<TaxReportVO> getTypeList(CorpVO corpvo, CorpTaxVo corptaxvo, String period, String operatorid, String operatedate, SingleObjectBO sbo)throws DZFWarpException;
	
	// 获取预置模板数据
//	List<RptBillVO> getRptBillVO(TaxReportVO paravo, SingleObjectBO sbo, CorpVO corpvo) throws DZFWarpException;

	// 审批前检查
	void checkBeforeProcessApprove(TaxReportVO reportvo, SingleObjectBO sbo, CorpVO corpvo) throws DZFWarpException;

	// 数据检查
	String checkReportData(Map mapJson, CorpVO corpvo, TaxReportVO reportvo,
						   HashMap<String, TaxReportDetailVO> hmRptDetail, SingleObjectBO sbo) throws DZFWarpException;
	
	// 数据检查
	String checkReportDataWarning(Map mapJson, CorpVO corpvo, TaxReportVO reportvo,
                                  HashMap<String, TaxReportDetailVO> hmRptDetail, SingleObjectBO sbo) throws DZFWarpException;

	// 获取期初数据
	HashMap<String, Object> getQcData(CorpVO corpvo, TaxReportVO reportvo, SingleObjectBO sbo) throws DZFWarpException;

//	TaxTypeListDetailVO[] getTaxTypeListDetailVO(boolean showYearInTax, String yearmonth, CorpVO corpvo,
//			SingleObjectBO sbo) throws DZFWarpException;

	// 读取报表审核检查条件数组
	String[] getCondition(String pk_taxreport, UserVO userVO, TaxReportVO reportvo, SingleObjectBO sbo)
			throws DZFWarpException;

	// 上传报文
	Object sendTaxReport(CorpVO corpVO, UserVO userVO, Map objMapReport, SpreadTool spreadtool, TaxReportVO reportvo,
						 SingleObjectBO sbo) throws DZFWarpException;

	/**
	 * 刷新申报状态
	 * 
	 * @param corpvo
	 * @param reportvo
	 * @throws DZFWarpException
	 */
	public void getDeclareStatus(CorpVO corpvo, CorpTaxVo corptaxvo, TaxReportVO reportvo) throws DZFWarpException;

	/**
	 * 申报作废
	 */
	public void processObsoleteDeclare(CorpVO corpvo, TaxReportVO reportvo) throws DZFWarpException;

	/**
	 * 根据公司获取地区
	 * @param corpvo
	 * @return
	 * @throws DZFWarpException
	 */
	public String getLocation(CorpVO corpvo) throws DZFWarpException;

	/**
	 * 零申报
	 * 
	 * @param typedetailvo
	 * @param corpvo
	 * @param singleObjectBO
	 * @throws DZFWarpException
	 */
	public void processZeroDeclaration(TaxReportVO typedetailvo,
                                       CorpVO corpvo, CorpTaxVo corptaxvo, SingleObjectBO singleObjectBO)
			throws DZFWarpException;
	
	/**
	 * 查询完税凭证
	 * @param corpvo
	 * @param reportvo
	 * @return
	 * @throws DZFWarpException
	 */
	public TaxPaymentVO[] queryTaxPayment(CorpVO corpvo, TaxReportVO reportvo) throws DZFWarpException;
	
	/**
	 * 检查税表
	 * @param corpvo
	 * @param reportvo
	 * @return
	 * @throws DZFWarpException
	 */
	public String checkReportList(CorpVO corpvo, CorpTaxVo corptaxvo, List<TaxReportVO> list) throws DZFWarpException;
	
	public  List<TaxRptTempletVO> queryRptTempletVOs(String pk_corp, String period)  throws DZFWarpException;
}
