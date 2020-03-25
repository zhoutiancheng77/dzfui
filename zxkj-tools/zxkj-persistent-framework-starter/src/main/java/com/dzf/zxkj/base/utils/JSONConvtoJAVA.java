package com.dzf.zxkj.base.utils;

import com.alibaba.fastjson.parser.ParserConfig;
import com.dzf.zxkj.common.lang.*;


public class JSONConvtoJAVA {
	
	
	public static ParserConfig  getParserConfig(){
		ParserConfig config = ParserConfig.getGlobalInstance();
		config.getDeserializers().put(DZFDouble.class, DZFDoubleDeserializer.instance);
		config.getDeserializers().put(DZFBoolean.class, DZFBooleanDeserializer.instance);
		config.getDeserializers().put(DZFDate.class, DZFDateDeserializer.instance);
		config.getDeserializers().put(DZFDateTime.class, DZFDateTimeDeserializer.instance);
		config.getDeserializers().put(DZFTime.class, DZFTimeDeserializer.instance);
		return config;
	}
	
}
