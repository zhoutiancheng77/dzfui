package com.dzf.zxkj.base.utils;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.exception.WiseRunException;
import com.dzf.zxkj.common.utils.RC4;
import com.dzf.zxkj.common.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;


/**
 * 数据加密解密
 */
@Slf4j
public class CodeUtils1 {
	
	private static ClassPathResource resource = new ClassPathResource("param.txt");

	private static String pubkey;
	
	private static String prikey;
	
	private static String defaultkey;
	
	private static void readUIParameter(){
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try {
			String path = resource.getURL().getPath(); 
			File f = new File(path);
			if (f.isFile()){
				fis = new FileInputStream(f);
				ois = new ObjectInputStream(fis);
				pubkey = (String)ois.readObject();
				prikey =  (String)ois.readObject();
				defaultkey = (String)ois.readObject();
			}
		}catch (Exception ex){
			log.error("错误",ex);
			throw new WiseRunException(ex);
		}
	}

	//这里异常吃掉
	public static String enCode(String value) throws DZFWarpException {
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
	public static String deCode(String pvalue) throws DZFWarpException{
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
