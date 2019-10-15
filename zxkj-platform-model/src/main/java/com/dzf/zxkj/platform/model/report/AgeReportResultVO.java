package com.dzf.zxkj.platform.model.report;

import java.util.List;

/**
 * 往来账龄查询条件
 * 
 * @author liubj
 *
 */
public class AgeReportResultVO {
	private Object result;
	private List<String> periods;
	private List<String> period_names;
	public Object getResult() {
		return result;
	}
	public void setResult(Object result) {
		this.result = result;
	}
	public List<String> getPeriods() {
		return periods;
	}
	public void setPeriods(List<String> periods) {
		this.periods = periods;
	}
	public List<String> getPeriod_names() {
		return period_names;
	}
	public void setPeriod_names(List<String> period_names) {
		this.period_names = period_names;
	}
	
}
