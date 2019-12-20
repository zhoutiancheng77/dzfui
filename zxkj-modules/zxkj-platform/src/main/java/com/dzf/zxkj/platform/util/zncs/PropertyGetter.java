package com.dzf.zxkj.platform.util.zncs;

import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.utils.SpringUtils;
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
	public static String ptjx_url = null;
	public static String fpcy_fpcyurl = null;
	public static String fpcy_ocrurl = null;
	public static String fpcy_busiurl = null;
	public static String fpcy_authurl = null;
	public static String fpcy_version = null;
	public static String fpcy_appKey = null;
	public static String fpcy_appSecret = null;
	public static String fpcy_uid = null;
	public static String fpcy_unzip = null;
	public static String fpcy_unencry = null;
	public static String fpcy_endes = null;
	public static String fpcy_enca = null;
	public static String fpcy_codeType = null;
	public static String fpcy_standardtime = null;
	public static String fpcy_regpwd = null;
	public static String fpcy_pagesize = null;
	public static String fpcy_randnum = null;
	public static String cft_busiurl = null;
	public static String cft_getcorpnameurl = null;
	public static String cft_appId = null;
	public static String cft_version = null;
	public static String cft_randnum = null;
	public static String cft_regpwd = null;
	public static String cft_encryptCode = null;
	public static String ptb_ptxxurl = null;
	public static String ptb_xxptbm = null;
	public static String ptb_platformCode = null;
	public static String ptb_signType = null;
	public static String ptb_format = null;
	public static String ptb_xxversion = null;
	public static String ptb_xxpwd = null;
	public static String ptb_xxsize = null;
	public static String ptb_privateKey = null;
	public static String ptb_publicKey = null;
	static {
		InputStream is = null;
		Properties prop = new Properties();
		try {
			Resource exportTemplate = new ClassPathResource("properties"+ File.separator+"zncsConfig.properties");
			is = exportTemplate.getInputStream();
			prop.load(is);
			// / 加载属性列表
			inv_ip = prop.getProperty("ocr_inv_ip");
			webrow = prop.getProperty("ocr_webrow");
			ptjx_url = prop.getProperty("ptjx_url");
			} catch (IOException e) {
				e.printStackTrace();
				}
		}
}


