package com.dzf.admin.model.app.transfer.filetrans;

import lombok.Data;

import java.io.Serializable;

/**
 * 接手人信息
 * @author zy
 *
 */
@Data
public class RetAppMemberVO implements Serializable{

	private static final long serialVersionUID = 4077018980376224832L;

	private String id;
	
	private String username;
	
	private String avatar;
	
	private String pk_corp;//所属公司主键
	
	private String corpname;//所属公司名称

}
