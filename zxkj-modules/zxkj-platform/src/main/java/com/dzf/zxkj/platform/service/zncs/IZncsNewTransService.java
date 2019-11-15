package com.dzf.zxkj.platform.service.zncs;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.zncs.BillCategoryVO;

import java.util.Map;

public interface IZncsNewTransService {
	/**
	 * 
	 * @param falseMap
	 * @param trueMap
	 * @param key
	 * @return
	 * @throws DZFWarpException
	 */
	public Map<String, BillCategoryVO> newInsertCategoryVOs(Map<String, BillCategoryVO> falseMap, Map<String, BillCategoryVO> trueMap, String key) throws DZFWarpException;
	
	public Map<String, BillCategoryVO> queryCategoryVOs_IsAccount(String pk_corp, String period, String flag)throws DZFWarpException;
	
}
