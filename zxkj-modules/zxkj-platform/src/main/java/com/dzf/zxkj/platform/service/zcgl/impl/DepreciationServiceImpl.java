package com.dzf.zxkj.platform.service.zcgl.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.platform.model.zcgl.DepreciationVO;
import com.dzf.zxkj.platform.service.zcgl.IDepreciationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("am_rep_checkvoucher")
public class DepreciationServiceImpl implements IDepreciationService {

	private SingleObjectBO singleObjectBO;

	public SingleObjectBO getSingleObjectBO() {
		return singleObjectBO;
	}

	@Autowired
	public void setSingleObjectBO(SingleObjectBO singleObjectBO) {
		this.singleObjectBO = singleObjectBO;
	}
	
	@Override
	public DepreciationVO[] query(String corp, String voucher) throws DZFWarpException {
		String condition = " pk_voucher = ? and pk_corp = ? and nvl(dr,0) = 0 ";
		SQLParameter sp = new SQLParameter();
		sp.addParam(voucher);
		sp.addParam(corp);
		DepreciationVO[] results = (DepreciationVO[]) singleObjectBO.queryByCondition(DepreciationVO.class, condition, sp);
		return results;
	}
	
}
