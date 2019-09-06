package com.dzf.zxkj.platform.services.am.zcgl.impl;

import com.dzf.zxkj.common.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.am.zcgl.BdTradeAssetCheckVO;
import com.dzf.zxkj.platform.services.am.zcgl.IBdTradeAssetCheckService;

import java.util.List;

/**
 * @Auther: dandelion
 * @Date: 2019-09-05
 * @Description:
 */
public class BdTradeAssetCheckServiceImpl implements IBdTradeAssetCheckService {
    @Override
    public List<BdTradeAssetCheckVO> queryDefaultFromZclb(String pk_corp, String pk_zclb) throws DZFWarpException {
        return null;
    }

    @Override
    public BdTradeAssetCheckVO[] getAssetcheckVOs(String corpType, String pk_corp) throws DZFWarpException {
        return new BdTradeAssetCheckVO[0];
    }
}
