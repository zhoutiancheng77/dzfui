package com.dzf.zxkj.platform.services.am.zcgl;

import com.dzf.zxkj.common.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.am.zcgl.AssetCardDisplayColumnVO;

import java.util.List;

/**
 * @Auther: dandelion
 * @Date: 2019-09-05
 * @Description:
 */
public interface IAssetCardDisplayColumnService {
    List<AssetCardDisplayColumnVO> qryDisplayColumns(String pk_corp) throws DZFWarpException;

    void saveDisplayColumn(AssetCardDisplayColumnVO displayvo, String pk_corp) throws DZFWarpException;
}
