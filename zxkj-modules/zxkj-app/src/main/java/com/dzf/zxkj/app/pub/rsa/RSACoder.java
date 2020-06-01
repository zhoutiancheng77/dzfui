package com.dzf.zxkj.app.pub.rsa;
import org.apache.commons.lang3.ArrayUtils;

import javax.crypto.Cipher;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;


/** */

/**
* RSA安全编码组件
*  
* @version 1.0
* @since 1.0
*/
public abstract class RSACoder extends Coder {  
    public static final String KEY_ALGORITHM = "RSA";  
    public static final String SIGNATURE_ALGORITHM = "MD5withRSA";  

    private static final String PUBLIC_KEY = "RSAPublicKey";  
    private static final String PRIVATE_KEY = "RSAPrivateKey";  

    /** *//**
     * 用私钥对信息生成数字签名
     *  
     * @param data
     *            加密数据
     * @param privateKey
     *            私钥
     *  
     * @return
     * @throws Exception
     */
    public static String sign(byte[] data, String privateKey) throws Exception {  
        // 解密由base64编码的私钥  
        byte[] keyBytes = decryptBASE64(privateKey);  

        // 构造PKCS8EncodedKeySpec对象  
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);  

        // KEY_ALGORITHM 指定的加密算法  
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);  

        // 取私钥匙对象  
        PrivateKey priKey = keyFactory.generatePrivate(pkcs8KeySpec);  

        // 用私钥对信息生成数字签名  
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);  
        signature.initSign(priKey);  
        signature.update(data);  

        return encryptBASE64(signature.sign());  
    }  

    /** *//**
     * 校验数字签名
     *  
     * @param data
     *            加密数据
     * @param publicKey
     *            公钥
     * @param sign
     *            数字签名
     *  
     * @return 校验成功返回true 失败返回false
     * @throws Exception
     *  
     */
    public static boolean verify(byte[] data, String publicKey, String sign)  
            throws Exception {  

        // 解密由base64编码的公钥  
        byte[] keyBytes = decryptBASE64(publicKey);  

        // 构造X509EncodedKeySpec对象  
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);  

        // KEY_ALGORITHM 指定的加密算法  
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);  

        // 取公钥匙对象  
        PublicKey pubKey = keyFactory.generatePublic(keySpec);  

        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);  
        signature.initVerify(pubKey);  
        signature.update(data);  

        // 验证签名是否正常  
        return signature.verify(decryptBASE64(sign));  
    }  

    /** *//**
     * 解密<br>
     * 用私钥解密 http://www.5a520.cn http://www.feng123.com
     *  
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] decryptByPrivateKey(byte[] data, String key)  
            throws Exception {  
        // 对密钥解密  
        byte[] keyBytes = decryptBASE64(key);  

        // 取得私钥  
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);  
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);  
        Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);  

        // 对数据解密  
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());  
        cipher.init(Cipher.DECRYPT_MODE, privateKey);  

        //return cipher.doFinal(data);  
        
     // 解密时超过128字节就报错。为此采用分段解密的办法来解密
        byte[] enBytes = null;
        for (int i = 0; i < data.length; i += 128) {
            byte[] doFinal = cipher.doFinal(ArrayUtils.subarray(data, i, i + 128));
            enBytes = ArrayUtils.addAll(enBytes, doFinal);  
        }
        return enBytes;
//       return cipher.doFinal(data);  
    }  

    /** *//**
     * 解密<br>
     * 用私钥解密
     *  
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] decryptByPublicKey(byte[] data, String key)  
            throws Exception {  
        // 对密钥解密  
        byte[] keyBytes = decryptBASE64(key);  

        // 取得公钥  
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);  
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);  
        Key publicKey = keyFactory.generatePublic(x509KeySpec);  

        // 对数据解密  
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());  
        cipher.init(Cipher.DECRYPT_MODE, publicKey);  

      //  return cipher.doFinal(data);  
        
        // 解密时超过128字节就报错。为此采用分段解密的办法来解密
        byte[] enBytes = null;
        for (int i = 0; i < data.length; i += 128) {
            byte[] doFinal = cipher.doFinal(ArrayUtils.subarray(data, i, i + 128));
            enBytes = ArrayUtils.addAll(enBytes, doFinal);  
        }
        return enBytes;//
        //return cipher.doFinal(data);  
    }  

    /** *//**
     * 加密<br>
     * 用公钥加密
     *  
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPublicKey(byte[] data, String key)  
            throws Exception {  
        // 对公钥解密  
        byte[] keyBytes = decryptBASE64(key);  

        // 取得公钥  
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);  
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);  
        Key publicKey = keyFactory.generatePublic(x509KeySpec);  

        // 对数据加密  
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());  
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);  

        
        // 加密时超过117字节就报错。为此采用分段加密的办法来加密 
        byte[] enBytes = null;
        for (int i = 0; i < data.length; i += 64) {  
        // 注意要使用2的倍数，否则会出现加密后的内容再解密时为乱码
            byte[] doFinal = cipher.doFinal(ArrayUtils.subarray(data, i,i + 64));  
            enBytes = ArrayUtils.addAll(enBytes, doFinal);  
        }
        return enBytes;//cipher.doFinal(data);  
    }  

    /** *//**
     * 加密<br>
     * 用私钥加密
     *  
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPrivateKey(byte[] data, String key)  
            throws Exception {  
        // 对密钥解密  
        byte[] keyBytes = decryptBASE64(key);  

        // 取得私钥  
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);  
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);  
        Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);  

        // 对数据加密  
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());  
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);  

       // return cipher.doFinal(data);  
     // 加密时超过117字节就报错。为此采用分段加密的办法来加密 
        byte[] enBytes = null;
        for (int i = 0; i < data.length; i += 64) {  
        // 注意要使用2的倍数，否则会出现加密后的内容再解密时为乱码
            byte[] doFinal = cipher.doFinal(ArrayUtils.subarray(data, i,i + 64));  
            enBytes = ArrayUtils.addAll(enBytes, doFinal);  
        }
        return enBytes;//cipher.doFinal(data);  
    }  

    /** *//**
     * 取得私钥
     *  
     * @param keyMap
     * @return
     * @throws Exception
     */
    public static String getPrivateKey(Map<String, Object> keyMap)  
            throws Exception {  
        Key key = (Key) keyMap.get(PRIVATE_KEY);  

        return encryptBASE64(key.getEncoded());  
    }  

    /** *//**
     * 取得公钥
     *  
     * @param keyMap
     * @return
     * @throws Exception
     */
    public static String getPublicKey(Map<String, Object> keyMap)  
            throws Exception {  
        Key key = (Key) keyMap.get(PUBLIC_KEY);  

        return encryptBASE64(key.getEncoded());  
    }  

    /** *//**
     * 初始化密钥
     *  
     * @return
     * @throws Exception
     */
    public static Map<String, Object> initKey() throws Exception {  
    	//2016-07-06注释，改用统一publickey，privatekey
//        KeyPairGenerator keyPairGen = KeyPairGenerator  
//                .getInstance(KEY_ALGORITHM);  
//        keyPairGen.initialize(1024);  
//
//        KeyPair keyPair = keyPairGen.generateKeyPair();  

//        // 公钥  
//        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();  
//
//        // 私钥  
//        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();  
    	// 公钥  
    	RSAPublicKey publickey = RSAUtils.getDefaultPublicKey();
//		String defaultPublicKey = RSACoder.encryptBASE64(publickey.getEncoded());
		// 私钥  
		RSAPrivateKey privatekey = RSAUtils.getDefaultPrivateKey();
//		String defaultPrivateKey = RSACoder.encryptBASE64(privatekey.getEncoded());
		
        Map<String, Object> keyMap = new HashMap<String, Object>(2);  

        keyMap.put(PUBLIC_KEY, publickey);  
        keyMap.put(PRIVATE_KEY, privatekey);  
        return keyMap;  
    }  
} 