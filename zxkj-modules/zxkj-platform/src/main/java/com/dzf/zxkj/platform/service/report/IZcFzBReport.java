package com.dzf.zxkj.platform.service.report;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.report.FseJyeVO;
import com.dzf.zxkj.platform.model.report.ZcFzBVO;

import java.util.List;
import java.util.Map;

public interface IZcFzBReport {

	/**
	 * 资产负债表取数
	 * @param date
	 * @param pk_corp
	 * @param ishashz  是否包含未记账
	 * @return
	 * @throws BusinessException
	 */
	public ZcFzBVO[] getZCFZBVOs(String period, String pk_corp, String ishasjz, String ishasye) throws DZFWarpException;

	/**
	 * 资产负债查询(不包含平衡原因)
	 * @param period
	 * @param pk_corp
	 * @param ishasjz 是否包含未记账(可以为空)
	 * @param hasyes
	 * @return
	 * @throws DZFWarpException
	 */
	public ZcFzBVO[] getZCFZBVOs(String period, String pk_corp, String ishasjz, String[] hasyes) throws  DZFWarpException ;

	/**
	 * 资产负债查询,单独某几个项目(不包含平衡原因)
	 * @param period
	 * @param pk_corp
	 * @param ishasjz 是否包含未记账(可以为空)
	 * @param hasyes
	 * @param xmids 行次编码（id只是支持了3个制度，先不用）
	 * @return
	 * @throws DZFWarpException
	 */
	public ZcFzBVO[] getZCFZBVOsConXmids(String period, String pk_corp, String ishasjz, String[] hasyes, List<String> xmids) throws  DZFWarpException ;

	/**
	 * 资产负债表查询(包含不平衡原因,原因有效率因素，不要轻易用这个接口，如果只是为了查询，使用上面的接口)
	 * @param period
	 * @param pk_corp
	 * @param ishasjz
	 * @param hasyes
	 * @return
	 * @throws DZFWarpException
	 */
	public Object[] getZCFZBVOsConMsg(String period, String pk_corp, String ishasjz, String[] hasyes) throws  DZFWarpException ;



	/**
	 * 资产负债表查询(根据"发生额余额表"取数) 单个月份的
	 * @param pk_corp
	 * @param hasyes
	 * @param mapc
	 * @param fvos
	 * @return
	 * @throws DZFWarpException
	 */
	public ZcFzBVO[] getZcfzVOs(String pk_corp, String[] hasyes, Map<String, YntCpaccountVO> mapc, FseJyeVO[] fvos) throws DZFWarpException ;



	/**
	 * 资产负债表查询(根据"发生额余额表"取数) 多个月份的
	 * @param pk_corp
	 * @param hasyes
	 * @param mapc
	 * @param fvos
	 * @return
	 * @throws DZFWarpException
	 */
	public List<ZcFzBVO[]> getZcfzVOs(DZFDate begdate, DZFDate enddate, String pk_corp, String ishasjz, String[] hasyes, Object[] qryobjs) throws DZFWarpException ;

	
}
