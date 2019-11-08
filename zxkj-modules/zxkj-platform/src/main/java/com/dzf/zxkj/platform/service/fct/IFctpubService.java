package com.dzf.zxkj.platform.service.fct;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.platform.model.sys.CorpVO;

import java.util.List;

/**
 * 会计工厂公共接口
 * @author mfz
 *
 */
public interface IFctpubService {

	/**
	 * 判断企业公司是否被会计公司委托
	 * @param curDate 当前日期
	 * @param pk_corp 公司ID
	 * @return 如果被委托返回true
	 * @throws DZFWarpException
	 */
	public boolean isFctCorp(DZFDate curDate, String pk_corp)throws DZFWarpException;
	
	/**
	 * 返回客户委托给了哪个会计工厂
	 * @param pk_corp
	 * @return
	 * @throws DZFWarpException
	 */
	public String getAthorizeFactoryCorp(DZFDate curDate, String pk_corp)throws DZFWarpException;
	/**
	 * 判断会计公司是否被会计工厂确认
	 * @param pk_corp 会计公司
	 * @return
	 * @throws DZFWarpException
	 */
	public boolean isConfirmCorp(String pk_corp)throws DZFWarpException;
	
	/**
	 * 葛经纬调用
	 * @param pk_factory 会计工厂主键
	 * @param pk_user 用户主键
	 * @return
	 * @throws DZFWarpException
	 */
	public List<String> queryPowerCustomer(String pk_factory, String pk_user, String period)throws DZFWarpException;
	
	/**
	 * 祁祥调用
	 * @param pk_factory
	 * @return
	 * @throws DZFWarpException
	 */
	public List<CorpVO> queryCustomersByFactory(String pk_factory, String[] queryParam)throws DZFWarpException;
	public int queryTotalCustomersByFactory(String pk_factory, String[] queryParam)throws DZFWarpException;
}
