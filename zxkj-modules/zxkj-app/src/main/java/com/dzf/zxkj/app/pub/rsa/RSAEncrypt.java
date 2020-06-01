package com.dzf.zxkj.app.pub.rsa;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import sun.misc.BASE64Decoder;

public class RSAEncrypt
{
  public static String iosPrivave_key = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBALyJEMKYyIPffyCMxjuBXxAaks3Iqgw9euuznkYf68G9nVClmJXEcN0U6T9N2nJEkgoBxCmFNhGDrQzdkUMqLho2aLbH1DrDD61tXec+oXRB2RaIHY8/XcM2ufFN96XLeTHyAIFAxxyzfvgbByws3K49o0QtKPI7LfHhvPgP8yQFAgMBAAECf2WhfWdo9K4hzGW/fRyWoKgNqxVDFmeCvMBSEv+6NVw62pKesaIKQszqeNGnJhpy3EaIrTW0mXDXinFk/uk106NgnTPbNWAYthqxSI2Pqo/ggrEpMPLz6hQzPjRgCd2RPsCJcY+j3/F73RC/0lTb9jSyi5L61Xe67bK6xrHLT7kCQQDlY9Qd/q2R72lvPWxvZbmGmDd5d4VJ5F56Dz1mFOelDyHhUk+/oA09F0ggWPBgHbOom3YffnCxVXojnI6kBHOjAkEA0mf7wwrrPBh5CmSpeX5IkHPB/FCAapje9ZOw529IKBBtOSEGrriHYICjtkCia70Ax5RJXohI1wYMyGCNdw1ENwJBANa3oBjHiG7e3CFNLJAFdyjemKaxUul8w1abp0xDayVcycjgxFdJmLrDbTciKTa+9qlvg50tPI0xxpdI91fd9uMCQQDIRHcq13q9saGMFfIFlQCmlbffXBRO4gDeCFyKfK5WjAQaK2g756HbZbXtpCm0mKpkTK1lEB0cpUKHevdLiNITAkEA0B4JDvKjRZP7bT34qjlc0PtX/o2gX6U6tPJ/6tb/l7yIsFQY1BxC6x3IHhJ+KULm1HW/LDv4/8F0AvWurWIFKQ";
  private static final String DEFAULT_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQChDzcjw/rWgFwnxunbKp7/4e8w\r/UmXx2jk6qEEn69t6N2R1i/LmcyDT1xr/T2AHGOiXNQ5V8W4iCaaeNawi7aJaRht\rVx1uOH/2U378fscEESEG8XDqll0GCfB1/TjKI2aitVSzXOtRs8kYgGU78f7VmDNg\rXIlk3gdhnzh+uoEQywIDAQAB\r";
  private static final String DEFAULT_PRIVATE_KEY = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAKEPNyPD+taAXCfG\r6dsqnv/h7zD9SZfHaOTqoQSfr23o3ZHWL8uZzINPXGv9PYAcY6Jc1DlXxbiIJpp4\r1rCLtolpGG1XHW44f/ZTfvx+xwQRIQbxcOqWXQYJ8HX9OMojZqK1VLNc61GzyRiA\rZTvx/tWYM2BciWTeB2GfOH66gRDLAgMBAAECgYBp4qTvoJKynuT3SbDJY/XwaEtm\ru768SF9P0GlXrtwYuDWjAVue0VhBI9WxMWZTaVafkcP8hxX4QZqPh84td0zjcq3j\rDLOegAFJkIorGzq5FyK7ydBoU1TLjFV459c8dTZMTu+LgsOTD11/V/Jr4NJxIudo\rMBQ3c4cHmOoYv4uzkQJBANR+7Fc3e6oZgqTOesqPSPqljbsdF9E4x4eDFuOecCkJ\rDvVLOOoAzvtHfAiUp+H3fk4hXRpALiNBEHiIdhIuX2UCQQDCCHiPHFd4gC58yyCM\r6Leqkmoa+6YpfRb3oxykLBXcWx7DtbX+ayKy5OQmnkEG+MW8XB8wAdiUl0/tb6cQ\rFaRvAkBhvP94Hk0DMDinFVHlWYJ3xy4pongSA8vCyMj+aSGtvjzjFnZXK4gIjBjA\r2Z9ekDfIOBBawqp2DLdGuX2VXz8BAkByMuIh+KBSv76cnEDwLhfLQJlKgEnvqTvX\rTB0TUw8avlaBAXW34/5sI+NUB1hmbgyTK/T/IFcEPXpBWLGO+e3pAkAGWLpnH0Zh\rFae7oAqkMAd3xCNY6ec180tAe57hZ6kS+SYLKwb4gGzYaCxc22vMtYksXHtUeamo\r1NMLzI2ZfUoX\r";
  private RSAPrivateKey privateKey;
  private RSAPublicKey publicKey;
  private static final char[] HEX_CHAR = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

