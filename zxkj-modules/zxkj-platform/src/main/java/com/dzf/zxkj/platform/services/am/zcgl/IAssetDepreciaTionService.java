package com.dzf.zxkj.platform.services.am.zcgl;

import com.dzf.zxkj.common.exception.BusinessException;
import com.dzf.zxkj.common.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.am.zcgl.AssetDepreciaTionVO;
import com.dzf.zxkj.platform.model.am.zcgl.AssetCardVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;

/**
 * @Auther: dandelion
 * @Date: 2019-09-05
 * @Description:
 */
public interface IAssetDepreciaTionService {
    void updateDepToGLState(String pk_assetdep, boolean istogl, String pk_voucher) throws DZFWarpException;

    void deleteAssetDepreciation(AssetDepreciaTionVO assetdepVO) throws DZFWarpException;

    void clearProcessDep(CorpVO corpvo, String loginDate, AssetCardVO[] assetCardVOS) throws BusinessException;

    void processAssetDepToGL(CorpVO corpvo, AssetDepreciaTionVO assetdepVO, AssetCardVO assetcardVO) throws BusinessException;
}
