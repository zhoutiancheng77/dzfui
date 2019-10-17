package com.dzf.zxkj.report.service.cwzb;


import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.query.KmReoprtQueryParamVO;
import com.dzf.zxkj.platform.model.report.FzYebVO;

import java.util.List;

/**
 * 辅助核算余额表
 * @author llh
 *
 */
public interface IFzhsYebReport {

	public List<FzYebVO> getFzYebVOs(KmReoprtQueryParamVO paramVo) throws DZFWarpException;

}
