package com.dzf.zxkj.platform.model.report;


import com.dzf.zxkj.common.model.SuperVO;

/**
 * 权重值
 * @author zhangj
 *
 */
public class ExpendpTionVO extends SuperVO {
	
	private String name;
	private String value;
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
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
