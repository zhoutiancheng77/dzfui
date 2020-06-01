package com.dzf.zxkj.app.service.app.act;


import com.dzf.zxkj.app.model.resp.bean.ReportBeanVO;
import com.dzf.zxkj.app.model.resp.bean.ResponseBaseBeanVO;
import com.dzf.zxkj.base.exception.DZFWarpException;

public interface IQryReportService {
	
	public ResponseBaseBeanVO qryDaily(ReportBeanVO qryDailyBean) throws DZFWarpException;

	/**
	 * 月报
	 * 
	 * @param qryDailyBean
	 * @return
	 * @throws DZFWarpException
	 */
	public ResponseBaseBeanVO qryMonthRep(ReportBeanVO qryDailyBean) throws DZFWarpException;

	public ResponseBaseBeanVO qryAssetsLiab(ReportBeanVO ReportBeanVO) throws DZFWarpException;

	public ResponseBaseBeanVO qryProfits(ReportBeanVO ReportBeanVO) throws DZFWarpException;

	public ResponseBaseBeanVO qryCashFlow(ReportBeanVO ReportBeanVO) throws DZFWarpException;

	public ResponseBaseBeanVO qryPayTax(ReportBeanVO ReportBeanVO) throws DZFWarpException;

	public ResponseBaseBeanVO qryDetailReport(ReportBeanVO ReportBeanVO) throws DZFWarpException;

	public ResponseBaseBeanVO expendption(ReportBeanVO reportBean) throws DZFWarpException;

	public ResponseBaseBeanVO profitgrow(ReportBeanVO reportBean) throws DZFWarpException;

	public ResponseBaseBeanVO qryDeclarForm(ReportBeanVO reportBean) throws DZFWarpException;

}
