package com.dzf.zxkj.app.utils;

import com.dzf.zxkj.common.utils.BeanHelper;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.lang.reflect.Field;
import java.util.Map;

public class AppkeyUtil {
    //支持多层继承,,需要从父级去获取字段设值
    public static void setMulAppValue(Map<String,Object> map,Object svo,Class[] classes){
        for (int i = 0; i < classes.length; i++) {
            setAppValue(map,svo,classes[i]);
        }
    }

    //只有一级的时候,,如果有多继承的要考虑多个class,getDeclaredFields只反射当前级次
    public static void setAppValue(Map<String,Object> map,Object svo,Class classname){
        for (Field field :classname.getDeclaredFields())
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


    //只支持一级的时候,,如果有多继承的要考虑多个class,getDeclaredFields只反射当前级次
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
