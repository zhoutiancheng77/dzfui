package com.dzf.zxkj.report.service.cwzb;


import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.query.KmReoprtQueryParamVO;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.model.report.KmMxZVO;

/**
 * 查询公共方法
 * @author zhangj
 *
 */
public interface IKmReportPub {

	/**
	 * 根据辅助类别查询发生
	 * @param pamvo
	 * @return
	 * @throws DZFWarpException
	 */
	public KmMxZVO[] getKmfsConFZ(KmReoprtQueryParamVO pamvo) throws DZFWarpException;
	
	
	/**
	 * 查询单个科目的期初(只是末级科目)
	 * @return
	 * @throws DZFWarpException
	 */
	public DZFDouble getKmQcValue(String pk_accsubj, DZFDate begindate, String pk_corp) throws DZFWarpException;
	
}
