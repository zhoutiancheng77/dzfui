package com.dzf.zxkj.app.utils;


public interface ObjectConstants {

	byte UF_BOOLEAN = (byte) 0xf1;

	byte UF_DATE = (byte) 0xf2;

	byte UF_DATETIME = (byte) 0xf3;

	byte UF_DOUBLE = (byte) 0xf4;

	byte UF_TIME = (byte) 0xf5;

	byte UF_END = (byte) 0xfe;

	int UF_BOOLEAN_LEN = 3;

	int UF_DATE_LEN = 10;

	int UF_DATETIME_LEN = 10;

	int UF_DOUBLE_LEN = 24;

	int UF_TIME_LEN = 10;

	byte[] TRUE = new byte[] { UF_BOOLEAN, 1, UF_END };

	byte[] FALSE = new byte[] { UF_BOOLEAN, 0, UF_END };
}
