package com.dzf.zxkj.platform.service.zncs;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.zncs.ParaSetVO;

import java.util.List;

/**
 * 公司参数
 * @author mfz
 *
 */
public interface IParaSet {

	public List<ParaSetVO> queryParaSet(String pk_corp)throws DZFWarpException;
	
	public void saveParaSet(String pk_corp, String pType, String pValue)throws DZFWarpException;
	
}
