package com.dzf.zxkj.app.model.resp.bean;

import com.dzf.zxkj.common.model.SuperVO;

import java.util.List;

public class MoreServiceHVo extends SuperVO {

	private String mc;

	private List<MoreServiceBVo> blist;

	public String getMc() {
		return mc;
	}

	public void setMc(String mc) {
		this.mc = mc;
	}

	public List<MoreServiceBVo> getBlist() {
		return blist;
	}

	public void setBlist(List<MoreServiceBVo> blist) {
		this.blist = blist;
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
