package com.dzf.zxkj.excel.param;

import org.apache.poi.ss.usermodel.HorizontalAlignment;

public class TitleColumnExcelport {

	private int rowspan;// 合并行

	private String name;// 名字
	
	private HorizontalAlignment alignment;// excel的位置(居左，居右，居中)

	public TitleColumnExcelport(int rowspan, String name, HorizontalAlignment alignment) {
		super();
		this.rowspan = rowspan;
		this.name = name;
		this.alignment = alignment;
	}

	public HorizontalAlignment getAlignment() {
		return alignment;
	}

	public void setAlignment(HorizontalAlignment alignment) {
		this.alignment = alignment;
	}

	public int getRowspan() {
		return rowspan;
	}

	public void setRowspan(int rowspan) {
		this.rowspan = rowspan;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
