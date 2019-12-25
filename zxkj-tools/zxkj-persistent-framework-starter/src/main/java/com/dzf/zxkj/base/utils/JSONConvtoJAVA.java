package com.dzf.zxkj.base.utils;

import com.alibaba.fastjson.parser.ParserConfig;
import com.dzf.zxkj.common.lang.DZFDouble;


public class JSONConvtoJAVA {
	
	
	public static ParserConfig  getParserConfig(){
		ParserConfig config = ParserConfig.getGlobalInstance();
		config.getDerializers().put(DZFDouble.class, DZFDoubleDeserializer.instance);
		return config;
	}
	
}
