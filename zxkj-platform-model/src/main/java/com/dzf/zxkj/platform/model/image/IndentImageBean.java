package com.dzf.zxkj.platform.model.image;

import java.io.Serializable;

public class IndentImageBean implements Serializable{
	private static final long serialVersionUID = 1085075766967549037L;
	private int totalCount;
	private ImageMetaVO[] metaVOs;
	
	public int getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	public ImageMetaVO[] getMetaVOs() {
		return metaVOs;
	}
	public void setMetaVOs(ImageMetaVO[] metaVOs) {
		this.metaVOs = metaVOs;
	}

}
