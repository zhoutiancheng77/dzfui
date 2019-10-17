package com.dzf.zxkj.base.utils;

import com.dzf.zxkj.common.utils.StringUtil;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;


class PropDescriptor {
    private Class<?> beanType;
    private Class<?> propType;
    private String name;
    private String baseName;

    public PropDescriptor(Class<?> beanType, Class<?> propType, String propName) {
        if (beanType == null) {
            throw new IllegalArgumentException("Bean Class can not be null!");
        }
        if (propName == null) {
            throw new IllegalArgumentException("Bean Property name can not be null!");
        }

        this.propType = propType;
        this.beanType = beanType;
        this.name = propName;
        if ((this.name.startsWith("m_")) && (this.name.length() > 2))
            this.baseName = StringUtil.capitalize(this.name.substring(2));
        else
            this.baseName = StringUtil.capitalize(propName);
    }

    public synchronized Method getReadMethod(Class<?> currBean) {
        String readMethodName = null;
        if ((this.propType == Boolean.TYPE) || (this.propType == null))
            readMethodName = "is" + this.baseName;
        else {
            readMethodName = "get" + this.baseName;
        }
        Class classStart = currBean;
        if (classStart == null) {
            classStart = this.beanType;
        }
        Method readMethod = findMemberMethod(classStart, readMethodName, 0, null);
        if ((readMethod == null) && (readMethodName.startsWith("is"))) {
            readMethodName = "get" + this.baseName;
            readMethod = findMemberMethod(classStart, readMethodName, 0, null);
        }
        if (readMethod != null) {
            int mf = readMethod.getModifiers();
            if (!Modifier.isPublic(mf)) {
                return null;
            }
            Class retType = readMethod.getReturnType();
            if (!this.propType.isAssignableFrom(retType)) {
                // Logger.warn("return type unmatch for get Method and property! : " + classStart.getName() + "." + this.name);
            }

        }

        return readMethod;
    }

    public synchronized Method getWriteMethod(Class<?> currBean) {
        String writeMethodName = null;
        if (this.propType == null) {
            this.propType = findPropertyType(getReadMethod(currBean), null);
        }
        if (writeMethodName == null) {
            writeMethodName = "set" + this.baseName;
        }
        Class classStart = currBean;
        if (classStart == null) {
            classStart = this.beanType;
        }
        Method writeMethod = findMemberMethod(classStart, writeMethodName, 1, new Class[]{this.propType == null ? null : this.propType});

        if (writeMethod != null) {
            int mf = writeMethod.getModifiers();
            if ((!Modifier.isPublic(mf)) || (Modifier.isStatic(mf))) {
                writeMethod = null;
            }
        }
        return writeMethod;
    }

    private Class<?> findPropertyType(Method readMethod, Method writeMethod) {
        Class propertyType = null;
        if (readMethod != null) {
            propertyType = readMethod.getReturnType();
        }
        if ((propertyType == null) && (writeMethod != null)) {
            Class[] params = writeMethod.getParameterTypes();
            propertyType = params[0];
        }
        return propertyType;
    }

    private Method findMemberMethod(Class<?> beanClass, String mName, int num, Class[] args) {
        Method foundM = null;
        Method[] methods = beanClass.getDeclaredMethods();
        if (methods.length < 0) {
            return null;
        }
        for (Method method : methods) {
            if (method.getName().equalsIgnoreCase(mName)) {
                Class[] paramTypes = method.getParameterTypes();
                if (paramTypes.length == num) {
                    boolean match = true;
                    for (int i = 0; i < num; i++) {
                        if (!args[i].isAssignableFrom(paramTypes[i])) {
                            match = false;
                            break;
                        }
                    }
                    if (match) {
                        foundM = method;
                        break;
                    }
                }
            }
        }

        if ((foundM == null) &&
                (beanClass.getSuperclass() != null)) {
            foundM = findMemberMethod(beanClass.getSuperclass(), mName, num, args);
        }

        return foundM;
    }

    public String getName() {
        return this.name;
    }
}