package com.dzf.zxkj.platform.service.zncs;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.zncs.BaseCategoryVO;

import java.util.List;


public interface IBaseCategoryService {

	
	public List<BaseCategoryVO> queryBaseCategory(String pk_corp)throws DZFWarpException;
	
	public List<BaseCategoryVO> queryBaseCategoryByGdzcNotNull(String pk_accountschema)throws DZFWarpException;
	
	public List<BaseCategoryVO> queryBaseCategoryByGdzcIsNull()throws DZFWarpException;
		
	public BaseCategoryVO queryVOById(String pk_basecategory)throws DZFWarpException;
	
	public String queryCategoryName(String pk_billcategory)throws DZFWarpException;
	
	
}
