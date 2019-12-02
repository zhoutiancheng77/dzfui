package com.dzf.zxkj.platform.service.zncs.image;

import java.util.List;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.platform.model.sys.DZFBalanceVO;
import com.dzf.zxkj.platform.model.zncs.DZFBalanceBVO;
import com.dzf.zxkj.platform.model.zncs.QueryBalanceVO;

public interface IBalanceService {
	
	/**
	 * 充值
	 * 网站充值统一处理
	 * @param balanceVO
	 * @return
	 * @throws DZFWarpException
	 */
	public DZFBalanceVO recharge(DZFBalanceVO balanceVO) throws DZFWarpException;
	/**
	 * 扣费
	 * 
	 * @param balanceBVO 消费明细VO
	 		 * pk_dzfservicedes 产品(IDzfServiceConst...)
			 * changedcount -> 金额或者数量
			 * pk_corp -> 客户
			 * pk_user -> 操作员
			 * pk_corpkjgs -> 会计公司
			 * opdate -> 消费日期
			 * period -> 期间:2017-10,(可空,空取opdate前7位)
			 * description -> 描述(可空)
	 * @throws DZFWarpException
	 */
	public void consumption(DZFBalanceBVO balanceBVO) throws DZFWarpException;
	/**
	 * 会计工厂扣费
	 * @param balanceBVO
	 * @throws DZFWarpException
	 */
	public void consumptionByFct(DZFBalanceBVO balanceBVO, DZFDate date) throws DZFWarpException;
	/**
	 * 检查是否已经扣费
	 * 
	 * @param IDzfServiceConst 产品(IDzfServiceConst...)
	 * @param period 期间:2017-10
	 * @param pk_corp 客户
	 * @return DZFBoolean true:已扣费
	 * @throws DZFWarpException
	 */
	public DZFBoolean isAlreadyConsumption(String pk_dzfservicedes, String period,
										   String pk_corp) throws DZFWarpException;
	
	/**
	 * 消费明细查询
	 * @param queryVO
			 * pk_dzfservicedes 产品(IDzfServiceConst...)
			 * beginDate 开始日期
			 * endDate 结束日期
			 * pk_corpkjgs 会计公司[]
	 * @return
	 * @throws DZFWarpException
	 */
	public List<DZFBalanceBVO> queryBalanceDetails(QueryBalanceVO queryVO)throws DZFWarpException;
}
