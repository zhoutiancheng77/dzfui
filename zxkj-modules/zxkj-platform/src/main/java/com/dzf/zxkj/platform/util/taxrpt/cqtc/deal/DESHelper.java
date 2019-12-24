package com.dzf.zxkj.platform.util.taxrpt.cqtc.deal;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.io.IOException;

public class DESHelper {

	public DESHelper() {
	}

	private byte[] desEncrypt(byte[] plainText, byte[] desKey) throws Exception {
		byte rawKeyData[] = desKey;
		DESKeySpec dks = new DESKeySpec(rawKeyData);
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey key = keyFactory.generateSecret(dks);
		Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(desKey));
		byte data[] = plainText;
		byte encryptedData[] = cipher.doFinal(data);
		return encryptedData;
	}

	private byte[] desDecrypt(byte[] encryptText, byte[] desKey) throws Exception {
		byte rawKeyData[] = desKey;
		DESKeySpec dks = new DESKeySpec(rawKeyData);
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey key = keyFactory.generateSecret(dks);
		Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(desKey));
		byte encryptedData[] = encryptText;
		byte decryptedData[] = cipher.doFinal(encryptedData);
		return decryptedData;
	}

	public String encrypt(String input, byte[] desKey) throws Exception {
		return base64Encode(desEncrypt(input.getBytes(), desKey));
	}

	public String decrypt(String input, byte[] desKey) throws Exception {
		byte[] result = base64Decode(input);
		return new String(desDecrypt(result, desKey));
	}

	private static String base64Encode(byte[] s) {
		if (s == null)
			return null;
		BASE64Encoder b = new BASE64Encoder();
		return b.encode(s);
	}

	private static byte[] base64Decode(String s) throws IOException {
		if (s == null)
			return null;
		BASE64Decoder decoder = new BASE64Decoder();
		byte[] b = decoder.decodeBuffer(s);
		return b;
	}

}