package com.dzf.zxkj.app.model.sys;

public class AppQunGroup implements java.io.Serializable {

	private static final long serialVersionUID = -3213005461064875180L;

	private String groupname;// 群/组名称
	
	private String id;// 群/组id
	
	private AppMemberVO[] list;//群/组成员

	public String getGroupname() {
		return groupname;
	}

	public void setGroupname(String groupname) {
		this.groupname = groupname;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public AppMemberVO[] getList() {
		return list;
	}

	public void setList(AppMemberVO[] list) {
		this.list = list;
	}
	
}
