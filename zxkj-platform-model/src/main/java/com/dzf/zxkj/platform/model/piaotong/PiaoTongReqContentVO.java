package com.dzf.zxkj.platform.model.piaotong;

import java.io.Serializable;

public class PiaoTongReqContentVO implements Serializable{
	
	private String taxpayerNum;//纳税识别号
	private String enterpriseName;//企业名称
	private String startTime;//开始时间
	private String endTime;//结束时间
	private int pageSize;//每页条数
	private int pageIndex;//第几页
	public String getTaxpayerNum() {
		return taxpayerNum;
	}
	public String getEnterpriseName() {
		return enterpriseName;
	}
	public String getStartTime() {
		return startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public int getPageSize() {
		return pageSize;
	}
	public int getPageIndex() {
		return pageIndex;
	}
	public void setTaxpayerNum(String taxpayerNum) {
		this.taxpayerNum = taxpayerNum;
	}
	public void setEnterpriseName(String enterpriseName) {
		this.enterpriseName = enterpriseName;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}
	
}