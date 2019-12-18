package com.dzf.zxkj.platform.model.tax.workbench;

import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.model.SuperVO;

/**
 * 纳税工作台调用参数主VO
 * @author wangzhn
 *
 */
public class TaxRptCalCellVO extends SuperVO {
	
	private DZFBoolean success;//成功标识
	
	private String msg;//消息
	
	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public DZFBoolean getSuccess() {
		return success;
	}

	public void setSuccess(DZFBoolean success) {
		this.success = success;
	}

	public String getPKFieldName() {
		return null;
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return null;
	}

}
