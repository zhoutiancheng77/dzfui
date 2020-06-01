package com.dzf.zxkj.app.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * AES 是一种可逆加密算法，对用户的敏感信息加密处理 对原始数据进行AES加密后，在进行Base64编码转化；
 */
public class CryptUtil {

	/*
	 * 加密用的Key 可以用26个字母和数字组成 此处使用AES-128-CBC加密模式，key需要为16位。
	 */
	private String sKey = "yw^&32!@ss&(33d)";// key，可自行修改
	private String ivParameter = "7326d83jdw0uue18";// 偏移量,可自行修改
	private static CryptUtil instance = null;

	private CryptUtil() {

	}

	public static CryptUtil getInstance() {
		if (instance == null)
			instance = new CryptUtil();
		return instance;
	}

	public static String Encrypt(String encData, String secretKey, String vector) throws Exception {
		if (secretKey == null) {
			return null;
		}
		if (secretKey.length() != 16) {
			return null;
		}
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		byte[] raw = secretKey.getBytes();
		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		IvParameterSpec iv = new IvParameterSpec(vector.getBytes());// 使用CBC模式，需要一个向量iv，可增加加密算法的强度
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
		byte[] encrypted = cipher.doFinal(encData.getBytes("utf-8"));
		return new BASE64Encoder().encode(encrypted);// 此处使用BASE64做转码。
	}
	


	// 加密
	public String encryptAES(String sSrc) {
		try {
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			byte[] raw = sKey.getBytes();
			SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
			IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes());// 使用CBC模式，需要一个向量iv，可增加加密算法的强度
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
			byte[] encrypted = cipher.doFinal(sSrc.getBytes("utf-8"));
//			return new BASE64Encoder().encode(encrypted);// 此处使用BASE64做转码。
			return asHex(encrypted);
		} catch (Exception e) {
			return null;
		}
	}

	// 解密
	public String decryptAES(String sSrc) {
		try {
			byte[] raw = sKey.getBytes("ASCII");
			SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes());
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
//			byte[] encrypted1 = new BASE64Decoder().decodeBuffer(sSrc);// 先用base64解密
			byte[] encrypted1 = asBytes(sSrc);
			byte[] original = cipher.doFinal(encrypted1);
			String originalString = new String(original, "utf-8");
			return originalString;
		} catch (Exception ex) {
			return null;
		}
	}

	public String decrypt(String sSrc, String key, String ivs) throws Exception {
		try {
			byte[] raw = key.getBytes("ASCII");
			SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			IvParameterSpec iv = new IvParameterSpec(ivs.getBytes());
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
			byte[] encrypted1 = new BASE64Decoder().decodeBuffer(sSrc);// 先用base64解密
			byte[] original = cipher.doFinal(encrypted1);
			String originalString = new String(original, "utf-8");
			return originalString;
		} catch (Exception ex) {
			return null;
		}
	}

	public static String encodeBytes(byte[] bytes) {
		StringBuffer strBuf = new StringBuffer();

		for (int i = 0; i < bytes.length; i++) {
			strBuf.append((char) (((bytes[i] >> 4) & 0xF) + ((int) 'a')));
			strBuf.append((char) (((bytes[i]) & 0xF) + ((int) 'a')));
		}

		return strBuf.toString();
	}
	
    /** 
     * 将2进制数值转换为16进制字符串 
     * @param buf 
     * @return 
     */  
    public String asHex(byte buf[]){  
        StringBuffer strbuf = new StringBuffer(buf.length * 2);  
        int i;  
        for (i = 0; i < buf.length; i++){  
            if (((int) buf[i] & 0xff) < 0x10)  
                strbuf.append("0");  
            strbuf.append(Long.toString((int) buf[i] & 0xff, 16));  
        }  
        return strbuf.toString();  
    }  
	
    /** 
     * 将16进制转换 
     * @param hexStr 
     * @return 
     */  
    public byte[] asBytes(String hexStr) {  
        if (hexStr.length() < 1)  
            return null;  
        byte[] result = new byte[hexStr.length() / 2];  
        for (int i = 0; i < hexStr.length() / 2; i++){  
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);  
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2),16);  
            result[i] = (byte) (high * 16 + low);  
        }  
        return result;  
    }  

//	public static void main(String[] args) throws Exception {
//		CryptUtil crypt = CryptUtil.getInstance();
//		String content = "appuse00000000aAosRX0001";
//		String dcontent = crypt.encryptAES(content);
//		System.out.println("加密：" + dcontent);
//
//		System.out.println("解密：" + crypt.decryptAES(dcontent));
//	}

}