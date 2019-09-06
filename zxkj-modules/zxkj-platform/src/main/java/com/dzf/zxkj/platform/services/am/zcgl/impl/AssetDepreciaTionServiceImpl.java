package com.dzf.zxkj.platform.services.am.zcgl.impl;

import com.dzf.zxkj.common.exception.BusinessException;
import com.dzf.zxkj.common.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.am.zcgl.AssetDepreciaTionVO;
import com.dzf.zxkj.platform.model.am.zcgl.AssetCardVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.services.am.zcgl.IAssetDepreciaTionService;

/**
 * @Auther: dandelion
 * @Date: 2019-09-05
 * @Description:
 */
public class AssetDepreciaTionServiceImpl implements IAssetDepreciaTionService {
    @Override
    public void updateDepToGLState(String pk_assetdep, boolean istogl, String pk_voucher) throws DZFWarpException {

    }

    @Override
    public void deleteAssetDepreciation(AssetDepreciaTionVO assetdepVO) throws DZFWarpException {

    }

    @Override
    public void clearProcessDep(CorpVO corpvo, String loginDate, AssetCardVO[] assetCardVOS) throws BusinessException {

    }

    @Override
    public void processAssetDepToGL(CorpVO corpvo, AssetDepreciaTionVO assetdepVO, AssetCardVO assetcardVO) throws BusinessException {

    }
}
