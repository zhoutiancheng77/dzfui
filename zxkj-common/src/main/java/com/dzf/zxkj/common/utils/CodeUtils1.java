package com.dzf.zxkj.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

import java.io.ObjectInputStream;


/**
 * 数据加密解密
 */
@Slf4j
public class CodeUtils1 {
	
	private static ClassPathResource resource;

	private static String pubkey;
	
	private static String prikey;
	
	private static String defaultkey;

	static {
		resource = new ClassPathResource("param.txt");
	}
	
	private static void readUIParameter(){
		try {
			ObjectInputStream ois = new ObjectInputStream(resource.getInputStream());
				pubkey = (String)ois.readObject();
				prikey =  (String)ois.readObject();
				defaultkey = (String)ois.readObject();
		}catch (Exception ex){
			log.error("错误",ex);
			throw new RuntimeException(ex);
		}
	}

	//这里异常吃掉
	public static String enCode(String value) throws RuntimeException {
		if(StringUtil.isEmpty(value))
			return value;
		if(pubkey == null || "".equals(pubkey))
			readUIParameter();
		String key = null;
		try{
			key =  RC4.encry_RC4_string(value, defaultkey);
		}catch(Exception e){
			//log.error(value+"，加密失败",e);
			key = value;
		}
		return key;
	}

	//这里异常吃掉
	public static String deCode(String pvalue) throws RuntimeException{
		if(StringUtil.isEmpty(pvalue))
			return pvalue;
		if(prikey == null || "".equals(prikey))
			readUIParameter();
		String key = null;
		try{
			key = RC4.decry_RC4(pvalue,defaultkey);
		}catch(Exception e){
			//log.error(pvalue+",解密失败",e);
			key = pvalue;
		}
		if(StringUtil.isEmpty(key))
			key = pvalue;
		return key;
	}
}
