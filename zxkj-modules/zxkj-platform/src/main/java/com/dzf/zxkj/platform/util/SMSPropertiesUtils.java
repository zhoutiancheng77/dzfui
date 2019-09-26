package com.dzf.zxkj.platform.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * SMSPropertiesUtils
 * 
 * @author QX 2016-09-15
 *
 */
public class SMSPropertiesUtils {

	public static Properties getProperties() {

		Properties p = new Properties();
		InputStream inputStream = null;
		try {
			inputStream = SMSPropertiesUtils.class.getClassLoader().getResourceAsStream("config.properties");

			p.load(inputStream);

		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return p;
	}

}
