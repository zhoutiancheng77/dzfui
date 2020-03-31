package com.dzf.zxkj.platform.service.batchprint;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.platform.model.batchprint.BatchPrintSetVo;

import java.util.List;


/**
 * 批量打印设置
 * @author zhangj
 *
 */
public interface IBatchPrintSetSer {
	
	public List<BatchPrintSetVo> queryPrintVOs(String parentcorpid, String corpname, String corpcode,
											   DZFDate begdate, DZFDate enddate, String qry_zt, String cuserid) throws DZFWarpException;

	public List<BatchPrintSetVo> queryPrintVOs(String parentcorpid, String corpname, String corpcode,
											   DZFDate begdate, DZFDate enddate, String qry_zt) throws DZFWarpException;

	public BatchPrintSetVo saveSetVo(BatchPrintSetVo setvo, String operatorid, DZFDateTime opedate) throws DZFWarpException;

	public void updateSetVO(BatchPrintSetVo setvo) throws DZFWarpException;

	public void deleteSetVo(String priid) throws DZFWarpException;
	
	public void printReport() throws DZFWarpException;
	
	public void printReportFromSetVos(BatchPrintSetVo vo) throws DZFWarpException;
	
	public Object[] downReport(String pk_corp,String id) throws DZFWarpException;
	
	public Object[] downReports(String pk_corp,String[] ids) throws DZFWarpException;

}
