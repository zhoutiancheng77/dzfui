package com.dzf.zxkj.report.service.cwzb;


import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.platform.model.report.KmMxZVO;

import java.util.List;

/**
 * 现金/银行日记账
 * @author jiaozj
 *
 */
public interface IXjRjZReport {
	public KmMxZVO[] getSpecCashPay(String pk_corp, DZFDate beginDate, DZFDate endDate, DZFBoolean includesg, String pk_currency, String kms, List<String> kmcodelist) throws DZFWarpException;
	/**
	 * 现金日记账--多币种
	 * @param period
	 * @param pk_corp
	 * @return
	 * @throws BusinessException
	 */
	public KmMxZVO[] getXJRJZVOs(String pk_corp, String kms, DZFDate begindate, DZFDate enddate, DZFBoolean xswyewfs, DZFBoolean xsyljfs, DZFBoolean ishasjz, DZFBoolean ishassh, String pk_currency) throws  DZFWarpException ;

	/**
	 * 现金日记账
	 * @param period
	 * @param pk_corp
	 * @return
	 * @throws BusinessException
	 */
	public KmMxZVO[] getXJRJZVOs(String pk_corp, String kms, DZFDate begindate, DZFDate enddate, DZFBoolean xswyewfs, DZFBoolean xsyljfs, DZFBoolean ishasjz, DZFBoolean ishassh) throws  DZFWarpException ;


	/**
	 * 现金日记账--包含下级科目
	 * @param period
	 * @param pk_corp
	 * @return
	 * @throws BusinessException
	 */
	public KmMxZVO[] getXJRJZVOsConMo(String pk_corp, String kmsbegin, String kmsend,
                                      DZFDate begindate, DZFDate enddate, DZFBoolean xswyewfs, DZFBoolean xsyljfs,
                                      DZFBoolean ishasjz, DZFBoolean ishassh, String pk_currency, List<String> kmcodelist, Object[] qryobjs) throws  DZFWarpException ;
	
}
