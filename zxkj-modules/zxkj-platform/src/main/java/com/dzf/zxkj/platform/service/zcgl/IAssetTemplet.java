package com.dzf.zxkj.platform.service.zcgl;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.sys.BdAssetCategoryVO;
import com.dzf.zxkj.platform.model.zcgl.AssetDepTemplate;

import java.util.HashMap;

public interface IAssetTemplet {

	/**
	 * 取固定资产折旧模板
	 * @param pk_corp
	 * @param categoryVO
	 * @return
	 * @throws BusinessException
	 */
	public AssetDepTemplate[] getAssetDepTemplate(String pk_corp, Integer tempkind, BdAssetCategoryVO categoryVO, HashMap<String, AssetDepTemplate[]> depTemplateMap) throws DZFWarpException;

}
