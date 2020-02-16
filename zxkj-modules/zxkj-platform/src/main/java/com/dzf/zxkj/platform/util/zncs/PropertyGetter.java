package com.dzf.zxkj.platform.util.zncs;

import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.platform.config.ZncsUrlConfig;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class PropertyGetter {

	public static String inv_ip = null;// 聂老师网站地址
	public static String webrow = null;// 清单总行数
	public static String ptjx_url = null; //票通进项
	static {
		ZncsUrlConfig zncsUrlConfig = (ZncsUrlConfig) SpringUtils.getBean(ZncsUrlConfig.class);
		// / 加载属性列表
		inv_ip = zncsUrlConfig.inv_ip;
		webrow = zncsUrlConfig.webrow;
		ptjx_url = zncsUrlConfig.ptjx_ptjxurl;
		}
}


