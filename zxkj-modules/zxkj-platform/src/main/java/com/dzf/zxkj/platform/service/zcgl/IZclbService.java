package com.dzf.zxkj.platform.service.zcgl;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.sys.BdAssetCategoryVO;

/**
 *资产类别
 */
public interface IZclbService {
	BdAssetCategoryVO queryByid(String pid)throws DZFWarpException;
	BdAssetCategoryVO[] queryAssetCategory(String pk_corp) throws DZFWarpException;
	BdAssetCategoryVO[] queryAssetCategoryRef(String pk_corp) throws DZFWarpException;
	void save(BdAssetCategoryVO vo) throws DZFWarpException;
	void delete(BdAssetCategoryVO vo, String pk_corp) throws DZFWarpException;
	String existCheck(BdAssetCategoryVO vo, String pk_corp) throws DZFWarpException;
	BdAssetCategoryVO queryAssetCategoryByPrimaryKey(BdAssetCategoryVO vo) throws DZFWarpException;
	
	
	BdAssetCategoryVO[] queryParent(String pk_corp, Integer AccountSchema) throws DZFWarpException;

}
