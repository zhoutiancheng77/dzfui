package com.dzf.zxkj.platform.service.zncs;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.model.sys.CorpVO;

import java.util.List;

public interface IZncsService {

	/**
	 * 查询当前小企业所属代账公司白名单下的所有企业信息
	 * 可能会计工厂
	 * @param pk_corp
	 * @return
	 * @throws DZFWarpException
	 */
	public List<CorpVO> queryWhiteListCorpVOs(String pk_corp)throws DZFWarpException;
	
	/**
	 * 王钊宁调用
	 * 报税销项清单取数
	 * @param pk_corp 公司
	 * @param period 期间
	 * @param tickflag 1普票 2专票3未开票(只有12有值)
	 * @param taxrate 税率
	 * @return 0劳务金额1劳务税额2货物金额3货物税额
	 * @throws DZFWarpException
	 */
	public List<DZFDouble> queryVATSaleInvoiceMny(String pk_corp, String period, Integer tickflag, String[] taxrate)throws DZFWarpException;
	/**
	 * 王钊宁调用
	 * 报税进项清单取数
	 * @param pk_corp 公司
	 * @param period 期间
	 * @param tickflag 1普票 2专票3未开票(只有12有值)
	 * @param bs 标识 1空2旅客运输3农产品
	 * @param bs 认证标识：0已认证/1未认证/空
	 * @param taxrate 税率
	 * @return 0金额1税额
	 * @throws DZFWarpException
	 */
	public List<DZFDouble> queryVATIncomeInvoiceMny(String pk_corp, String period, Integer tickflag, String[] taxrate, Integer bs, Integer rzbs)throws DZFWarpException;

	/**
	 * 报税进项清单数量取数
	 * @param pk_corp
	 * @param period
	 * @param tickflag
	 * @param bs 1空2旅客运输3农产品
	 * @param rzbs
	 * @return
	 * @throws DZFWarpException
	 */
	public DZFDouble queryVATIncomeInvoiceNumber(String pk_corp, String period, Integer tickflag, Integer bs, Integer rzbs)throws DZFWarpException;
}
