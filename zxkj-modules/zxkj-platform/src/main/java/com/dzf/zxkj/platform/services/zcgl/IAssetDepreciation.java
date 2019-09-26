package com.dzf.zxkj.platform.services.zcgl;

import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.zcgl.AssetDepreciaTionVO;
import com.dzf.zxkj.platform.model.zcgl.AssetcardVO;

/**
 * 资产折旧
 * @author dzf
 *
 */
public interface IAssetDepreciation {

	
	
	/**
	 * 模板已经更改(清理使用)
	 * @param corpvo
	 * @param loginDate
	 * @param assetcardVOs
	 * @throws BusinessException
	 */
	public void clearProcessDep(CorpVO corpvo, String loginDate, AssetcardVO[] assetcardVOs) throws BusinessException;

	/**
	 * 资产折旧明细生产凭证
	 * @param assetdepVO
	 * @param assetcardVO
	 * @throws BusinessException
	 */
	public void processAssetDepToGL(CorpVO corpvo, AssetDepreciaTionVO assetdepVO, AssetcardVO assetcardVO) throws BusinessException ;
}
