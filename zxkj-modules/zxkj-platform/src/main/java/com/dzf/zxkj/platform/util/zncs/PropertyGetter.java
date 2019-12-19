package com.dzf.zxkj.platform.util.zncs;

import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.platform.config.ZncsUrlConfig;

public class PropertyGetter {

	public static String inv_ip = null;// 聂老师网站地址
	public static String webrow = null;// 清单总行数
//	public static String ocr_sercet = null;// 文通密钥 -----弃用
//	public static String ocr_libpath = null;// 文通软件路径 -----弃用
//	public static String ocr_imagepath = null;// 图片路径
//	public static String ocr_model = null;// 文通模板地址 -----弃用
//	public static String area_code = null;// 聂老师网站地区代码 -----弃用
//	public static String interface_way = null;// 接口方式 现在只支持聂老师网站
//	public static String ptarea_code = null;// 票通地区代码 -----弃用
//	public static String webprefix = null;// 聂老师网站开头前缀
//	public static String dzfkjpicurl = null;// 调用在线会计地址 -----弃用
	// public static String ismulthread = null;
//	public static String istest = null;// 是否测试

	static {
				ZncsUrlConfig zncsUrlConfig = (ZncsUrlConfig) SpringUtils.getBean(ZncsUrlConfig.class);
				// / 加载属性列表
				inv_ip = zncsUrlConfig.inv_ip;
				webrow = zncsUrlConfig.webrow;
//				ocr_sercet = prop.getProperty("ocr_sercet");
//				ocr_libpath = prop.getProperty("ocr_libpath");
//				ocr_imagepath = prop.getProperty("ocr_imagepath");
//				ocr_model = prop.getProperty("ocr_model");
//				area_code = prop.getProperty("area_code");
//				interface_way = prop.getProperty("interface_way");
//				ptarea_code = prop.getProperty("ptarea_code");
//				webprefix = prop.getProperty("webprefix");
//				dzfkjpicurl = prop.getProperty("dzfocrimage");
				// ismulthread = prop.getProperty("ismulthread");
//				istest = prop.getProperty("istest");
			}

	}


