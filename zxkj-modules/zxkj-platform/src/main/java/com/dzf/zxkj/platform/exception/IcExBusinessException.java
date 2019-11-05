package com.dzf.zxkj.platform.exception;


import com.dzf.zxkj.common.exception.BusinessException;
import com.dzf.zxkj.platform.model.icset.IntradeoutVO;

import java.util.List;

public class IcExBusinessException extends BusinessException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1382703934541254384L;

	public IcExBusinessException(String s) {
		super(s);
	}

	private List<IntradeoutVO> errList;

	public List<IntradeoutVO> getErrList() {
		return errList;
	}

	public void setErrList(List<IntradeoutVO> errList) {
		this.errList = errList;
	}

}
