package com.dzf.zxkj.platform.util.taxrpt.shandong.deal;

import com.dzf.zxkj.base.exception.BusinessException;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 
 * 山东中税参数
 */
public class TaxParamUtils {

	public static String TESTTAXURL = null;// 测试地址
	public static String PROTAXURL = null;// 正式地址
	public static String CLIENTNO = null;// 客户端号
	public static String APPLICATIONID = null;//
	public static String SUPPLIER = null;// 大账房账号
	public static String PASSWORD = null;// 大账房密码
	public static String ENABLED = null;// 大账房密码
	public static String ISTEST = null;// 是否测试环境
	static {
		Properties prop = new Properties();
		// 读取属性文件jedis_config.properties
		InputStream input = TaxParamUtils.class.getResourceAsStream("/properties/sdtaxconfig.properties");
		try {
			if (input == null) {
				throw new BusinessException("丢失配置文件!");
			} else {
				prop.load(input);
				// / 加载属性列表
				CLIENTNO = prop.getProperty("clientno");
				APPLICATIONID = prop.getProperty("applicationid");
				SUPPLIER = prop.getProperty("supplier");
				PASSWORD = prop.getProperty("password");
				ENABLED = prop.getProperty("enabled");
				TESTTAXURL = prop.getProperty("testtaxurl");
				PROTAXURL = prop.getProperty("protaxurl");
				ISTEST = prop.getProperty("istest");
			}
		} catch (IOException e) {
			throw new BusinessException("读取配置文件失败!");
		} catch (Exception e) {
			throw new BusinessException("读取配置文件失败!");
		}finally{
			IOUtils.closeQuietly(input);
		}
	}
}
