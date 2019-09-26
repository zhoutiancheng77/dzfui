package com.dzf.zxkj.platform.model.st;

import java.util.LinkedHashMap;
import java.util.Map;


/****
 *　页签　Class 对照
 * @author asoka
 *
 */
public class ReportTabClassMapping {
	
	//一般纳税人
	@SuppressWarnings({ "serial", "rawtypes" })
	public static Map<String,Class> normalTax = new LinkedHashMap<String, Class>(){{
		
				put("NMOTAX1", NmTaxTab1VO.class);//增 值 税 纳 税 申 报 表
				put("NMOTAX2", NmTaxTab2VO.class);//城建税、教育费附加、地方教育附加税（费）申报表
	 }
	};
	
	
	//文化事业建设费申报表
	@SuppressWarnings({ "serial", "rawtypes" })
	public static Map<String,Class> cltcTax = new LinkedHashMap<String, Class>(){{
		
				put("CLTCTAX1", CltcTaxTab1VO.class);//文化事业建设费申报表
				
	 }
	};

}
