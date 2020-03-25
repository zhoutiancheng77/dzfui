package com.dzf.zxkj.base.utils;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.dzf.zxkj.common.lang.DZFDate;

import java.lang.reflect.Type;

public class DZFDateDeserializer implements ObjectDeserializer {

    public final static DZFDateDeserializer instance = new DZFDateDeserializer();

    @Override
    public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {

        Object o = parser.parse();

        return (T) new DZFDate((String)o) ;
    }

    @Override
    public int getFastMatchToken() {
        return 0;
    }
}