  public RSAPrivateKey getPrivateKey()
  {
    return this.privateKey;
  }

  public RSAPublicKey getPublicKey()
  {
    return this.publicKey;
  }

  public void genKeyPair()
  {
    KeyPairGenerator keyPairGen = null;
    try {
      keyPairGen = KeyPairGenerator.getInstance("RSA");
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    KeyPair keyPair =  null;
    if(keyPairGen!=null){
    	keyPairGen.initialize(1024, new SecureRandom());
    	keyPair = keyPairGen.generateKeyPair();
    }
    this.privateKey = (keyPair!=null ?  ((RSAPrivateKey)keyPair.getPrivate()):null);
    this.publicKey = (keyPair!=null? ((RSAPublicKey)keyPair.getPublic()):null);
  }

  public void loadPublicKey(InputStream in)
    throws Exception
  {
    try
    {
      BufferedReader br = new BufferedReader(new InputStreamReader(in));
      String readLine = null;
      StringBuilder sb = new StringBuilder();
      while ((readLine = br.readLine()) != null) {
        if (readLine.charAt(0) != '-')
        {
          sb.append(readLine);
          sb.append('\r');
        }
      }
      loadPublicKey(sb.toString());
    } catch (IOException e) {
      throw new Exception("公钥数据流读取错误");
    } catch (NullPointerException e) {
      throw new Exception("公钥输入流为空");
    }
  }

  public void loadPublicKey(String publicKeyStr)
    throws Exception
  {
    try
    {
      BASE64Decoder base64Decoder = new BASE64Decoder();
      byte[] buffer = base64Decoder.decodeBuffer(publicKeyStr);
      KeyFactory keyFactory = KeyFactory.getInstance("RSA");
      X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
      this.publicKey = ((RSAPublicKey)keyFactory.generatePublic(keySpec));
    } catch (NoSuchAlgorithmException e) {
      throw new Exception("无此算法");
    } catch (InvalidKeySpecException e) {
      throw new Exception("公钥非法");
    } catch (IOException e) {
      throw new Exception("公钥数据内容读取错误");
    } catch (NullPointerException e) {
      throw new Exception("公钥数据为空");
    }
  }

  public void loadPrivateKey(InputStream in)
    throws Exception
  {
    try
    {
      BufferedReader br = new BufferedReader(new InputStreamReader(in));
      String readLine = null;
      StringBuilder sb = new StringBuilder();
      while ((readLine = br.readLine()) != null) {
        if (readLine.charAt(0) != '-')
        {
          sb.append(readLine);
          sb.append('\r');
        }
      }
      loadPrivateKey(sb.toString());
    } catch (IOException e) {
      throw new Exception("私钥数据读取错误");
    } catch (NullPointerException e) {
      throw new Exception("私钥输入流为空");
    }
  }

  public void loadPrivateKey(String privateKeyStr) throws Exception {
    try {
      BASE64Decoder base64Decoder = new BASE64Decoder();
      byte[] buffer = base64Decoder.decodeBuffer(privateKeyStr);
      PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
      KeyFactory keyFactory = KeyFactory.getInstance("RSA");
      this.privateKey = ((RSAPrivateKey)keyFactory.generatePrivate(keySpec));
    } catch (NoSuchAlgorithmException e) {
      throw new Exception("无此算法");
    } catch (InvalidKeySpecException e) {
      throw new Exception("私钥非法");
    } catch (IOException e) {
      throw new Exception("私钥数据内容读取错误");
    } catch (NullPointerException e) {
      throw new Exception("私钥数据为空");
    }
  }

  public byte[] encrypt(RSAPublicKey publicKey, byte[] plainTextData)
    throws Exception
  {
    if (publicKey == null) {
      throw new Exception("加密公钥为空, 请设置");
    }
    Cipher cipher = null;
    try {
      cipher = Cipher.getInstance("RSA", new BouncyCastleProvider());
      cipher.init(1, publicKey);
      return cipher.doFinal(plainTextData);
    }
    catch (NoSuchAlgorithmException e) {
      throw new Exception("无此加密算法");
    } catch (NoSuchPaddingException e) {
      e.printStackTrace();
      return null;
    } catch (InvalidKeyException e) {
      throw new Exception("加密公钥非法,请检查");
    } catch (IllegalBlockSizeException e) {
      throw new Exception("明文长度非法"); } catch (BadPaddingException e) {
    }
    throw new Exception("明文数据已损坏");
  }

  public byte[] decrypt(RSAPrivateKey privateKey, byte[] cipherData)
    throws Exception
  {
    if (privateKey == null) {
      throw new Exception("解密私钥为空, 请设置");
    }
    Cipher cipher = null;
    try {
      cipher = Cipher.getInstance("RSA", new BouncyCastleProvider());
      cipher.init(2, privateKey);
      return cipher.doFinal(cipherData);
    }
    catch (NoSuchAlgorithmException e) {
      throw new Exception("无此解密算法");
    } catch (NoSuchPaddingException e) {
      e.printStackTrace();
      return null;
    } catch (InvalidKeyException e) {
      throw new Exception("解密私钥非法,请检查");
    } catch (IllegalBlockSizeException e) {
      throw new Exception("密文长度非法"); } catch (BadPaddingException e) {
    }
    throw new Exception("密文数据已损坏");
  }

  public static String byteArrayToString(byte[] data)
  {
    StringBuilder stringBuilder = new StringBuilder();
    for (int i = 0; i < data.length; i++)
    {
      stringBuilder.append(HEX_CHAR[((data[i] & 0xF0) >>> 4)]);

      stringBuilder.append(HEX_CHAR[(data[i] & 0xF)]);
      if (i < data.length - 1) {
        stringBuilder.append(' ');
      }
    }
    return stringBuilder.toString();
  }

  public static void main(String[] args)
  {
    RSAEncrypt rsaEncrypt = new RSAEncrypt();
    try
    {
      rsaEncrypt.loadPublicKey("MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQChDzcjw/rWgFwnxunbKp7/4e8w\r/UmXx2jk6qEEn69t6N2R1i/LmcyDT1xr/T2AHGOiXNQ5V8W4iCaaeNawi7aJaRht\rVx1uOH/2U378fscEESEG8XDqll0GCfB1/TjKI2aitVSzXOtRs8kYgGU78f7VmDNg\rXIlk3gdhnzh+uoEQywIDAQAB\r");
      //System.out.println("加载公钥成功");
    } catch (Exception e) {
      System.err.println(e.getMessage());
      System.err.println("加载公钥失败");
    }

    try
    {
      rsaEncrypt.loadPrivateKey("MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAKEPNyPD+taAXCfG\r6dsqnv/h7zD9SZfHaOTqoQSfr23o3ZHWL8uZzINPXGv9PYAcY6Jc1DlXxbiIJpp4\r1rCLtolpGG1XHW44f/ZTfvx+xwQRIQbxcOqWXQYJ8HX9OMojZqK1VLNc61GzyRiA\rZTvx/tWYM2BciWTeB2GfOH66gRDLAgMBAAECgYBp4qTvoJKynuT3SbDJY/XwaEtm\ru768SF9P0GlXrtwYuDWjAVue0VhBI9WxMWZTaVafkcP8hxX4QZqPh84td0zjcq3j\rDLOegAFJkIorGzq5FyK7ydBoU1TLjFV459c8dTZMTu+LgsOTD11/V/Jr4NJxIudo\rMBQ3c4cHmOoYv4uzkQJBANR+7Fc3e6oZgqTOesqPSPqljbsdF9E4x4eDFuOecCkJ\rDvVLOOoAzvtHfAiUp+H3fk4hXRpALiNBEHiIdhIuX2UCQQDCCHiPHFd4gC58yyCM\r6Leqkmoa+6YpfRb3oxykLBXcWx7DtbX+ayKy5OQmnkEG+MW8XB8wAdiUl0/tb6cQ\rFaRvAkBhvP94Hk0DMDinFVHlWYJ3xy4pongSA8vCyMj+aSGtvjzjFnZXK4gIjBjA\r2Z9ekDfIOBBawqp2DLdGuX2VXz8BAkByMuIh+KBSv76cnEDwLhfLQJlKgEnvqTvX\rTB0TUw8avlaBAXW34/5sI+NUB1hmbgyTK/T/IFcEPXpBWLGO+e3pAkAGWLpnH0Zh\rFae7oAqkMAd3xCNY6ec180tAe57hZ6kS+SYLKwb4gGzYaCxc22vMtYksXHtUeamo\r1NMLzI2ZfUoX\r");
      //System.out.println("加载私钥成功");
    } catch (Exception e) {
      System.err.println(e.getMessage());
      System.err.println("加载私钥失败");
    }

    String encryptStr = "Test String chaijunkun";
    try
    {
      byte[] cipher = rsaEncrypt.encrypt(rsaEncrypt.getPublicKey(), encryptStr.getBytes());

      byte[] plainText = rsaEncrypt.decrypt(rsaEncrypt.getPrivateKey(), cipher);
      //System.out.println("密文长度:" + cipher.length);
      //System.out.println(byteArrayToString(cipher));
      //System.out.println("明文长度:" + plainText.length);
      //System.out.println(byteArrayToString(plainText));
      //System.out.println(new String(plainText));
    } catch (Exception e) {
      System.err.println(e.getMessage());
    }
  }

  public static String iosdecrypt(String enCryptStr)
    throws Exception
  {
    BASE64Decoder base64Decoder = new BASE64Decoder();
    byte[] buffer = base64Decoder.decodeBuffer(enCryptStr);

    buffer = iosdecrypt(loadIOSPrivateKey(), buffer);
    return new String(buffer);
  }

  public static byte[] iosdecrypt(PrivateKey privateKey, byte[] cipherData) throws Exception {
    if (privateKey == null) {
      throw new Exception("解密私钥为空, 请设置");
    }
    Cipher cipher = null;
    try {
      cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", new BouncyCastleProvider());
      cipher.init(2, privateKey);
      return cipher.doFinal(cipherData);
    } catch (Exception e) {
    }
    throw new Exception("无此解密算法");
  }

  public static RSAPrivateKey loadIOSPrivateKey() throws Exception {
    try {
      BASE64Decoder base64Decoder = new BASE64Decoder();
      byte[] buffer = base64Decoder.decodeBuffer(iosPrivave_key);
      PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
      KeyFactory keyFactory = KeyFactory.getInstance("RSA");
      return (RSAPrivateKey)keyFactory.generatePrivate(keySpec); } catch (Exception e) {
    }
    throw new Exception("无此算法");
  }
}