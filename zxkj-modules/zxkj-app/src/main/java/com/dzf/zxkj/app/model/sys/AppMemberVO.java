package com.dzf.zxkj.app.model.sys;

import java.io.Serializable;

/**
 * 接手人信息
 * @author zy
 *
 */
public class AppMemberVO implements Serializable{

	private static final long serialVersionUID = 4077018980376224832L;

	private String id;
	
	private String username;
	
	private String avatar;
	
	private String pk_corp;//所属公司主键
	
	private String corpname;//所属公司名称

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getCorpname() {
		return corpname;
	}

	public void setCorpname(String corpname) {
		this.corpname = corpname;
	}
	
}
