package com.dzf.zxkj.platform.model.yscs;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/***
 * <head>
	<return_type>返回类型</return_type>
	<return_time>返回时间</return_time>
	<return_code>结果编码<return_code>
	<return_msg>返回信息<return_msg>
	<return_authkey>request_authkey<return_authkey>
</head>
 * @author asoka
 *
 */
@XStreamAlias("head")
public class ResponseHeadVO {
	
	private String return_type;
	
	private String return_time;
	
	private String return_code;
	
	private String return_msg;
	
	private String return_authkey;

	public String getReturn_type() {
		return return_type;
	}

	public void setReturn_type(String return_type) {
		this.return_type = return_type;
	}

	public String getReturn_time() {
		return return_time;
	}

	public void setReturn_time(String return_time) {
		this.return_time = return_time;
	}

	public String getReturn_code() {
		return return_code;
	}

	public void setReturn_code(String return_code) {
		this.return_code = return_code;
	}

	public String getReturn_msg() {
		return return_msg;
	}

	public void setReturn_msg(String return_msg) {
		this.return_msg = return_msg;
	}

	public String getReturn_authkey() {
		return return_authkey;
	}

	public void setReturn_authkey(String return_authkey) {
		this.return_authkey = return_authkey;
	}

	@Override
	public String toString() {
		return "ResponseHeadVO [return_type=" + return_type + ", return_time="
				+ return_time + ", return_code=" + return_code
				+ ", return_msg=" + return_msg + ", return_authkey="
				+ return_authkey + "]";
	}
	
	
	
	

}
