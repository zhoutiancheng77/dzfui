package com.dzf.zxkj.app.pub;

import com.dzf.zxkj.app.pub.rsa.RSACoder;
import com.dzf.zxkj.app.utils.Base64Util;
import com.dzf.zxkj.app.utils.IOUtils;
import com.dzf.zxkj.app.utils.MD516;
import com.dzf.zxkj.common.utils.StringUtil;
import java.io.IOException;
import java.util.Map;

/**
 * 根据前台传递的用户和设备信息生成token
 * 
 * @author zhangj
 *
 */
public class GenTokenUtil {

	public static String publicKey;
	private static String privateKey;

	static {
		try {
			Map<String, Object> keyMap = RSACoder.initKey();
			publicKey = RSACoder.getPublicKey(keyMap);
			privateKey = RSACoder.getPrivateKey(keyMap);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 通过设备信息+用户信息生成token字符串
	 * 
	 * @param devicemsg
	 * @param usermsg
	 * @return
	 */
	public static String getToken(String devicemsg, String usermsg,String corpid) {
		String res = null;
		try {

			byte[] bytes = getTokenByte(devicemsg, usermsg,corpid);

			byte[] resbytes = RSACoder.encryptByPublicKey(bytes, publicKey);


			res = Base64Util.getBASE64(resbytes);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return res;
	}

	public static byte[] getTokenByte(String devicemsg, String usermsg,String corpid) {
		byte[] bytes = null;
		try {
			String id = MD516.Md5(IOUtils.getBytes(new String[] { devicemsg, usermsg,corpid }));

			bytes = IOUtils.getBytes(new String[] { id });

		} catch (Exception e) {
			e.printStackTrace();
		}

		return bytes;
	}

	/**
	 * 验证token信息
	 * 
	 * @param token
	 * @param devicemsg
	 * @param usermsg
	 * @return
	 */
	public static boolean validate(String token, String devicemsg, String usermsg,String corpid) {

		try {
			if (StringUtil.isEmpty(token) || StringUtil.isEmpty(devicemsg) || StringUtil.isEmpty(usermsg) || StringUtil.isEmpty(corpid)) {
				return false;
			}

			byte[] tokenbytes1 = RSACoder.decryptByPrivateKey(Base64Util.getFromBASE64(token), privateKey);

			byte[] resbytes = getTokenByte(devicemsg, usermsg,corpid);

			boolean b = (new String(tokenbytes1).equals(new String(resbytes)));

			return b;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static void main(String[] args) {
		getToken("ABCD123!@#", "","");
	}

}
