package com.dzf.zxkj.base.utils;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.dzf.zxkj.common.lang.DZFTime;

import java.lang.reflect.Type;

public class DZFTimeDeserializer implements ObjectDeserializer {

    public final static DZFTimeDeserializer instance = new DZFTimeDeserializer();

    @Override
    public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {

        Object o = parser.parse();

        if(o == null){
            return null;
        }

        if(o instanceof String){
            return (T)new DZFTime((String)o);
        }
        if(o instanceof java.sql.Date){
            return (T)new DZFTime((java.sql.Date)o);
        }
        if(o instanceof java.util.Date){
            return (T)new DZFTime((java.util.Date)o);
        }
        if(o instanceof Long){
            return (T)new DZFTime((Long)o);
        }

        return null ;
    }

    @Override
    public int getFastMatchToken() {
        return 0;
    }
}
