package com.dzf.zxkj.platform.services.am.zcgl;

import com.dzf.zxkj.common.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.am.zcgl.AssetDepTemplate;
import com.dzf.zxkj.platform.model.am.zcgl.BdAssetCategoryVO;

import java.util.HashMap;

/**
 * @Auther: dandelion
 * @Date: 2019-09-05
 * @Description:
 */
public interface IAssetDepTemplateService {
    AssetDepTemplate[] getAssetDepTemplate(String pk_corp, Integer tempkind, BdAssetCategoryVO categoryVO, HashMap<String, AssetDepTemplate[]> depTemplateMap) throws DZFWarpException;
}
