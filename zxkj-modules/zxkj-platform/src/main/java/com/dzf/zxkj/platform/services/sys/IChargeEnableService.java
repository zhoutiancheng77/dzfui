package com.dzf.zxkj.platform.services.sys;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFDate;

public interface IChargeEnableService {
	
	/*
	 * 按收费类型查询收费启动日期
	 * chargeType:
	 * 		IDzfServiceConst.ChargeType_01   //一键报税
	 * 		IDzfServiceConst.ChargeType_02   //智能凭证
	 * 		IDzfServiceConst.ChargeType_03   //风控体检
	 * 		IDzfServiceConst.ChargeType_04   //标准产品
	 * 返回null说明还没启用，不收费
	 */
	public DZFDate queryByType(String chargeType) throws DZFWarpException;
}
