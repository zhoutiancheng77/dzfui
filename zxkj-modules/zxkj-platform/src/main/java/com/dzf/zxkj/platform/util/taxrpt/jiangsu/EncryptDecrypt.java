package com.dzf.zxkj.platform.util.taxrpt.jiangsu;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Crazy on 2016/5/13.
 * 创建人：lishaofeng
 * 日期：2016/5/13.
 * 时间：16:59.
 * 项目名称：delta-ucenter.
 * 类注释：接口参数加密和解密
 */
public class EncryptDecrypt {

    /**
     * 默认加密密文
     */
    private static String defaultKey="ucenter";

    /**
     * note 随机密钥长度 取值 0-32;
     * note 加入随机密钥，可以令密文无任何规律，即便是原文和密钥完全相同，加密结果也会每次不同，增大破解难度。
     * note 取值越大，密文变动规律越大，密文变化 = 16 的 $ckey_length 次方
     * note 当此值为 0 时，则不产生随机密钥
     */
    private static final  Integer keyLength=4;


    /**
     * 参数加密
     * @param str 加密字符串
     * @return
     */
    public static String encode(String str,String key){
        return encode(str,key,0);
    }

    /**
     * 参数加密
     * @param str  加密字符串
     * @param key  通信密钥，需要从应用表中查询得到
     * @param expiry   密文有效期, 加密时候有效， 单位 秒，0 为永久有效
     * @return
     */
    public static String encode(String str,String key,Integer expiry){
        try {
            str = java.net.URLEncoder.encode(str,"UTF-8");
        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
        }
        key = md5( key!=null ? key : defaultKey);
        String keyA = md5(subString(key, 0, 16));
        String keyB = md5(subString(key, 16, 16));
        String keyC = keyLength > 0? subString(md5(microTime()), -keyLength): "";

        String cryptKey = keyA + md5( keyA + keyC);
        int key_length = cryptKey.length();

        str = sprintf("%010d", expiry>0 ? expiry + time() : 0)+subString(md5(str + keyB), 0, 16)+str;
        int string_length = str.length();

        StringBuffer resultBf = new StringBuffer();

        int[] box = new int[256];
        for(int i=0;i<256;i++){
            box[i] = i;
        }

        int[] rndkey = new int[256];
        for(int i = 0; i <= 255; i++) {
            rndkey[i] = (int)cryptKey.charAt(i % key_length);
        }

        int j=0;
        for(int i = 0; i < 256; i++) {
            j = (j + box[i] + rndkey[i]) % 256;
            int tmp = box[i];
            box[i] = box[j];
            box[j] = tmp;
        }

        j=0;
        int a=0;
        for(int i = 0; i < string_length; i++) {
            a = (a + 1) % 256;
            j = (j + box[a]) % 256;
            int tmp = box[a];
            box[a] = box[j];
            box[j] = tmp;

            resultBf.append((char)( ((int)str.charAt(i)) ^ (box[(box[a] + box[j]) % 256])));

        }

        return keyC+base64Encode(resultBf.toString()).replaceAll("=", "");
    }

//    /**
//     * 参数解密
//     * @param str
//     * @return
//     */
//    public static String decode(String str){
//        return decode(str,null);
//    }

    /**
     * 参数解密
     * @param str  密文
     * @param key  密钥
     * @return
     */
    public static String decode(String str,String key){

        key = md5( key!=null ? key : defaultKey);
        String keyA = md5(subString(key, 0, 16));
        String keyB = md5(subString(key, 16, 16));
        String keyC = keyLength > 0? subString(str, 0, keyLength) : "";

        String cryptKey = keyA + md5( keyA + keyC);
        int key_length = cryptKey.length();

        str =base64Decode(subString(str, keyLength));
        int string_length = str.length();

        StringBuffer resultBf = new StringBuffer();

        int[] box = new int[256];
        for(int i=0;i<256;i++){
            box[i] = i;
        }

        int[] rndkey = new int[256];
        for(int i = 0; i <= 255; i++) {
            rndkey[i] = (int)cryptKey.charAt(i % key_length);
        }

        int j=0;
        for(int i = 0; i < 256; i++) {
            j = (j + box[i] + rndkey[i]) % 256;
            int tmp = box[i];
            box[i] = box[j];
            box[j] = tmp;
        }

        j=0;
        int a=0;
        for(int i = 0; i < string_length; i++) {
            a = (a + 1) % 256;
            j = (j + box[a]) % 256;
            int tmp = box[a];
            box[a] = box[j];
            box[j] = tmp;

            resultBf.append((char)( ((int)str.charAt(i)) ^ (box[(box[a] + box[j]) % 256])));

        }

        String result = resultBf.substring(0, resultBf.length());
        if((Integer.parseInt(subString(result, 0, 10)) == 0 || Long.parseLong(subString(result, 0, 10)) - time() > 0) && subString(result, 10, 16).equals( subString(md5(subString(result, 26) + keyB), 0, 16))) {
            String resultStr = subString(result, 26);
            try {
                resultStr = java.net.URLDecoder.decode(resultStr,"UTF-8");
            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
            }
            return resultStr;
        } else {
            return "";
        }

    }
    private static String md5(String input) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
            return null;
        }
        return byte2hex(md.digest(input.getBytes()));
    }

    private static String md5(long input) {
        return md5(String.valueOf(input));
    }

    private static String base64Decode(String input) {
        try {
            return new String(Base64.decode(input.toCharArray()), "iso-8859-1");
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private static String base64Encode(String input) {
        try {
            return new String(Base64.encode(input.getBytes("iso-8859-1")));
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private static String byte2hex(byte[] b) {
        StringBuffer hs = new StringBuffer();
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = (Integer.toHexString(b[n] & 0XFF));
            if (stmp.length() == 1)
                hs.append("0").append(stmp);
            else
                hs.append(stmp);
        }
        return hs.toString();
    }

    /**
     * 截取字符串
     * @param input 输入字符串
     * @param begin 开始位置
     * @param length 长度
     * @return
     */
    private static  String subString(String input, int begin, int length) {
        return input.substring(begin, begin + length);
    }

    /**
     * 从什么位置开始截取字符串
     * @param input 输入字符串
     * @param begin 开始长度
     * @return
     */
    private static String subString(String input, int begin) {
        if (begin > 0) {
            return input.substring(begin);
        } else {
            return input.substring(input.length() + begin);
        }
    }

    /**
     * 获取时间到毫秒
     * @return
     */
    private static long microTime() {
        return System.currentTimeMillis();
    }

    /**
     * 获取时间到秒
     * @return
     */
    private static long time() {
        return System.currentTimeMillis() / 1000;
    }

    /**
     * 格式字符串
     * @param format
     * @param input
     * @return
     */
    private static String sprintf(String format, long input) {
        String temp = "0000000000" + input;
        return temp.substring(temp.length() - 10);
    }

    public static void main(String[] args) throws Exception{
      String str=  EncryptDecrypt.encode("中国社会fsfsdf","db473312b61b9bd165f091906cfe44b5a018d234");
        System.out.println("================"+str);
       String str1 = EncryptDecrypt.decode("5b97L/HlTWPIcnARNSbpCXpsuSPwmxfon0kS+DfxrCUzfObdOCPY3EwsdxmvA84akGbir8AXj7LVQ99EUp+X3CwTCOHl8Fs","db473312b61b9bd165f091906cfe44b5a018d234");
        System.out.println(str1);
//        System.out.println("======="+System.currentTimeMillis());
    }

}
