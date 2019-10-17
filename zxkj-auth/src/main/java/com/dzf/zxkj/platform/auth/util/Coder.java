package com.dzf.zxkj.platform.auth.util;
 	import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.security.MessageDigest;
 
public class Coder {
   public static final String KEY_SHA="SHA";
public static final String KEY_MD5="MD5";
   
  /**
12 	     * BASE64解密
13 	     * @param key
14 	     * @return
15 	     * @throws Exception
16 	     */
 public static byte[] decryptBASE64(String key) throws Exception{
       return (new BASE64Decoder()).decodeBuffer(key);
 }
    
   /**
22 	     * BASE64加密
23 	     * @param key
24 	     * @return
25 	     * @throws Exception
26 	     */
 public static String encryptBASE64(byte[] key)throws Exception{
      return (new BASE64Encoder()).encodeBuffer(key);
	    }
    
	    /**
32 	     * MD5加密
33 	     * @param data
34 	     * @return
35 	     * @throws Exception
36 	     */
    public static byte[] encryptMD5(byte[] data)throws Exception{
        MessageDigest md5 = MessageDigest.getInstance(KEY_MD5);
        md5.update(data);
        return md5.digest();
 	    }
	     
	    /**
44 	     * SHA加密
45 	     * @param data
46 	     * @return
47 	     * @throws Exception
48 	     */
	    public static byte[] encryptSHA(byte[] data)throws Exception{
        MessageDigest sha = MessageDigest.getInstance(KEY_SHA);
        return sha.digest();
	}
}