package com.dzf.zxkj.platform.exception;

import com.dzf.zxkj.common.exception.BusinessException;
import com.dzf.zxkj.platform.model.jzcl.TempInvtoryVO;

import java.util.List;
import java.util.Map;

public class ExBusinessException extends BusinessException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1382703934541254384L;

	public ExBusinessException(String s) {
		super(s);
	}

	private Map<String, List<TempInvtoryVO>> lmap;

	public Map<String, List<TempInvtoryVO>> getLmap() {
		return lmap;
	}

	public void setLmap(Map<String, List<TempInvtoryVO>> lmap) {
		this.lmap = lmap;
	}
}
