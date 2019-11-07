package com.dzf.zxkj.platform.processor;

import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.common.utils.ArrayUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.ServletModelAttributeMethodProcessor;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RenamingProcessor extends ServletModelAttributeMethodProcessor{

    private RequestMappingHandlerAdapter requestMappingHandlerAdapter;

    //Rename cache
    private final Map<String, Map<String, String>> replaceMap = new ConcurrentHashMap<>();

    public RenamingProcessor(boolean annotationNotRequired) {
        super(annotationNotRequired);
    }

    @Override
    protected void bindRequestParameters(WebDataBinder binder, NativeWebRequest nativeWebRequest) {
        Object target = binder.getTarget();

        if(requestMappingHandlerAdapter == null){
            requestMappingHandlerAdapter = SpringUtils.getBean(RequestMappingHandlerAdapter.class);
        }

        Class<?> targetClass = target.getClass();
        if (!replaceMap.containsKey(targetClass.getName())) {
            Map<String, String> mapping = analyzeClass(targetClass);
            replaceMap.put(targetClass.getName(), mapping);
        }
        Map<String, String> mapping = replaceMap.get(targetClass.getName());
        ParamNameDataBinder paramNameDataBinder = new ParamNameDataBinder(target, binder.getObjectName(), mapping);
        requestMappingHandlerAdapter.getWebBindingInitializer().initBinder(paramNameDataBinder, nativeWebRequest);
        super.bindRequestParameters(paramNameDataBinder, nativeWebRequest);
    }

    private static Field[] getAllField(Class<?> targetClass){
        Field[] fields = targetClass.getDeclaredFields();
        Class clazz = targetClass.getSuperclass();
        if(clazz != null){
            return ArrayUtil.mergeArray(fields, clazz.getDeclaredFields());
        }
        return fields;
    }

    private static Map<String, String> analyzeClass(Class<?> targetClass) {
        Field[] fields = getAllField(targetClass);

        Map<String, String> renameMap = new HashMap<String, String>();
        for (Field field : fields) {
            JsonProperty paramNameAnnotation = field.getAnnotation(JsonProperty.class);
            if (paramNameAnnotation != null && !paramNameAnnotation.value().isEmpty()) {
                renameMap.put(paramNameAnnotation.value(), field.getName());
            }
        }
        if (renameMap.isEmpty()) return Collections.emptyMap();
        return renameMap;
    }
}
