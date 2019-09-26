package com.dzf.zxkj.platform.model.yscs;


import com.thoughtworks.xstream.annotations.XStreamAlias;

/****
 * 	<request_type>请求类型</request_type>
	<request_time>请求时间</request_time>
	<requerst_srcsys>来源系统</requerst_srcsys>
	<requerst_no>请求编号（yyMMdd-srcsys-no）<request_no>
	<request_authkey>
请求权限校验码（MD5(authkey+base64(请求时间+来源系统+请求编号))）
<request_authkey>
 * @author asoka
 *
 */

@XStreamAlias("head")
public class RequestHeadVO {
	
	@XStreamAlias("request_type")
	private String request_type;
	
	private String request_time;
	
	private String request_srcsys;
	
	private String request_no;
	
	private String request_authkey;

	public String getRequest_type() {
		return request_type;
	}

	public void setRequest_type(String request_type) {
		this.request_type = request_type;
	}

	public String getRequest_time() {
		return request_time;
	}

	public void setRequest_time(String request_time) {
		this.request_time = request_time;
	}


	public String getRequest_srcsys() {
		return request_srcsys;
	}

	public void setRequest_srcsys(String request_srcsys) {
		this.request_srcsys = request_srcsys;
	}

	public String getRequest_no() {
		return request_no;
	}

	public void setRequest_no(String request_no) {
		this.request_no = request_no;
	}

	public String getRequest_authkey() {
		return request_authkey;
	}

	public void setRequest_authkey(String request_authkey) {
		this.request_authkey = request_authkey;
	}

	@Override
	public String toString() {
		return "RequestHeadVO [request_type=" + request_type
				+ ", request_time=" + request_time + ", requerst_srcsys="
				+ request_srcsys + ", requerst_no=" + request_no
				+ ", request_authkey=" + request_authkey + "]";
	}
	
	
	
	

}
