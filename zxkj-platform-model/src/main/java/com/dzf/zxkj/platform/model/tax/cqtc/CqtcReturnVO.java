package com.dzf.zxkj.platform.model.tax.cqtc;

import com.dzf.zxkj.common.model.SuperVO;

/**
 * 
 * 
 *
 */
@SuppressWarnings({ "rawtypes", "serial" })
public class CqtcReturnVO extends SuperVO {

	private String openId;

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return null;
	}

}
