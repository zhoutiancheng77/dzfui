package com.dzf.zxkj.platform.model.yscs;


import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("xml")
public class ResponseVO {
	
	private ResponseHeadVO head;
	
	private ResponseBodyVO body;

	public ResponseHeadVO getHead() {
		return head;
	}

	public void setHead(ResponseHeadVO head) {
		this.head = head;
	}

	public ResponseBodyVO getBody() {
		return body;
	}

	public void setBody(ResponseBodyVO body) {
		this.body = body;
	}

	@Override
	public String toString() {
		return "ResponseVO [head=" + head + ", body=" + body + "]";
	}
	
	

}
