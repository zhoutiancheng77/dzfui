package com.dzf.zxkj.report.service.cwzb;


import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.query.KmReoprtQueryParamVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.platform.model.report.FzKmmxVO;

import java.util.List;
import java.util.Map;

/**
 * 辅助项目明细账
 * @author zhangj
 *
 */
public interface IFzKmmxReport {
	
	
	/**
	 * 查询辅助明细vo
	 * @param paramavo
	 * @param bshowcolumn 是否显示单个项目
	 * @return
	 * @throws BusinessException
	 */
	public Object[] getFzkmmxVos(KmReoprtQueryParamVO paramavo, DZFBoolean bshowcolumn) throws DZFWarpException;
	
	
	/**
	 * 查询所有当前类别+科目的所有辅助项目
	 * @param paramavo
	 * @return
	 * @throws DZFWarpException
	 */
	public Map<String,List<FzKmmxVO>> getAllFzKmmxVos(KmReoprtQueryParamVO paramavo) throws DZFWarpException;
	

}
