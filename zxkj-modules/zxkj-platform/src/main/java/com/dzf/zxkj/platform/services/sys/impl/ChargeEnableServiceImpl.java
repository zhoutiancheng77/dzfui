package com.dzf.zxkj.platform.services.sys.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.platform.model.sys.ChargeEnableVO;
import com.dzf.zxkj.platform.services.sys.IChargeEnableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("enableServ")
public class ChargeEnableServiceImpl implements IChargeEnableService {

	@Autowired
	private SingleObjectBO singleObjectBO;


	@Override
	public DZFDate queryByType(String chargeType) throws DZFWarpException {
		SQLParameter sp=new SQLParameter();
		sp.addParam(chargeType);
		ChargeEnableVO[] vo = (ChargeEnableVO[]) singleObjectBO.queryByCondition(ChargeEnableVO.class, "chargetype=? and nvl(dr,0)=0", sp);
		if (vo==null||vo.length ==0) {
			return null;
		}else{
			return vo[0].getCoperatordate();
		}
	}

}
