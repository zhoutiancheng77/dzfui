package com.dzf.zxkj.report.service.cwbb;

import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.report.entity.LrbRptSetVo;
import com.dzf.zxkj.report.entity.ZcfzRptSetVo;

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
	 * @throws Exception
	 */
	ZcfzRptSetVo[] queryZcfzRptVOs(String pk_trade_accountschema) throws Exception;

	/**
	 * 从数据库获取,,目前数据库没这些数据，暂时用代码获取
	 * 只是查询一级科目(4位长度)，如果有二级的会自动截取，查询
	 * @param pk_trade_accountschema
	 * @return
	 * @throws Exception
	 */
	public List<String> queryZcFzKmFromSetVo(String pk_trade_accountschema) throws Exception;

	
	/**
	 * 从代码获取科目编码
	 * 只是查询一级科目(4位长度)，如果有二级的会自动截取，查询
	 * @param pk_corp
	 * @param xmvalues 目前只能按照行次（id还不能用）
	 * @return
	 * @throws Exception
	 */
	public List<String> queryZcfzKmFromDaima(String pk_corp, List<String> xmvalues, String[] hasyes) throws Exception;
	
	/**
	 * 从数据库获取,,目前数据库没这些数据，暂时用代码获取
	 * 只是查询一级科目(4位长度)，如果有二级,三级等的会自动截取，查询
	 * @param pk_trade_accountschema
	 * @return
	 * @throws Exception
	 */
	public String queryZcFzKmsToString(String pk_trade_accountschema) throws Exception;

	/**
	 * 从数据库获取,,目前数据库没这些数据，暂时用代码获取
	 * 查询利润表设置
	 * @param pk_trade_accountschema
	 * @return
	 * @throws Exception
	 */
	public LrbRptSetVo[] queryLrbRptVos(String pk_trade_accountschema) throws Exception;
	
	
	/**
	 * 从数据库获取,,目前数据库没这些数据，暂时用代码获取
	 * 只是查询一级科目(4位长度)，如果有二级的会自动截取，查询
	 * @param pk_trade_accountschema
	 * @return
	 * @throws Exception
	 */
	public List<String> queryLrbKmsFromSetVo(String pk_trade_accountschema) throws Exception;
	
	
	/**
	 * 从代码获取科目编码
	 * 只是查询一级科目(4位长度)，如果有二级的会自动截取，查询
	 * @param pk_trade_accountschema
	 * @return
	 * @throws Exception
	 */
	public List<String> queryLrbKmsFromDaima(CorpVO corpVO, List<String> xmid) throws Exception;

	/**
	 * 从数据库获取,,目前数据库没这些数据，暂时用代码获取
	 * 只是查询一级科目(4位长度)，如果有二级的会自动截取，查询
	 * @param pk_trade_accountschema
	 * @return
	 * @throws Exception
	 */
	public String queryLrbKmsToString(String pk_trade_accountschema) throws Exception;

}
