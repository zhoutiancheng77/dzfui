package com.dzf.zxkj.app.utils;


import com.dzf.zxkj.common.utils.StringUtil;

/**
 * app 参数加密
 * 
 * @author zhangj
 *
 */
public class AppEncryPubUtil {

	/**
	 * 加密参数
	 * 
	 * @param reqmsg
	 * @return
	 */
	public static String enctyParam(String reqmsg) {

		if (StringUtil.isEmpty(reqmsg)) {
			return "";
		}

		return CryptUtil.getInstance().encryptAES(reqmsg);
	}

	/**
	 * 解密参数
	 * 
	 * @param reqmsg
	 * @return
	 */
	public static String decryParam(String reqmsg) {

		if (StringUtil.isEmpty(reqmsg)) {
			return "";
		}

		return CryptUtil.getInstance().decryptAES(reqmsg);

	}

}
