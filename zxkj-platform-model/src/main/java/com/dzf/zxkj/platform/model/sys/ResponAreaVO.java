package com.dzf.zxkj.platform.model.sys;

import com.dzf.zxkj.common.model.SuperVO;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 省市区数据VO
 * @author 宗岩
 *
 */

public class ResponAreaVO extends SuperVO {
	
	@JsonProperty("")
	private Integer region_id;	
	
	@JsonProperty("")
	private String region_name;
	
	private String parenter_id;

	public Integer getRegion_id() {
		return region_id;
	}

	public void setRegion_id(Integer region_id) {
		this.region_id = region_id;
	}

	public String getRegion_name() {
		return region_name;
	}

	public void setRegion_name(String region_name) {
		this.region_name = region_name;
	}

	public String getParenter_id() {
		return parenter_id;
	}

	public void setParenter_id(String parenter_id) {
		this.parenter_id = parenter_id;
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
