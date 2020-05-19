package com.dzf.zxkj.report.service.cwbb;


import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.model.report.ExpendpTionVO;

/**
 * 手机端报表接口
 * @author zhangj
 *
 */
public interface IAppReport {
	
	public ExpendpTionVO[] getAppSjMny(String period, String pk_corp, String pk_currency) throws BusinessException ;
	
	
	/**
	 * 取每年的净利润的增长值
	 */
	public DZFDouble[] getAppNetProfit(String year, String pk_corp) throws BusinessException;

}
