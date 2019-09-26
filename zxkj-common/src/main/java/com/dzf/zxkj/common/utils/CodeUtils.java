package com.dzf.zxkj.common.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;


/**
 * 数据加密解密
 */
public class CodeUtils {
	
	private static String pubkey;
	
	private static String prikey;
	
	private static String defaultkey;
	
	private static void readUIParameter(){
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try {
			File f = new File("param.txt");
			if (f.isFile()){
				fis = new FileInputStream(f);
				ois = new ObjectInputStream(fis);
				pubkey = (String)ois.readObject();
				prikey =  (String)ois.readObject();
				defaultkey = (String)ois.readObject();
			}
		}catch (Exception ex){
			ex.printStackTrace();
		}
	}
	/**
	 * 加密
	 * @throws Exception 
	 */
	public static String enCode(String value) throws Exception{
		if(pubkey == null || "".equals(pubkey) || defaultkey != null)
			readUIParameter();
		byte[] data = value.getBytes();
		byte[] encodedData = RSACodeUtils.encryptByPublicKey(data, pubkey);
		String password = DataPasswroid.encode(encodedData);
		return password;
	}
	/**
	 * 解密
	 * @throws Exception 
	 */
	public static String deCode(String pvalue) throws Exception{
		if(prikey == null || "".equals(prikey) || defaultkey != null)
			readUIParameter();
		byte[] data = DataPasswroid.decode(pvalue);
		byte[] decodedData = RSACodeUtils.decryptByPrivateKey(data, prikey);
		return new String(decodedData);
	}
}
