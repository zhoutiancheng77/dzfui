package com.dzf.zxkj.app.utils;

import com.dzf.zxkj.common.utils.BeanHelper;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.lang.reflect.Field;
import java.util.Map;

public class AppkeyUtil {

    public static void setAppValue(Map<String,Object> map,Object svo){
        for (Field field : svo.getClass().getDeclaredFields())
        {
            JsonProperty annotation = field.getAnnotation(JsonProperty.class);
            String fieldkey = field.getName();
            if (null != annotation)
            {
                String ankey = annotation.value();
                Object obj = map.get(ankey);
                if(obj==null) continue;
                BeanHelper.setProperty(svo, fieldkey, obj);
            }else {
                Object obj = map.get(fieldkey);
                if(obj==null) continue;
                BeanHelper.setProperty(svo, fieldkey ,map.get(fieldkey));
            }
        }
    }
}
