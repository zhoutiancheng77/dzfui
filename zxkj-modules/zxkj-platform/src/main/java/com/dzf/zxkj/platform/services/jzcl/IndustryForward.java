package com.dzf.zxkj.platform.services.jzcl;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.model.jzcl.CostForwardInfo;
import com.dzf.zxkj.platform.model.jzcl.CostForwardVO;
import com.dzf.zxkj.platform.model.jzcl.QmclVO;

import java.util.List;

/**
 * 工业结转
 *
 */
public interface IndustryForward {
	/**
	 * 第一步
	 */
	public List<CostForwardVO> queryIndustCFVO(QmclVO vo, boolean b) throws DZFWarpException;

	/**
	 * 第二步
	 */
	public CostForwardVO createsecZJVO(String pk_corp, CostForwardVO oldv, boolean isjf, String zzfycode)throws DZFWarpException;
	/**
	 * 第三步
	 */
	public CostForwardVO createthirdZJVO(String pk_corp, CostForwardVO oldv, boolean isjf, String zzfycode)throws DZFWarpException;

	/**
	 * list.get(0)为基本生产成本材料、人工、制造费用期初
	 * list.get(1....max)为存货信息
	 * 第四步
	 */
	public List<CostForwardInfo> queryIndustQCInvtory(QmclVO vo) throws DZFWarpException;


	/**
	 * 第五步
	 */
	public CostForwardVO createfiveZJVO(String pk_corp, boolean isjf, String accode, DZFDouble mny, DZFDouble nnum, String pk_invtory, String invname)throws DZFWarpException;
	/**
	 * 判断
	 * @return
	 * @throws BusinessException
	 */
	public boolean is2007(String pk_corp) throws DZFWarpException;

	public List<CostForwardInfo> queryIndustQCInvtoryNOIC(QmclVO headvo, String jztype);
	
	
	
	
}
