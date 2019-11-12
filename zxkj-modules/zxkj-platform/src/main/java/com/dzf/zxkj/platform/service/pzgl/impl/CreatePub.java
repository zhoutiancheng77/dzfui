package com.dzf.zxkj.platform.service.pzgl.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;

public class CreatePub {

	private SingleObjectBO singleobjectbo;
	
	
	public SingleObjectBO getSingleobjectbo() {
		return singleobjectbo;
	}


//	public void setSingleobjectbo(SingleObjectBO singleobjectbo) {
//		this.singleobjectbo = singleobjectbo;
//	}


	public CreatePub(SingleObjectBO singleobjectbo){
		this.singleobjectbo=singleobjectbo;
	}
	
	public YntCpaccountVO queryAccountVO(String pk_account) throws BusinessException {
		YntCpaccountVO accountvo = (YntCpaccountVO)getSingleobjectbo().queryByPrimaryKey(YntCpaccountVO.class, pk_account);
		return accountvo;
	}
	
}
