package com.dzf.zxkj.jackson.serializer;

import com.dzf.zxkj.custom.type.DZFDate;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

/**
 * @Auther: dandelion
 * @Date: 2019-09-09
 * @Description:
 */
public class DZFDateSerializer extends StdSerializer<DZFDate> {

    public DZFDateSerializer() {
        super(DZFDate.class);
    }

    @Override
    public void serialize(DZFDate value, JsonGenerator jsonGenerator, SerializerProvider provider) throws IOException {
        jsonGenerator.writeString(value.toString());
    }
}
