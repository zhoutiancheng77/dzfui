package com.dzf.zxkj.platform.service.glic;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.platform.model.glic.IcDetailVO;
import com.dzf.zxkj.platform.model.jzcl.TempInvtoryVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;

import java.util.List;
import java.util.Map;


/**
 * 
 * 库存成本表
 */
public interface IKcCbb {

	public Map<String, IcDetailVO> queryDetail(QueryParamVO paramvo, CorpVO corpvo) throws DZFWarpException;
	
	public List<TempInvtoryVO> queryZgVOs(CorpVO corpvo, String userid, DZFDate doped) throws DZFWarpException;
	
	public void saveZg(TempInvtoryVO[] bodyvos, String pk_corp, String userid) throws DZFWarpException;
	
	public TzpzHVO queryJzPz(String pk_corp, String period) throws DZFWarpException;
}
