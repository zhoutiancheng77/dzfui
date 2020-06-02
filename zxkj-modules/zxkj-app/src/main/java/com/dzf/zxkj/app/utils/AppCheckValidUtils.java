package com.dzf.zxkj.app.utils;


import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.utils.Common;
import com.dzf.zxkj.common.utils.StringUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 公司校验
 * @author zhangj
 *
 */
public class AppCheckValidUtils {

	/**
	 * 校验手机号是否合法
	 */
	public static void validatePhone(String phone){
		if(StringUtil.isEmpty(phone)){
			throw new BusinessException("手机号不能为空");
		}
		
		String phonepattern = "^((17[0-9])|(14[0-9])|(13[0-9])|(15[^4,\\D])|(18[0-9])|(19[0-9])|(16[0-9]))\\d{8}$";
		Pattern p = Pattern.compile(phonepattern);
		Matcher  m = p.matcher(phone);
		if (!m.matches()) {
			throw new BusinessException("短信发送失败,手机号"+phone+"不合法");
		}
	}
	
	/**
	 * 校验公司主键是否为空
	 * @param pk_corp
	 * @param pk_tempcorp
	 * @throws DZFWarpException
	 */
	public static void isEmptyWithCorp(String pk_corp, String pk_tempcorp,String message)throws DZFWarpException {
		if((StringUtil.isEmpty(pk_corp) 
				|| Common.tempidcreate.equals(pk_corp))
				&& StringUtil.isEmpty(pk_tempcorp)){
			if(!StringUtil.isEmpty(message)){
				throw new BusinessException(message);
			}else{
				throw new BusinessException("公司信息不能为空，请检查！");
			}
		}
	}
	
	
	public static boolean isEmptyCorp(String pk_corp) throws DZFWarpException {
		if(StringUtil.isEmpty(pk_corp) || Common.tempidcreate.equals(pk_corp)){
			return true;
		}else{
			return false;
		}
	}
	
}
