//package com.dzf.zxkj.platform.util.taxrpt.cqtc.deal;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.Properties;
//
///**
// * 1、会计公司pk 2、可访问的IP
// *
// * @author mfz
// *
// */
//public class CqtcPropertyUtils {
//
//	public static Properties getProperties() {
//		Properties property = new Properties();
//		InputStream inputStream = null;
//		try {
//			inputStream = CqtcPropertyUtils.class.getClassLoader().getResourceAsStream("properties/cqtcconfig.properties");
//			property.load(inputStream);
//		} catch (IOException e1) {
//		} finally {
//			try {
//				if(inputStream != null)
//					inputStream.close();
//			} catch (IOException e) {
//			}
//		}
//		return property;
//	}
//
//}
