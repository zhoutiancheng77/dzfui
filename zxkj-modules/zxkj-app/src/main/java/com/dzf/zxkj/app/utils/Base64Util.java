package com.dzf.zxkj.app.utils;

import java.io.IOException;

public class Base64Util {  
	public static String getBASE64(String srcVal) {
		if (srcVal == null)
			return null;
		return (new sun.misc.BASE64Encoder()).encode(srcVal.getBytes());
	}
	public static String getBASE64(byte[] bs) {
		if (bs == null)
			return null;
		return (new sun.misc.BASE64Encoder()).encode(bs);
	}
//	// �� BASE64 ������ַ� s ���н��� ����
//	public static String getFromBASE64(String s) throws IOException {
//		if (s == null)
//			return null;
//		sun.misc.BASE64Decoder decoder = new sun.misc.BASE64Decoder();
//		byte[] b = decoder.decodeBuffer(s);
//		return new String(b);
//	}
	public static byte[] getFromBASE64(String s) throws IOException {
		if (s == null)
			return null;
		sun.misc.BASE64Decoder decoder = new sun.misc.BASE64Decoder();
		byte[] b = decoder.decodeBuffer(s);
		return b;
	}
	public static String encrypt(Object srcStr) {
		return getBASE64((String) srcStr);
	}

//	public static String decrypt(String enStr) throws IOException {
//		return getFromBASE64(enStr);
//	}
}  