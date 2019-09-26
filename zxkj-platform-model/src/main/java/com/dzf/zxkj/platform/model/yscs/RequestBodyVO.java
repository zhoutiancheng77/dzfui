package com.dzf.zxkj.platform.model.yscs;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/****
 * 	<request_data>
		json串（加密算法-PBE-BASE64）
	</request_data>
 * @author asoka
 *
 */
@XStreamAlias("body")
public class RequestBodyVO {
	
	private String request_data;

	public String getRequest_data() {
		return request_data;
	}

	public void setRequest_data(String request_data) {
		this.request_data = request_data;
	}

	@Override
	public String toString() {
		return "RequestBodyVO [request_data=" + request_data + "]";
	}
	
}
