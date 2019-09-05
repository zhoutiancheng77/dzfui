package com.dzf.zxkj.mybatis.converter;

import com.dzf.zxkj.common.lang.DZFDateTime;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

/**
 * @Auther: dandelion
 * @Date: 2019-09-03
 * @Description:
 */
public class DZFDateTimeConverter extends StdSerializer<DZFDateTime> {
    public DZFDateTimeConverter() {
        super(DZFDateTime.class);
    }

    @Override
    public void serialize(DZFDateTime dzfDateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (dzfDateTime != null) {
            jsonGenerator.writeString(dzfDateTime.toString());
        }
    }
}
