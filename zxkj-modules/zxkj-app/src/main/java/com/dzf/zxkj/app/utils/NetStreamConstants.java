package com.dzf.zxkj.app.utils;



public class NetStreamConstants {

	//public static final int NC_STREAM_MAGIC = 0x897172;

	public static final DES des = new DES(232);

	public static final int NC_STREAM_BUFFER_SIZE = 1024;

	public final static byte[] NC_STREAM_HEADER = { (byte) 154,
			(byte) -126, (byte)143};

	/**
	 * des end code
	 */
	public static final byte ENDEDCODE = (byte) 100;

	public static boolean STREAM_NEED_COMPRESS = true;//"true".equals(System
//			.getProperty("nc.stream.compress"));

	public static boolean STREAM_NEED_ENCRYPTED =true;// "true".equals(System
//			.getProperty("nc.stream.encrypted"));

	public static boolean STREAM_AUTO_ADAPT =false;// "true".equals(System
//			.getProperty("nc.stream.autoAdapt"));

	public static boolean STREAM_NEED_STATISTIC =false;// "true".equals(System
//			.getProperty("nc.stream.statistic"));

}
