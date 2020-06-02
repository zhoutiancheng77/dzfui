package com.dzf.zxkj.app.utils;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
/*
md5加密算法，有16位、32位加密，分别生成32位、64位密文
*/
public class MD516 {
	public static String Md5(String plainText) {
		 return Md5(plainText.getBytes());
		}
public static String Md5(byte[] plainText) {
  String result = null;
  try {
   MessageDigest md = MessageDigest.getInstance("MD5");
   md.update(plainText);
   byte b[] = md.digest();
   int i;
   StringBuffer buf = new StringBuffer("");
   for (int offset = 0; offset < b.length; offset++) {
    i = b[offset];
    if (i < 0)
     i += 256;
    if (i < 16)
     buf.append("0");
    buf.append(Integer.toHexString(i));
   }
   // result = buf.toString();  //md5 32bit
   // result = buf.toString().substring(8, 24))); //md5 16bit
   result = buf.toString().substring(8, 24);
//   System.out.println("mdt 16bit: " + buf.toString().substring(8, 24));
//   System.out.println("md5 32bit: " + buf.toString() );
  } catch (NoSuchAlgorithmException e) {
   //e.printStackTrace();
  }
  return result;
}
/* 测试段 { */
public static void main(String args[]) {
  String passwd = null;
  String loginpasswd = null;
  passwd = "123qaz";   //密码明文
  loginpasswd = Md5(passwd);
  System.out.println("MD5 16Bit : " + loginpasswd);
}
/* 测试段 }*/
}