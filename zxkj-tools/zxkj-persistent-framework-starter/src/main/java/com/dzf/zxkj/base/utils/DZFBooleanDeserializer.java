package com.dzf.zxkj.base.utils;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.dzf.zxkj.common.lang.DZFBoolean;

import java.lang.reflect.Type;

public class DZFBooleanDeserializer implements ObjectDeserializer {

    public final static DZFBooleanDeserializer instance = new DZFBooleanDeserializer();

    @Override
    public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {

        Object o = parser.parse();
        if(o == null){
            return null;
        }

        if(o instanceof String){
            return (T)new DZFBoolean((String)o);
        }

        return (T) new DZFBoolean((Boolean)o) ;
    }

    @Override
    public int getFastMatchToken() {
        return 0;
    }
}
