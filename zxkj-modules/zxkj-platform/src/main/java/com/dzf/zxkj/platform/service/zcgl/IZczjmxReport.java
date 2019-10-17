package com.dzf.zxkj.platform.service.zcgl;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.zcgl.AssetDepreciaTionVO;
import com.dzf.zxkj.common.query.QueryParamVO;

/**
 * 资产折旧明细
 * @author zhangj
 *
 */
public interface IZczjmxReport {

	/**
	 * @param vos
	 * @param direction 0是本身，1是资产负债表
	 * @return
	 * @throws BusinessException
	 */
	public AssetDepreciaTionVO[] getZczjMxVOs(QueryParamVO vo, String asset_id) throws DZFWarpException;


	/**
	 * @param vos
	 * @param direction 0是本身，1是资产负债表
	 * @return
	 * @throws BusinessException
	 */
	public AssetDepreciaTionVO[] linkZczjMxVOs(QueryParamVO vo) throws DZFWarpException;



	/**
	 * 删除折旧明细
	 * @return
	 * @throws BusinessException
	 */
	public void deleteZjmx(String pk, String pk_voucheid) throws DZFWarpException;


	/**
	 * 生成凭证的逻辑
	 * @param pk
	 * @return
	 * @throws BusinessException
	 */
	public AssetDepreciaTionVO insertVoucher(CorpVO corpvo, String pk)throws DZFWarpException;
	/**
	 * 根据主键获取vo
	 * @param id
	 * @return
	 * @throws BusinessException
	 */
	public AssetDepreciaTionVO queryById(String id) throws DZFWarpException;


	/**
	 * 折旧汇总表联查明细
	 * 需要资产类别的编码和名称
	 */
	public AssetDepreciaTionVO[] getZczjMxVOsByHz(QueryParamVO vo, String catecode, String catename) throws DZFWarpException ;
	
	public AssetDepreciaTionVO[] queryAll(QueryParamVO vo) throws DZFWarpException;
}
