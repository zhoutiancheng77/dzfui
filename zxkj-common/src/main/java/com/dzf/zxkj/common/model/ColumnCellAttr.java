package com.dzf.zxkj.common.model;

/**
 * 
 * 表头每个字段的属性
 * 
 * @author zhangj
 * 
 */
public class ColumnCellAttr extends SuperVO {

	private String columname;
	private String key;
	private Integer colspan;
	private Integer rowspan;
	private String column;
	private int width;

	public ColumnCellAttr(String columname, String key, Integer colspan, Integer rowspan, String column, int width) {
		super();
		this.columname = columname;
		this.key = key;
		this.colspan = colspan;
		this.rowspan = rowspan;
		this.column = column;
		this.width = width;
	}

	public ColumnCellAttr() {
	}
	public ColumnCellAttr(String columnname) {
		this.columname = columnname;
	}

	public ColumnCellAttr(String columname, String column, int width) {
		this.columname = columname;
		this.column = column;
		this.width = width;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getColumname() {
		return columname;
	}

	public void setColumname(String columname) {
		this.columname = columname;
	}

	public Integer getColspan() {
		return colspan;
	}

	public void setColspan(Integer colspan) {
		this.colspan = colspan;
	}

	public Integer getRowspan() {
		return rowspan;
	}

	public void setRowspan(Integer rowspan) {
		this.rowspan = rowspan;
	}

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	@Override
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
