package com.dzf.zxkj.platform.model.report;


/**
 * 科目自定义column
 * @author zhangj
 *
 */
public class KmReportDatagridColumn extends DatagridColumn {
	
	private String formatter;
	
	private Integer rowspan;
	
	private Integer colspan;
	
	private String halign;
	

	public String getHalign() {
		return halign;
	}

	public void setHalign(String halign) {
		this.halign = halign;
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

	public String getFormatter() {
		return formatter;
	}

	public void setFormatter(String formatter) {
		this.formatter = formatter;
	}
	
	

}
