package com.dzf.zxkj.platform.model.yscs;

import com.thoughtworks.xstream.annotations.XStreamAlias;


/***
 * <body>
	<return_data>
	json串（加密算法-PEB-BASE64）
	<return_data>
<body>
 * @author asoka
 *
 */
@XStreamAlias("body")
public class ResponseBodyVO {
	
	private String return_data;

	public String getReturn_data() {
		return return_data;
	}

	public void setReturn_data(String return_data) {
		this.return_data = return_data;
	}

	@Override
	public String toString() {
		return "ResponseBodyVO [return_data=" + return_data + "]";
	}
	
	

}
