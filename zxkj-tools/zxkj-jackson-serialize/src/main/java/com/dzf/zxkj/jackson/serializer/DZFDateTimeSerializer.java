package com.dzf.zxkj.jackson.serializer;

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
public class DZFDateTimeSerializer extends StdSerializer<DZFDateTime> {
    public DZFDateTimeSerializer() {
        super(DZFDateTime.class);
    }

    @Override
    public void serialize(DZFDateTime dzfDateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (dzfDateTime != null) {
            jsonGenerator.writeString(dzfDateTime.toString());
        }
    }
}
