package com.dzf.zxkj.platform.service.zncs;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.zncs.BlackListVO;

import java.util.List;

/**
 * 黑名单
 * @author mfz
 *
 */
public interface IBlackList {

	public List<BlackListVO> queryBlackListVOs(String pk_corp)throws DZFWarpException;
	
	public List<BlackListVO> saveBlackListVO(String pk_corp, String blackListnames)throws DZFWarpException;
	
	public void deleteBlackListVO(String pk_corp, String pk_blacklist)throws DZFWarpException;
	
}
