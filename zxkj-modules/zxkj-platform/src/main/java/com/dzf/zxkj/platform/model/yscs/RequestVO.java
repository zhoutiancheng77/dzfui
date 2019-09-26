package com.dzf.zxkj.platform.model.yscs;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("xml")
public class RequestVO {
	
	private RequestHeadVO head;
	
	private RequestBodyVO body;

	public RequestHeadVO getHead() {
		return head;
	}

	public void setHead(RequestHeadVO head) {
		this.head = head;
	}

	public RequestBodyVO getBody() {
		return body;
	}

	public void setBody(RequestBodyVO body) {
		this.body = body;
	}

	@Override
	public String toString() {
		return "RequestVO [head=" + head + ", body=" + body + "]";
	}
	
	

}
