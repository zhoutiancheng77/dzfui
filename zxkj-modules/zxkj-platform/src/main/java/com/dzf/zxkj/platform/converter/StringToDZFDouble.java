package com.dzf.zxkj.platform.converter;

import com.dzf.zxkj.common.lang.DZFDouble;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToDZFDouble implements Converter<String, DZFDouble> {
    @Override
    public DZFDouble convert(String source) {
        return new DZFDouble(source);
    }
}
