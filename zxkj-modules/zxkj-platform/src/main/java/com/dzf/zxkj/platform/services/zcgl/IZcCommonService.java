package com.dzf.zxkj.platform.services.zcgl;


import com.dzf.zxkj.common.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.sys.BdAssetCategoryVO;
import com.dzf.zxkj.platform.model.sys.BdTradeAssetCheckVO;

import java.util.Map;

public interface IZcCommonService {
    BdTradeAssetCheckVO[] getAssetcheckVOs(String corpType, String pk_corp) throws DZFWarpException;
//    BdTradeAssetCheckVO[] getAssetcheckVOsHY(String corpType) throws DZFWarpException;
    Map<String, BdAssetCategoryVO> queryAssetCategoryMap() throws DZFWarpException;
}
