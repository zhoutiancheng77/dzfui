package com.dzf.zxkj.platform.model.piaotong;

import java.io.Serializable;

public class PiaoTongResContentVO implements Serializable{
	
	private int pageSize;//纳税识别号
	private int start;//起始条数
	private int totalCount;//总条数
	
	private PiaoTongResHVO[] data;

	public int getPageSize() {
		return pageSize;
	}

	public int getStart() {
		return start;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public PiaoTongResHVO[] getData() {
		return data;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public void setData(PiaoTongResHVO[] data) {
		this.data = data;
	}
	
}