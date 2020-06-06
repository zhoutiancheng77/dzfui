package com.dzf.admin.model.app.transfer.filetrans;

import lombok.Data;

@Data
public class RetAppQunGroup implements java.io.Serializable {

	private String groupname;// 群/组名称
	
	private String id;// 群/组id
	
	private RetAppMemberVO[] list;//群/组成员

}
