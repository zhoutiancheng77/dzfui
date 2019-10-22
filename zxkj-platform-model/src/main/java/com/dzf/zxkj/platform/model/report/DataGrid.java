package com.dzf.zxkj.platform.model.report;

import com.dzf.zxkj.common.entity.Grid;

import java.util.ArrayList;
import java.util.List;

public class DataGrid extends Grid {
	
	private List columns = new ArrayList();
	
	private String rowdata;

	public List getColumns() {
		return columns;
	}

	public void setColumns(List columns) {
		this.columns = columns;
	}

	public String getRowdata() {
		return rowdata;
	}

	public void setRowdata(String rowdata) {
		this.rowdata = rowdata;
	};

	
}
