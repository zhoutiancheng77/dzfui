package com.dzf.zxkj.platform.model.piaotong;
public class CaiFangTongContentVO{
	private CaiFangTongHVO[] fpkj;
	
	private int size;
	
	private String maxkprq;

	public CaiFangTongHVO[] getFpkj() {
		return fpkj;
	}

	public void setFpkj(CaiFangTongHVO[] fpkj) {
		this.fpkj = fpkj;
	}

	public int getSize() {
		return size;
	}

	public String getMaxkprq() {
		return maxkprq;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public void setMaxkprq(String maxkprq) {
		this.maxkprq = maxkprq;
	}
	
}