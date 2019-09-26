package com.dzf.zxkj.platform.model.st;

import com.dzf.zxkj.common.entity.Json;

public class GridJson extends Json {
	
	private static final long serialVersionUID = 1L;
	//不可编辑项
	private String[] notEditCellNames;

	public String[] getNotEditCellNames() {
		return notEditCellNames;
	}
	public void setNotEditCellNames(String[] notEditCellNames) {
		this.notEditCellNames = notEditCellNames;
	}


}
