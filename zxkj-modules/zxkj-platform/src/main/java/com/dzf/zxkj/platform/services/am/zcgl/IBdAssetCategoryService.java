package com.dzf.zxkj.platform.services.am.zcgl;

import com.dzf.zxkj.common.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.am.zcgl.BdAssetCategoryVO;

import java.util.Map;

/**
 * @Auther: dandelion
 * @Date: 2019-09-05
 * @Description:
 */
public interface IBdAssetCategoryService {
    Map<String, BdAssetCategoryVO> queryAssetCategoryMap() throws DZFWarpException;
}
