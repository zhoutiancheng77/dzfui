package com.dzf.zxkj.platform.model.sys;

import java.io.Serializable;
import java.util.Map;

/**
 * 消息推送Bean
 * */
public class JPMessageBean implements Serializable{
	
	private String[] corptags;//消息分组tag 
	private String[] userids;//推送目标id 
	private String message;//消息概要，不可为空
	private Map<String, String> extras;//键值对扩展消息体 
	private String sourcesys;//系统类型
	
	public String[] getCorptags() {
		return corptags;
	}
	public void setCorptag(String[] corptags) {
		this.corptags = corptags;
	}
	public String[] getUserids() {
		return userids;
	}
	public void setUserids(String[] userids) {
		this.userids = userids;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	public Map<String, String> getExtras() {
		return extras;
	}
	public void setExtras(Map<String, String> extras) {
		this.extras = extras;
	}
	public String getSourcesys() {
		return sourcesys;
	}
	public void setSourcesys(String sourcesys) {
		this.sourcesys = sourcesys;
	}
	
	
	
}
