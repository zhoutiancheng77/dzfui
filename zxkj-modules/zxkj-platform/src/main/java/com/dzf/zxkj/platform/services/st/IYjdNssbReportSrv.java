package com.dzf.zxkj.platform.services.st;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.st.StBaseVO;
import com.dzf.zxkj.platform.model.st.StNssbInfoVO;

import java.util.Map;

public interface IYjdNssbReportSrv {

	/**
	 * 根据年度，公司
	 * 生成11张附表
	 * */
//	public abstract Map<String,StBaseVO[]> GenNssbBasicReport(String cyear,String pk_corp) throws BusinessException;
	public abstract Map<String, StBaseVO[]> GenNssbBasicReport(StNssbInfoVO infvo) throws DZFWarpException;
	/**
	 * 根据会计年度，公司
	 * 从10张附表生成主表
	 * */
	public abstract Map<String,StBaseVO[]> GenNssbMainReport(StNssbInfoVO infvo) throws DZFWarpException;
	/**
	 * 月季度
	 */
	public abstract Map<String,StBaseVO[]> GenYJDNssbMainReport(StNssbInfoVO infvo) throws DZFWarpException;
	/**
	 * 值修改后重新计算
	 * */
	public StBaseVO[] reCalculate(String report, StBaseVO[] reportvos) throws DZFWarpException;

	/**
	 * 全表重新计算
	 * */
	public Map<String,StBaseVO[]>  reCalculateMain(Map<String, StBaseVO[]> vosmap) throws DZFWarpException;

	/**
	 * 单表保存
	 * */
	public void updateSingleReport(String reportcode, StBaseVO[] reportvos) throws DZFWarpException;

	/**
	 * 全表保存
	 * */
	public void updateReportAll(Map<String, StBaseVO[]> vosmap) throws DZFWarpException;

	/**
	 * 单表查询
	 * */
	public StBaseVO[] querySingleReport(String reportcode, String cyear, String period, String pk_corp);
	
/*	*//**
	 * 全表查询
	 * *//*
//	public Map<String,StBaseVO[]>  queryReportAll(String cyear,String pk_corp);
	public Map<String,StBaseVO[]> queryReportAll(StNssbInfoVO infvo) throws BusinessException;*/
	/**
	 * 月季度纳税申报查询
	 */
	public Map<String,StBaseVO[]> queryYJDReport(StNssbInfoVO infvo) throws DZFWarpException;
	
	/**
	 * 审核
	 * */
	public void approveRpinfo(StNssbInfoVO infvo) throws DZFWarpException;
	
	/**
	 * 取消审核
	 * */
	public void unaApproveRpinfo(StNssbInfoVO infvo) throws DZFWarpException;
	
	
}
