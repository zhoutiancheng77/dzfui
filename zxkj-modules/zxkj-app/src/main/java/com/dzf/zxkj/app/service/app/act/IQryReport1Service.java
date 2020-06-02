package com.dzf.zxkj.app.service.app.act;


import com.dzf.zxkj.app.model.nssb.NssbBean;
import com.dzf.zxkj.app.model.resp.bean.ReportBeanVO;
import com.dzf.zxkj.app.model.resp.bean.ReportResBean;
import com.dzf.zxkj.app.model.resp.bean.ResponseBaseBeanVO;
import com.dzf.zxkj.app.model.resp.rptbean.LrbYearBeanVo;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.model.report.CwgyInfoVO;

import java.util.List;
import java.util.Map;

public interface IQryReport1Service {

	/**
	 * 查询税负预警
	 *
	 * @param pk_corp
	 * @param year
	 * @throws DZFWarpException
	 */
	public ReportResBean qrySfYj(String pk_corp, String year) throws DZFWarpException;

	/**
	 * 纳税申报查询
	 *
	 * @param reportBean
	 * @return
	 * @throws DZFWarpException
	 */
	public ResponseBaseBeanVO qrynssb(ReportBeanVO reportBean) throws DZFWarpException;

	/**
	 * 获取辅助余额
	 *
	 * @param pk_corp
	 * @param period
	 * @param fzlb
	 * @return
	 * @throws DZFWarpException
	 */
	public ReportResBean qryFzye(String pk_corp, String period, String fzlb, int page, int rows, Integer versionno)
			throws DZFWarpException;

	/**
	 * 获取征期日历
	 *
	 * @param pk_corp
	 * @param year
	 * @return
	 * @throws DZFWarpException
	 */
	public ReportResBean qryZqrl(String pk_corp, String period) throws DZFWarpException;
	/**
	 * 查询纳税工作台数据
	 * @param pk_corp
	 * @param period
	 * @return
	 * @throws DZFWarpException
	 */
	public String queryNsgzt(String pk_corp, String period) throws DZFWarpException;


	/**
	 * 查询图片数量
	 * @param pk_corp
	 * @param begindate
	 * @param enddate
	 * @return
	 * @throws DZFWarpException
	 */
	public int queryLibnum(String pk_corp, DZFDate begindate, DZFDate enddate) throws DZFWarpException;

	/**
	 * 查询凭证数量
	 * @param pk_corp
	 * @param begindate
	 * @param enddate
	 * @return
	 * @throws DZFWarpException
	 */
	public int queryPzNum(String pk_corp, DZFDate begindate, DZFDate enddate) throws DZFWarpException;

	/**
	 * 取财务指标
	 * @param pk_corp
	 * @param period
	 * @return
	 * @throws DZFWarpException
	 */
	public Map<String, DZFDouble> queryCwzb(String pk_corp, String period) throws DZFWarpException;


	/**
	 * 获取每一项财务概要信息
	 * @param pk_corp
	 * @param period
	 * @return
	 * @throws DZFWarpException
	 */
	public Map<String, CwgyInfoVO> getCwgyMap(String pk_corp, String period) throws DZFWarpException;


	//	/**
//	 * 工资表查询
//	 *
//	 * @param reportBean
//	 * @return
//	 * @throws DZFWarpException
//	 */
//	public ResponseBaseBeanVO qrySalarAction(ReportBeanVO reportBean) throws DZFWarpException;
//
//	/**
//	 * 查询工资详情
//	 *
//	 * @param reportBean
//	 * @return
//	 * @throws DZFWarpException
//	 */
//	public ResponseBaseBeanVO qrySalarDetail(ReportBeanVO reportBean) throws DZFWarpException;
//
//	/**
//	 * 查询一个期间的纳税申报
//	 *
//	 * @return
//	 * @throws DZFWarpException
//	 */
//	public NssbBean qryNssbOnePeriod(String pk_corp, String period) throws DZFWarpException;
//
//	/**
//	 * 获取应缴纳税申报
//	 *
//	 * @param pk_corp
//	 * @param period
//	 * @return
//	 * @throws DZFWarpException
//	 */
//	public NssbBean getYjNsData(String pk_corp, String period) throws DZFWarpException;
//
//	/**
//	 * 查询利润情况
//	 *
//	 * @param pk_corp
//	 * @param year
//	 * @return
//	 * @throws DZFWarpException
//	 */
//	public List<LrbYearBeanVo> getLrbYearVo(String pk_corp, String year) throws DZFWarpException;
//
//





}
