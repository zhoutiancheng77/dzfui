package com.dzf.zxkj.jackson.serializer;

import com.dzf.zxkj.common.lang.DZFBoolean;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

/**
 * @Auther: dandelion
 * @Date: 2019-09-09
 * @Description:
 */
public class DZFBooleanSerializer extends StdSerializer<DZFBoolean> {

    public DZFBooleanSerializer() {
        super(DZFBoolean.class);
    }

    @Override
    public void serialize(DZFBoolean value, JsonGenerator jsonGenerator, SerializerProvider provider) throws IOException {
        if (value.booleanValue()) {
            jsonGenerator.writeString("是");
        } else {
            jsonGenerator.writeString("否");
        }
    }
}
