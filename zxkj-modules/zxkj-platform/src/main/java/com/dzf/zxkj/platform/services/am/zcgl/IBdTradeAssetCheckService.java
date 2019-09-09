package com.dzf.zxkj.platform.services.am.zcgl;

import com.dzf.zxkj.common.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.am.zcgl.BdTradeAssetCheckVO;

import java.util.List;

/**
 * @Auther: dandelion
 * @Date: 2019-09-05
 * @Description:
 */
public interface IBdTradeAssetCheckService {
    List<BdTradeAssetCheckVO> queryDefaultFromZclb(String pk_corp, String pk_zclb) throws DZFWarpException;

    BdTradeAssetCheckVO[] getAssetcheckVOs(String corpType, String pk_corp) throws DZFWarpException;
}
