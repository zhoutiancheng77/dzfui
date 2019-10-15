package com.dzf.zxkj.platform.service.jzcl;


import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;

public interface ISMcbftService {
	
	/**
	 * 注意:headVO 一定要带有子表数据。这里分摊不进行子表数据查询
	 */
	public void saveCBFt(TzpzHVO headVO, CorpVO corpVo) throws DZFWarpException;

}
