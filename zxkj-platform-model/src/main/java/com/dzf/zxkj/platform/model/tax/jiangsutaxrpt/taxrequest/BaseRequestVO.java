package com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest;

public class BaseRequestVO {
	// 交易流水号（32位）
	private String dealid;
	// 服务id
	private String clientid;
	// 服务id
	private String serviceid;
	// 回调服务id
	private String asynserviceid;
	// 报文类型 Json
	private String bwlx;
	// 业务报文
	private RequestBodyVO body;

	public String getDealid() {
		return dealid;
	}

	public void setDealid(String dealid) {
		this.dealid = dealid;
	}

	public String getClientid() {
		return clientid;
	}

	public void setClientid(String clientid) {
		this.clientid = clientid;
	}

	public String getServiceid() {
		return serviceid;
	}

	public void setServiceid(String serviceid) {
		this.serviceid = serviceid;
	}

	public String getAsynserviceid() {
		return asynserviceid;
	}

	public void setAsynserviceid(String asynserviceid) {
		this.asynserviceid = asynserviceid;
	}

	public String getBwlx() {
		return bwlx;
	}

	public void setBwlx(String bwlx) {
		this.bwlx = bwlx;
	}

	public RequestBodyVO getBody() {
		return body;
	}

	public void setBody(RequestBodyVO body) {
		this.body = body;
	}
}
