package com.dzf.zxkj.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.*;


/**
 * <p>
 * BASE64编码解码工具包
 * 
 */
@Slf4j
public class Base64CodeUtils {
 
    /**
     * 文件读取缓冲区大小
     */
    private static final int CACHE_SIZE = 1024;

    /**
     * <p>
     * BASE64字符串解码为二进制数据
     * </p>
     * 
     * @param base64
     * @return
     * @throws Exception
     */
    public static byte[] decode(String base64) throws Exception {
//        return Base64.decode(base64.getBytes());//--javabase64-1.3.1.jar中的内容
    	
//-------------------------------使用 sun.misc.BASE64Decoder
        if (base64 == null)
			return null;
		sun.misc.BASE64Decoder decoder = new sun.misc.BASE64Decoder();
		byte[] b = decoder.decodeBuffer(base64);
		return b;
		
		//--------------使用commons-codec-1.8.jar  import org.apache.commons.codec.binary.Base64;
		
//    	return Base64.decodeBase64(base64);
    }
     
    /**
     * <p>
     * 二进制数据编码为BASE64字符串
     * </p>
     * 
     * @param bytes
     * @return
     * @throws Exception
     */
    public static String encode(byte[] bytes) throws Exception {
//        return new String(Base64.encode(bytes));//--javabase64-1.3.1.jar中的内容
    	
    	//-------------------------------使用 sun.misc.BASE64Encoder
        if (bytes == null)
			return null;
		return (new sun.misc.BASE64Encoder()).encode(bytes);
    	//--------------使用commons-codec-1.8.jar  import org.apache.commons.codec.binary.Base64;
//    	return Base64.encodeBase64String(bytes);
    }
     
    /**
     * <p>
     * 将文件编码为BASE64字符串
     * </p>
     * <p>
     * 大文件慎用，可能会导致内存溢出
     * </p>
     * 
     * @param filePath 文件绝对路径
     * @return
     * @throws Exception
     */
    public static String encodeFile(String filePath) throws Exception {
        byte[] bytes = fileToByte(filePath);
        return encode(bytes);
    }
     
    /**
     * <p>
     * BASE64字符串转回文件
     * </p>
     * 
     * @param filePath 文件绝对路径
     * @param base64 编码字符串
     * @throws Exception
     */
    public static void decodeToFile(String filePath, String base64) throws Exception {
        byte[] bytes = decode(base64);
        byteArrayToFile(bytes, filePath);
    }
     
    /**
     * <p>
     * 文件转换为二进制数组
     * </p>
     * 
     * @param filePath 文件路径
     * @return
     * @throws Exception
     */
    public static byte[] fileToByte(String filePath) throws Exception {
        byte[] data = new byte[0];
        File file = new File(filePath);
        if (file.exists()) {
            FileInputStream in = new FileInputStream(file);
            ByteArrayOutputStream out = new ByteArrayOutputStream(2048);
            try{
                byte[] cache = new byte[CACHE_SIZE];
                int nRead = 0;
                while ((nRead = in.read(cache)) != -1) {
                    out.write(cache, 0, nRead);
                    out.flush();
                }
                data = out.toByteArray();
            }catch(Exception e){
            	log.error("错误",e);
            }finally{
            	if(out!=null){
            		 out.close();
            	}
            	if(in!=null){
            		in.close();
            	}
            }
         }
        return data;
    }
     
    /**
     * <p>
     * 二进制数据写文件
     * </p>
     * 
     * @param bytes 二进制数据
     * @param filePath 文件生成目录
     */
    public static void byteArrayToFile(byte[] bytes, String filePath) throws Exception {
    	InputStream in = null;
    	OutputStream out = null;
    	try{
            in = new ByteArrayInputStream(bytes);   
            File destFile = new File(filePath);
            if (!destFile.getParentFile().exists()) {
                destFile.getParentFile().mkdirs();
            }
            destFile.createNewFile();
            out = new FileOutputStream(destFile);
            byte[] cache = new byte[CACHE_SIZE];
            int nRead = 0;
            while ((nRead = in.read(cache)) != -1) {   
                out.write(cache, 0, nRead);
                out.flush();
            }
    	}catch(Exception e){
        	log.error("错误",e);
        }finally{
        	if(out!=null){
        		 out.close();
        	}
        	if(in!=null){
        		in.close();
        	}
        }
    }
}