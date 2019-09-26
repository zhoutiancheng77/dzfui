package com.dzf.zxkj.common.utils;


public class Encode
{

public String decode(String s)
{
    if(s == null)
        return null;
	String res = "";
	DES des = new DES(getKey());
	byte[] sBytes = s.getBytes();
	for (int i = 0; i < (sBytes.length / 16); i++)
	{
		byte[] theBytes = new byte[8];
		for (int j = 0; j <= 7; j++)
		{
			byte byte1 = (byte) (sBytes[16 * i + 2 * j] - 'a');
			byte byte2 = (byte) (sBytes[16 * i + 2 * j + 1] - 'a');
			theBytes[j] = (byte) (byte1 * 16 + byte2);
		}
		long x = des.bytes2long(theBytes);
		byte[] result = new byte[8];
		des.long2bytes(des.decrypt(x), result);
		res = res + (new String(result));
	}
	return res.trim();
}

public String encode(String s) {
    if(s == null)
        return null;
	String res = "";
	DES des = new DES(getKey());
	byte space = 0x20;
	byte[] sBytes = s.getBytes();
	int length = sBytes.length;
	int newLength = length + (8 - length % 8) % 8;
	byte[] newBytes = new byte[newLength];
	for (int i = 0; i < newLength; i++) {
		if (i <= length - 1) {
			newBytes[i] = sBytes[i];
		}
		else {
			newBytes[i] = space;
		}
	}
	for (int i = 0; i < (newLength / 8); i++) {
		byte[] theBytes = new byte[8];
		for (int j = 0; j <= 7; j++) {
			theBytes[j] = newBytes[8 * i + j];
		}
		long x = des.bytes2long(theBytes);
		byte[] result = new byte[8];
		des.long2bytes(des.encrypt(x), result);
		byte[] doubleResult = new byte[16];
		for (int j = 0; j < 8; j++) {
			doubleResult[2 * j] = (byte) (((((char) result[j]) & 0xF0) >> 4) + 'a');
			doubleResult[2 * j + 1] = (byte) ((((char) result[j]) & 0x0F) + 'a');
		}
		res = res + new String(doubleResult);
	}
	return res;
}

private static long getKey() {
	return 1231234234;
}
}