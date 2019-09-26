package com.dzf.zxkj.platform.model.jzcl;

import java.util.List;

public class TempDataTransFer {
	
	private boolean zgdataisave = false;
	
	private List<TempInvtoryVO> zglist;

	public boolean isZgdataisave() {
		return zgdataisave;
	}

	public void setZgdataisave(boolean zgdataisave) {
		this.zgdataisave = zgdataisave;
	}

	public List<TempInvtoryVO> getZglist() {
		return zglist;
	}

	public void setZglist(List<TempInvtoryVO> zglist) {
		this.zglist = zglist;
	}
}
