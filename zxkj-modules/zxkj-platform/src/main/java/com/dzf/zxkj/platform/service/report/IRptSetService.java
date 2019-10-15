package com.dzf.zxkj.platform.service.report;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.report.LrbRptSetVo;
import com.dzf.zxkj.platform.model.report.ZcfzRptSetVo;

import java.util.List;

/**
 * 查询报表设置
 * @author zhangj
 *
 */
public interface IRptSetService {

	/**
	 * 从数据库获取,,目前数据库没这些数据，暂时用代码获取
	 * 查询资产负债表设置
	 * @param pk_trade_accountschema
	 * @return
	 * @throws DZFWarpException
	 */
	public ZcfzRptSetVo[] queryZcfzRptVOs(String pk_trade_accountschema) throws DZFWarpException;

	/**
	 * 从数据库获取,,目前数据库没这些数据，暂时用代码获取
	 * 只是查询一级科目(4位长度)，如果有二级的会自动截取，查询
	 * @param pk_trade_accountschema
	 * @return
	 * @throws DZFWarpException
	 */
	public List<String> queryZcFzKmFromSetVo(String pk_trade_accountschema) throws DZFWarpException;

	
	/**
	 * 从代码获取科目编码
	 * 只是查询一级科目(4位长度)，如果有二级的会自动截取，查询
	 * @param pk_corp
	 * @param xmvalues 目前只能按照行次（id还不能用）
	 * @return
	 * @throws DZFWarpException
	 */
	public List<String> queryZcfzKmFromDaima(String pk_corp, List<String> xmvalues, String[] hasyes) throws DZFWarpException;

	/**
	 * 从数据库获取,,目前数据库没这些数据，暂时用代码获取
	 * 只是查询一级科目(4位长度)，如果有二级,三级等的会自动截取，查询
	 * @param pk_trade_accountschema
	 * @return
	 * @throws DZFWarpException
	 */
	public String queryZcFzKmsToString(String pk_trade_accountschema) throws DZFWarpException;

	/**
	 * 从数据库获取,,目前数据库没这些数据，暂时用代码获取
	 * 查询利润表设置
	 * @param pk_trade_accountschema
	 * @return
	 * @throws DZFWarpException
	 */
	public LrbRptSetVo[] queryLrbRptVos(String pk_trade_accountschema) throws DZFWarpException;


	/**
	 * 从数据库获取,,目前数据库没这些数据，暂时用代码获取
	 * 只是查询一级科目(4位长度)，如果有二级的会自动截取，查询
	 * @param pk_trade_accountschema
	 * @return
	 * @throws DZFWarpException
	 */
	public List<String> queryLrbKmsFromSetVo(String pk_trade_accountschema) throws DZFWarpException;


	/**
	 * 从代码获取科目编码
	 * 只是查询一级科目(4位长度)，如果有二级的会自动截取，查询
	 * @param pk_trade_accountschema
	 * @return
	 * @throws DZFWarpException
	 */
	public List<String> queryLrbKmsFromDaima(String pk_trade_accountschema, List<String> xmid) throws DZFWarpException;

	/**
	 * 从数据库获取,,目前数据库没这些数据，暂时用代码获取
	 * 只是查询一级科目(4位长度)，如果有二级的会自动截取，查询
	 * @param pk_trade_accountschema
	 * @return
	 * @throws DZFWarpException
	 */
	public String queryLrbKmsToString(String pk_trade_accountschema) throws DZFWarpException;

}
