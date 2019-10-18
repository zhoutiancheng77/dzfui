package com.dzf.zxkj.platform.service.gzgl.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@Slf4j
public class Des3Utils {

	/** 字符串默认键值 */
    private static String strDefaultKey = "AAHdKxzVCbsgdzfTnc1jtpWn";
	
	/**
	 * 转换成十六进制字符串
	 * 
	 * @param 'username'
	 * @return
	 *
	 * 		lee on 2017-08-09 10:54:19
	 */
	public static byte[] hex(String key) {
		String f = DigestUtils.md5Hex(key);
		byte[] bkeys = new String(f).getBytes();
		byte[] enk = new byte[24];
		for (int i = 0; i < 24; i++) {
			enk[i] = bkeys[i];
		}
		return enk;
	}

	/**
	 * 3DES加密
	 * 
	 * @param 'key'
	 *            密钥，24位
	 * @param srcStr
	 *            将加密的字符串
	 * @return
	 *
	 * 		lee on 2017-08-09 10:51:44
	 */
	public static String encode3Des( String srcStr) {
		byte[] keybyte = hex(strDefaultKey);
		byte[] src = srcStr.getBytes();
		try {
			// 生成密钥
			SecretKey deskey = new SecretKeySpec(keybyte, "DESede");
			// 加密
			Cipher c1 = Cipher.getInstance("DESede");
			c1.init(Cipher.ENCRYPT_MODE, deskey);

			byte[] bytes = Base64.encodeBase64(c1.doFinal(src));//encodeBase64String(c1.doFinal(src));
			// return c1.doFinal(src);//在单一方面的加密或解密
			return new String(bytes, "UTF-8");
		} catch (java.security.NoSuchAlgorithmException e1) {
			// TODO: handle exception
			log.error(e1.getMessage(),e1);
		} catch (javax.crypto.NoSuchPaddingException e2) {
			log.error(e2.getMessage(),e2);
		} catch (Exception e3) {
			log.error(e3.getMessage(),e3);
		}
		return null;
	}

	/**
	 * 3DES解密
	 * 
	 * @param 'key'
	 *            加密密钥，长度为24字节
	 * @param desStr
	 *            解密后的字符串
	 * @return
	 *
	 * 		lee on 2017-08-09 10:52:54
	 */
	public static String decode3Des(String desStr) {
		Base64 base64 = new Base64();
		byte[] keybyte = hex(strDefaultKey);

		try {
			byte[] src = base64.decode(desStr.getBytes("UTF-8"));
			// 生成密钥
			SecretKey deskey = new SecretKeySpec(keybyte, "DESede");
			// 解密
			Cipher c1 = Cipher.getInstance("DESede");
			c1.init(Cipher.DECRYPT_MODE, deskey);
			String pwd = new String(c1.doFinal(src));
			return pwd;
		} catch (java.security.NoSuchAlgorithmException e1) {
			log.error(e1.getMessage(),e1);
		} catch (javax.crypto.NoSuchPaddingException e2) {
			log.error(e2.getMessage(),e2);
		} catch (Exception e3) {
			log.error(e3.getMessage(),e3);
		}
		return null;
	}
	
	public static void main(String[] args) {
		String str = "dd，1d{d张}}三";
		String en_str =Des3Utils.encode3Des(str) ;
		
		System.out.println(en_str);
		System.out.println(Des3Utils.decode3Des(en_str));
	}

}
