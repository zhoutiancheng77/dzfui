package com.dzf.zxkj.platform.model.tax;

public class TaxReportQcSubVO {
	private Integer periodtype;
	private String qcdata;
	private Boolean createReportData;

	public Integer getPeriodtype() {
		return periodtype;
	}

	public void setPeriodtype(Integer periodtype) {
		this.periodtype = periodtype;
	}

	public String getQcdata() {
		return qcdata;
	}

	public void setQcdata(String qcdata) {
		this.qcdata = qcdata;
	}

	public Boolean getCreateReportData() {
		return createReportData;
	}

	public void setCreateReportData(Boolean createReportData) {
		this.createReportData = createReportData;
	}
}
