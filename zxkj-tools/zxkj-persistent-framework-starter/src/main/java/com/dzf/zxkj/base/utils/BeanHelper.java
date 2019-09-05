package com.dzf.zxkj.base.utils;

import org.apache.commons.beanutils.Converter;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BeanHelper {
    protected static final Object[] NULL_ARGUMENTS = new Object[0];

    private static Map<String, ReflectionInfo> cache = new ConcurrentHashMap();

    private static BeanHelper bhelp = new BeanHelper();

    public static BeanHelper getInstance() {
        return bhelp;
    }

    public static List<String> getPropertys(Object bean) {
        return Arrays.asList(getInstance().getPropertiesAry(bean));
    }

    public String[] getPropertiesAry(Object bean) {
        ReflectionInfo reflectionInfo = null;

        reflectionInfo = cachedReflectionInfo(bean.getClass());
        Set propertys = new HashSet();
        for (String key : reflectionInfo.writeMap.keySet()) {
            if (reflectionInfo.writeMap.get(key) != null) {
                propertys.add(key);
            }
        }
        return (String[]) propertys.toArray(new String[0]);
    }

    public static Object getProperty(Object bean, String propertyName) {
        try {
            Method method = getInstance().getMethod(bean, propertyName, false);
            if ((propertyName != null) && (method == null))
                return null;
            if (method == null) {
                return null;
            }
            return method.invoke(bean, NULL_ARGUMENTS);
        } catch (Exception e) {
            String errStr = new StringBuilder().append("Failed to get property: ").append(propertyName).toString();

            throw new RuntimeException(errStr, e);
        }
    }

    public static Object[] getPropertyValues(Object bean, String[] propertys) {
        Object[] result = new Object[propertys.length];
        try {
            Method[] methods = getInstance().getMethods(bean, propertys, false);
            for (int i = 0; i < propertys.length; i++)
                if ((propertys[i] == null) || (methods[i] == null))
                    result[i] = null;
                else
                    result[i] = methods[i].invoke(bean, NULL_ARGUMENTS);
        } catch (Exception e) {
            String errStr = new StringBuilder().append("Failed to get getPropertys from ").append(bean.getClass()).toString();

            throw new RuntimeException(errStr, e);
        }
        return result;
    }

    public static Method getMethod(Object bean, String propertyName) {
        return getInstance().getMethod(bean, propertyName, true);
    }

    public static Method getGetMethod(Object bean, String propertyName) {
        return getInstance().getMethod(bean, propertyName, false);
    }

    public static Method getSetMethod(Object bean, String propertyName) {
        return getInstance().getMethod(bean, propertyName, true);
    }

    public static Method[] getMethods(Object bean, String[] propertys) {
        return getInstance().getMethods(bean, propertys, true);
    }

    private Method[] getMethods(Object bean, String[] propertys, boolean isSetMethod) {
        Method[] methods = new Method[propertys.length];
        ReflectionInfo reflectionInfo = null;

        reflectionInfo = cachedReflectionInfo(bean.getClass());
        for (int i = 0; i < propertys.length; i++) {
            Method method = null;
            if (isSetMethod)
                method = reflectionInfo.getWriteMethod(propertys[i]);
            else {
                method = reflectionInfo.getReadMethod(propertys[i]);
            }
            methods[i] = method;
        }
        return methods;
    }

    private Method getMethod(Object bean, String propertyName, boolean isSetMethod) {
        Method method = null;
        ReflectionInfo reflectionInfo = null;

        reflectionInfo = cachedReflectionInfo(bean.getClass());
        if (isSetMethod)
            method = reflectionInfo.getWriteMethod(propertyName);
        else {
            method = reflectionInfo.getReadMethod(propertyName);
        }
        return method;
    }

    private ReflectionInfo cachedReflectionInfo(Class<?> beanCls) {
        return cacheReflectionInfo(beanCls, null);
    }

    private ReflectionInfo cacheReflectionInfo(Class<?> beanCls, List<PropDescriptor> pdescriptor) {
        String key = beanCls.getName();
        ReflectionInfo reflectionInfo = (ReflectionInfo) cache.get(key);
        if (reflectionInfo == null) {
            reflectionInfo = (ReflectionInfo) cache.get(key);
            if (reflectionInfo == null) {
                reflectionInfo = new ReflectionInfo();
                List<PropDescriptor> propDesc = new ArrayList<PropDescriptor>();
                if (pdescriptor != null)
                    propDesc.addAll(pdescriptor);
                else {
                    propDesc = getPropertyDescriptors(beanCls);
                }
                for (PropDescriptor pd : propDesc) {
                    Method readMethod = pd.getReadMethod(beanCls);
                    Method writeMethod = pd.getWriteMethod(beanCls);
                    if (readMethod != null) {
                        reflectionInfo.readMap.put(pd.getName().toLowerCase(), readMethod);
                    }

                    if (writeMethod != null) {
                        reflectionInfo.writeMap.put(pd.getName().toLowerCase(), writeMethod);
                    }
                }
                cache.put(key, reflectionInfo);
            }
        }
        return reflectionInfo;
    }

    public static void invokeMethod(Object bean, Method method, Object value) {
        try {
            if (method == null)
                return;
            Object[] arguments = {value};
            method.invoke(bean, arguments);
        } catch (Exception e) {
            String errStr = new StringBuilder().append("Failed to set property: ").append(method.getName()).toString();

            throw new RuntimeException(errStr, e);
        }
    }

    public static Object getPropertyValue(Class clazz, Object value) {
        try {

            Converter cr = BeanConvertor.getConVerter(clazz);
            value = cr.convert(clazz, value);
            return value;
        } catch (Exception e) {
            String errStr = new StringBuilder().append("Failed to set property: ").toString();

            throw new RuntimeException(errStr, e);
        }
    }

    public static void setProperty(Object bean, String propertyName, Object value) {
        try {
            Method method = getInstance().getMethod(bean, propertyName, true);
            if ((propertyName != null) && (method == null)) {
                return;
            }
            if (method == null) {
                return;
            }
            Converter cr = BeanConvertor.getConVerter(method.getParameterTypes()[0]);
            value = cr.convert(method.getParameterTypes()[0], value);
            method.invoke(bean, new Object[]{value});
        } catch (IllegalArgumentException e) {
            String errStr = new StringBuilder().append("Failed to set property: ").append(propertyName).append(" at bean: ").append(bean.getClass().getName()).append(" with value:").append(value).append(" type:").append(value == null ? "null" : value.getClass().getName()).toString();

            throw new IllegalArgumentException(errStr, e);
        } catch (Exception e) {
            String errStr = new StringBuilder().append("Failed to set property: ").append(propertyName).append(" at bean: ").append(bean.getClass().getName()).append(" with value:").append(value).toString();

            throw new RuntimeException(errStr, e);
        }
    }

    public Method[] getAllGetMethod(Class<?> beanCls, String[] fieldNames) {
        Method[] methods = null;
        ReflectionInfo reflectionInfo = null;
        List al = new ArrayList();
        reflectionInfo = cachedReflectionInfo(beanCls);
        for (String str : fieldNames) {
            al.add(reflectionInfo.getReadMethod(str));
        }
        methods = (Method[]) al.toArray(new Method[al.size()]);
        return methods;
    }

    private List<PropDescriptor> getPropertyDescriptors(Class<?> clazz) {
        List descList = new ArrayList();
        List superDescList = new ArrayList();
        List propsList = new ArrayList();
        Class propType = null;
        for (Method method : clazz.getDeclaredMethods())
            if (method.getName().length() >= 4) {
                if ((method.getName().charAt(3) >= 'A') && (method.getName().charAt(3) <= 'Z')) {
                    if (method.getName().startsWith("set")) {
                        if (method.getParameterTypes().length != 1) {
                            continue;
                        }
                        if (method.getReturnType() != Void.TYPE) {
                            continue;
                        }
                        propType = method.getParameterTypes()[0];
                    } else {
                        if ((!method.getName().startsWith("get")) ||
                                (method.getParameterTypes().length != 0)) {
                            continue;
                        }
                        propType = method.getReturnType();
                    }

                    String propname = method.getName().substring(3, 4).toLowerCase();
                    if (method.getName().length() > 4) {
                        propname = new StringBuilder().append(propname).append(method.getName().substring(4)).toString();
                    }
                    if (!propname.equals("class")) {
                        if (!propsList.contains(propname)) {
                            propsList.add(propname);

                            descList.add(new PropDescriptor(clazz, propType, propname));
                        }
                    }
                }
            }
        Class superClazz = clazz.getSuperclass();
        if (superClazz != null) {
            superDescList = getPropertyDescriptors(superClazz);
            descList.addAll(superDescList);
            if (!isBeanCached(superClazz)) {
                cacheReflectionInfo(superClazz, superDescList);
            }
        }
        return descList;
    }

    private boolean isBeanCached(Class<?> bean) {
        String key = bean.getName();
        ReflectionInfo cMethod = (ReflectionInfo) cache.get(key);
        if (cMethod == null) {
            cMethod = (ReflectionInfo) cache.get(key);
            if (cMethod == null) {
                return false;
            }
        }
        return true;
    }

    static class ReflectionInfo {
        Map<String, Method> readMap = new HashMap();

        Map<String, Method> writeMap = new HashMap();

        Method getReadMethod(String prop) {
            return prop == null ? null : (Method) this.readMap.get(prop.toLowerCase());
        }

        Method getWriteMethod(String prop) {
            return prop == null ? null : (Method) this.writeMap.get(prop.toLowerCase());
        }
    }
}