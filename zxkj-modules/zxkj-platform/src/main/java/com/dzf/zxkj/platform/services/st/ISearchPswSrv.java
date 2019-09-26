package com.dzf.zxkj.platform.services.st;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.sys.UserVO;

public interface ISearchPswSrv {

	/**
	 * param:ucode用户编码
	 * 返回用户电话号码
	*/
	public UserVO getPhoByUcode(String ucode) throws DZFWarpException;
	
	/**
	 * param:UserVO
	 * 保存重置的电话号码
	*/
	public UserVO savePsw(UserVO uvo)  throws DZFWarpException;
	
	/**
	 * param:UserVO
	 * 校验用户编码是否存在
	*/
	public UserVO UCodeIsExist(String user_code)  throws DZFWarpException;
}
