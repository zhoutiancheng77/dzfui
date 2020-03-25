package com.dzf.zxkj.base.utils;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.dzf.zxkj.common.lang.DZFDateTime;

import java.lang.reflect.Type;

public class DZFDateTimeDeserializer implements ObjectDeserializer {

    public final static DZFDateTimeDeserializer instance = new DZFDateTimeDeserializer();

    @Override
    public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {

        Object o = parser.parse();
        if(o == null){
            return null;
        }

        return (T) new DZFDateTime((String) o) ;
    }

    @Override
    public int getFastMatchToken() {
        return 0;
    }
}
