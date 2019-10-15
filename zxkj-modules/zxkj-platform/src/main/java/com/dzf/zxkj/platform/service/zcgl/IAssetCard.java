package com.dzf.zxkj.platform.service.zcgl;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.platform.model.jzcl.GdzcjzVO;
import com.dzf.zxkj.platform.model.jzcl.QmclVO;
import com.dzf.zxkj.platform.model.qcset.QcYeVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.zcgl.AssetDepreciaTionVO;
import com.dzf.zxkj.platform.model.zcgl.AssetcardVO;
import com.dzf.zxkj.platform.model.zcgl.ValuemodifyVO;

public interface IAssetCard {
	/**
	 * 更新固定资产卡片是否转总账标记
	 * @param pk_assetCard
	 * @param istogl
	 * @param pk_voucher
	 * @param ope 0 保存，1 删除
	 * @throws BusinessException
	 */
	public void updateToGLState(String pk_assetCard, boolean istogl, String pk_voucher, Integer ope) throws DZFWarpException;
	/**
	 * 更新固定资产折旧是否转总账标记
	 * @param pk_assetCard
	 * @param istogl
	 * @param pk_voucher
	 * @throws BusinessException
	 */
	public void updateDepToGLState(String pk_assetdep, boolean istogl, String pk_voucher) throws DZFWarpException;

	/**
	 * 更新固定资产卡片原值
	 * @param pk_assetCard
	 * @param assetmny
	 * @throws BusinessException
	 */
	public void updateAssetMny(AssetcardVO vo, double assetmny, ValuemodifyVO modifyvo) throws DZFWarpException;
	/**
	 * 更新固定资产是否清理标记
	 * @param pk_assetCards
	 * @param isclear
	 * @throws BusinessException
	 */
	public void updateIsClear(String[] pk_assetCards, boolean isclear) throws DZFWarpException;
	/**
	 * 更新固定资产是否清理标记
	 * @param assetcardVO
	 * @param isclear
	 * @throws BusinessException
	 */
	public void updateIsClear(AssetcardVO assetcardVO, boolean isclear) throws DZFWarpException;//ASSETCARDVO 1
	/**
	 * 资产折旧转总账
	 * @param assetdepVO
	 * @throws BusinessException
	 */
	//public void processeDreciationToGL(CorpVO corpvo,SuperVO assetdepVO)  throws DZFWarpException;//ASSETDEPRECIATIONVO 2
	/**
	 * 删除折旧明细
	 * @param assetdepVO
	 * @throws BusinessException
	 */
	public void deleteAssetDepreciation(AssetDepreciaTionVO assetdepVO) throws DZFWarpException;//ASSETDEPRECIATIONVO 3
	/**
	 * 计提折旧
	 * @param qmclVOs
	 */
	public QmclVO updateDepreciate(CorpVO corpvo, QmclVO qmclVOs, String coperatorid) throws DZFWarpException;

	/**
	 * 计提折旧(选择的资产计提折旧,主要为了处理资产清理)
	 * @param assetcarvo
	 * @throws DZFWarpException
	 */
	public void updateExecuteDepreciate(CorpVO corpvo, AssetcardVO[] assetcarvo, String period, String coperatorid) throws DZFWarpException;
	/**
	 * 反计提折旧
	 * @param qmclVOs
	 */
	public QmclVO rollbackDepreciate(QmclVO qmclVOs) throws DZFWarpException;
	/**
	 * 处理计提折旧
	 * @param assetcardVOs
	 * @param period
	 * @throws BusinessException
	 */
	public void processDepreciations(CorpVO corpvo, DZFDate loginDate, AssetcardVO[] assetcardVOs, String period, DZFBoolean isclear, String coperatorid) throws DZFWarpException;//ASSETCARDVO[] 4//modify by zhangj 清理的计提折旧出现问题
	/**
	 * 固定资产结账检查
	 * @param gdzcjzVOs
	 * @throws BusinessException
	 */
	public GdzcjzVO[] saveCheckAssetCards(GdzcjzVO[] gdzcjzVOs, CorpVO corpvo) throws DZFWarpException;
	/**
	 * 固定资产结账
	 * @param gdzcjzVOs
	 * @throws BusinessException
	 */
	//public GdzcjzVO[] settleAssetCards(GdzcjzVO[] gdzcjzVOs) throws DZFWarpException;
	/**
	 * 固定资产反结账
	 * @param gdzcjzVOs
	 * @throws BusinessException
	 */
	//public GdzcjzVO[] unSettleAssetCards(GdzcjzVO[] gdzcjzVOs) throws DZFWarpException;

	/**
	 * 固定资产结账\反结账
	 * @param gdzcjzVOs
	 * @throws BusinessException
	 */
	public GdzcjzVO[] updateSettleAssetCards(GdzcjzVO[] gdzcjzVOs, boolean issettle) throws DZFWarpException;

	/**
	 * 固定资产期初同步
	 * @param qcyevos
	 * @return
	 */
	public QcYeVO[] doAssetSync(QcYeVO[] qcyevos)  throws DZFWarpException;
	
	/**
	 * 查询资产与总账对账表
	 * @param pk_corp
	 * @param period
	 * @return
	 * @throws BusinessException
	 */
//	public ZcdzVO[] queryAssetCheckVOs(String pk_corp, String period) throws DZFWarpException;//ZCDZVO[] 5
	
	/**
	 * 查询资产类别
	 * @return
	 * @throws BusinessException
	 */
//	public HashMap<String, BdAssetCategoryVO> queryAssetCategoryMap() throws DZFWarpException;
}
