package com.dzf.zxkj.platform.service.zncs;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.zncs.CategoryKeywordVO;

import java.util.List;
import java.util.Map;

public interface ICategoryKeywordService {
	
	public Map<String, String> queryKeyWordMap()throws DZFWarpException;
	
	public List<CategoryKeywordVO> queryCateKey(String pk_corp)throws DZFWarpException;
	
	public List<CategoryKeywordVO> queryCateKeyByAll(Map<String, String> map, String pk_corp, String period)throws DZFWarpException;
}
