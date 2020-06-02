package com.dzf.zxkj.app.dao.app;

import java.util.List;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.platform.model.tax.workbench.BsWorkbenchVO;

/**
 * 数据库报表，执行sql语句，返回结果集的地方 
 * @author zhangj
 *
 */
public interface IQryReportDao {
	
	
	/**
	 * 纳税工作台语句
	 * @param pk_corp
	 * @param period
	 * @return
	 * @throws DZFWarpException
	 */
	public List<BsWorkbenchVO> queryNsgzt(String pk_corp, String period) throws DZFWarpException;
	
	
	/**
	 * 图片数语句
	 * @param pk_corp
	 * @param startdate
	 * @param enddate
	 * @return
	 * @throws DZFWarpException
	 */
	public int qryMonthPics(String pk_corp, DZFDate startdate, DZFDate enddate) throws DZFWarpException;
	
	
	/**
	 * 公司凭证数量
	 * @param pk_corp
	 * @param startdate
	 * @param enddate
	 * @return
	 * @throws DZFWarpException
	 */
	public int qryMonthVouchers(String pk_corp, DZFDate startdate, DZFDate enddate) throws DZFWarpException;

}
