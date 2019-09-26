package com.dzf.zxkj.platform.services.sys.impl;

public class CheckPwdSecurity {

	/**
	 * 校验密码的安全性
	 */
	public static String checkUserPWD2(String usercode,String pwd,StringBuffer sf){
		if(sf == null)
			sf = new StringBuffer();
		//校验用户名是否密码的子集
		String usr1= usercode.substring(1, usercode.length()-1);
		if(pwd.contains(usr1)){
			sf.append(" 密码不能包含用户编码! \n ");
		}
		return sf.toString();
	}
	
}
