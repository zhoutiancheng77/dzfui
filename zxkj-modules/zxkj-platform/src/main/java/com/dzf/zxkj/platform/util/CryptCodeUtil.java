package com.dzf.zxkj.platform.util;


import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 数据加密解密
 */
@Slf4j
public class CryptCodeUtil {
	
//	private static ClassPathResource resource = new ClassPathResource("encrypt_key.txt");

//	private static String pubkey = "d3d3LmRhemhhbmdmYW5nLmNvbQ==";

	private static String defaultkey = "www.dazhangfang.com";
	
//	private static void readUIParameter(){
//		FileInputStream fis = null;
//		ObjectInputStream ois = null;
//		try {
//			String path = resource.getURL().getPath();
//			File f = new File(path);
//			if (f.isFile()){
//				fis = new FileInputStream(f);
//				ois = new ObjectInputStream(fis);
//				pubkey = (String)ois.readObject();
//				defaultkey = new String(Base64CodeUtils.decode(pubkey));
//			}
//		}catch (Exception ex){
//			log.error("错误",ex);
//			throw new WiseRunException(ex);
//		}
//	}

	//这里异常吃掉
	public static String enCode(String value) throws DZFWarpException {
		if(StringUtil.isEmpty(value))
			return value;
//		if(pubkey == null || "".equals(defaultkey))
//			readUIParameter();
		String key = null;
		try{
			key =  RC4.encrypt(value, defaultkey);
		}catch(Exception e){
			//LOG.error(value+"，加密失败",e);
			key = value;
		}
		return key;
	}

	//这里异常吃掉
	public static String deCode(String pvalue) throws DZFWarpException{
		if(StringUtil.isEmpty(pvalue))
			return pvalue;
//		if(pubkey == null || "".equals(defaultkey))
//			readUIParameter();
		String key = null;
		try{
			key = RC4.decrypt(pvalue,defaultkey);
		}catch(Exception e){
			//LOG.error(pvalue+",解密失败",e);
			key = pvalue;
		}
		if(StringUtil.isEmpty(key))
			key = pvalue;
		return key;
	}
}
