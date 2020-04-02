package com.dzf.zxkj.platform.service.batchprint;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.platform.model.batchprint.BatchPrintSetVo;
import com.dzf.zxkj.platform.model.batchprint.BatchPrintSetQryVo;

import java.util.List;


/**
 * 批量打印设置
 * @author zhangj
 *
 */
public interface IBatchPrintSetTaskSer {


	/**
	 * 根据公司+期间查询成功的公司设置
	 * @param pk_corp
	 * @return
	 * @throws DZFWarpException
	 */
	public List<BatchPrintSetQryVo> queryPrintVOs2(String pk_corp , String period)  throws DZFWarpException;

	/**
	 * 查询公司归档任务
	 * @param parentcorpid
	 * @param corpname
	 * @param corpcode
	 * @param begdate
	 * @param enddate
	 * @param qry_zt
	 * @param cuserid
	 * @return
	 * @throws DZFWarpException
	 */
	public List<BatchPrintSetVo> queryPrintVOs(String parentcorpid, String corpname, String corpcode,
											   DZFDate begdate, DZFDate enddate, String qry_zt, String cuserid) throws DZFWarpException;

	/**
	 * 查询公司归档任务
	 * @param parentcorpid
	 * @param corpname
	 * @param corpcode
	 * @param begdate
	 * @param enddate
	 * @param qry_zt
	 * @return
	 * @throws DZFWarpException
	 */
	public List<BatchPrintSetVo> queryPrintVOs(String parentcorpid, String corpname, String corpcode,
											   DZFDate begdate, DZFDate enddate, String qry_zt) throws DZFWarpException;

	/**
	 * 保存归档任务设置
	 * @param setvo
	 * @param operatorid
	 * @param opedate
	 * @return
	 * @throws DZFWarpException
	 */
	public BatchPrintSetVo saveSetTaskVo(BatchPrintSetVo setvo, String operatorid, DZFDateTime opedate) throws DZFWarpException;

	/**
	 * 更新归档任务
	 * @param setvo
	 * @throws DZFWarpException
	 */
	public void updateSetTaskVO(BatchPrintSetVo setvo) throws DZFWarpException;

	/**
	 * 删除归档任务
	 * @param priid
	 * @throws DZFWarpException
	 */
	public void deleteSetTaskVo(String priid) throws DZFWarpException;
	
	public void printReport() throws DZFWarpException;
	
	public void printReportFromSetVos(BatchPrintSetVo vo) throws DZFWarpException;
	
	public Object[] downReport(String pk_corp,String id) throws DZFWarpException;
	
	public Object[] downReports(String pk_corp,String[] ids) throws DZFWarpException;

}
