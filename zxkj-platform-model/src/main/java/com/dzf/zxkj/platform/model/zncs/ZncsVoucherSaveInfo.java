package com.dzf.zxkj.platform.model.zncs;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.dzf.zxkj.common.model.SuperVO;

public class ZncsVoucherSaveInfo extends SuperVO {
	private Integer suss;
	private Integer fal;
	private List<String> message;//错误提示
	private List<JSONObject> tzpzlist;
	private String syjzcode;
	
	
	
	
	public String getSyjzcode() {
		return syjzcode;
	}

	public void setSyjzcode(String syjzcode) {
		this.syjzcode = syjzcode;
	}

	public List<JSONObject> getTzpzlist() {
		return tzpzlist;
	}

	public void setTzpzlist(List<JSONObject> tzpzlist) {
		this.tzpzlist = tzpzlist;
	}

	public Integer getSuss() {
		return suss;
	}

	public void setSuss(Integer suss) {
		this.suss = suss;
	}

	public Integer getFal() {
		return fal;
	}

	public void setFal(Integer fal) {
		this.fal = fal;
	}

	public List<String> getMessage() {
		return message;
	}

	public void setMessage(List<String> message) {
		this.message = message;
	}

	@Override
	public String getPKFieldName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getParentPKFieldName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
