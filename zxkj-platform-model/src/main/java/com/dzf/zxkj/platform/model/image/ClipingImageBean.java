package com.dzf.zxkj.platform.model.image;

import java.io.Serializable;

public class ClipingImageBean implements Serializable{
	private static final long serialVersionUID = 1085075766967549037L;
	private int totalCount;
	private ImageGroupVO[] clipingVOs;
	
	public int getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	public ImageGroupVO[] getClipingVOs() {
		return clipingVOs;
	}
	public void setClipingVOs(ImageGroupVO[] clipingVOs) {
		this.clipingVOs = clipingVOs;
	}
}
