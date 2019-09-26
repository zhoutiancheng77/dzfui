package com.dzf.zxkj.platform.model.zcgl;

import com.dzf.zxkj.common.lang.DZFDate;

/**
 * 资产管理，报表查询条件VO
 * @author liangyi
 *
 */
public class AssetQueryCdtionVO {

	//日期
	private DZFDate start_date;
	private DZFDate end_date;
	//资产卡片
	private String asscd_id;
	
	private String ascode;
	
	public DZFDate getStart_date() {
		return start_date;
	}
	public void setStart_date(DZFDate start_date) {
		this.start_date = start_date;
	}
	public DZFDate getEnd_date() {
		return end_date;
	}
	public void setEnd_date(DZFDate end_date) {
		this.end_date = end_date;
	}
	public String getAsscd_id() {
		return asscd_id;
	}
	public void setAsscd_id(String asscd_id) {
		this.asscd_id = asscd_id;
	}
	public String getAscode() {
		return ascode;
	}
	public void setAscode(String ascode) {
		this.ascode = ascode;
	}
	
	
}
