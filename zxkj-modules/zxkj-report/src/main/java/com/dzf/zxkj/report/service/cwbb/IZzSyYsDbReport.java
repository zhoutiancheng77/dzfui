package com.dzf.zxkj.report.service.cwbb;


import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.report.ZzSyYsDBVO;

/**
 * 增值税和营业税月度申报对比表
 *
 */
public interface IZzSyYsDbReport {

	/**
	 * 增值税和营业税月度申报对比表取数(多个公司对应一个期间)
	 * @param date
	 * @param pk_corp
	 * @return
	 * @throws BusinessException
	 */
	public ZzSyYsDBVO[] getZZSYYSDBVOs(String period, String pk_corp) throws DZFWarpException;
	
	/**
	 * 增值税和营业税月度申报对比表取数(一个公司对应多个期间)
	 * @param period
	 * @param pk_corp
	 * @return
	 * @throws BusinessException
	 */
	public ZzSyYsDBVO[] getZZSYYSDBVOsForPeriod(String[] periods, String pk_corp) throws  DZFWarpException ;
}
