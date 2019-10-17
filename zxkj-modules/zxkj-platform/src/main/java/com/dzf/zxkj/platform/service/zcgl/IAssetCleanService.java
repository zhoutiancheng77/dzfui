package com.dzf.zxkj.platform.service.zcgl;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.zcgl.AssetCleanVO;
import com.dzf.zxkj.platform.model.zcgl.AssetQueryCdtionVO;

import java.util.List;

public interface IAssetCleanService {
	public List<AssetCleanVO> query(String pk_corp, AssetQueryCdtionVO qryVO)
			throws DZFWarpException;

	public void delete(AssetCleanVO vo) throws DZFWarpException;

	public void insertToGL(String loginDate, CorpVO corpvo, AssetCleanVO vo)
			throws DZFWarpException;// 转总账

	public AssetCleanVO queryById(String id) throws DZFWarpException;//查询单条数据

	public AssetCleanVO refresh(String pk_acs)throws DZFWarpException;// 刷新单个单据的值

	/**
	 * 处理固定资产清理，包括生成资产清理单和凭证
	 * @param assetcardVOs
	 * @throws BusinessException
	 */
	public void processAssetClears(String loginDate, CorpVO corpvo, SuperVO[] assetcardVOs, String loginuserid) throws DZFWarpException;
	
	/**
	 * 更新资产清理单是否转总账标记
	 * @param pk_assetclear
	 * @param istogl
	 * @param pk_voucher
	 * @throws BusinessException
	 */
	public void updateACToGLState(String pk_assetclear, boolean istogl, String pk_voucher) throws DZFWarpException;
}
