//package com.dzf.zxkj.platform.util.taxrpt.jiangsu;
//
//import lombok.extern.slf4j.Slf4j;
//
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Map.Entry;
//import java.util.Properties;
//@Slf4j
//public class JSTaxPropUtil {
////	private static Logger log = Logger.getLogger(JSTaxPropUtil.class);
//
//	private static Map<String, String> props = new HashMap<String, String>();
//
//	static {
//		Properties pros = new Properties();
//		try {
//			pros.load(JSTaxPropUtil.class.getClassLoader().getResourceAsStream(
//					"properties/jiangsutax.properties"));
//		} catch (IOException e) {
//			log.error("读取江苏报税配置文件出错", e);
//		}
//		for (Entry<Object, Object> entry : pros.entrySet()) {
//			props.put((String) entry.getKey(), (String) entry.getValue());
//		}
//	}
//
//	public static String getprop(String key) {
//		return props.get(key);
//	}
//}
