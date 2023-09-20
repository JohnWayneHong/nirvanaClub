package com.jgw.delingha.bean;

/**
 * 本地已支持的功能对象
 */
public class LocalFunctionBean {
    private String appAuthCode;
    private Class<?> functionClass;

    public String getAppAuthCode() {
        return appAuthCode;
    }

    public void setAppAuthCode(String appAuthCode) {
        this.appAuthCode = appAuthCode;
    }

    public Class<?> getFunctionClass() {
        return functionClass;
    }

    public void setFunctionClass(Class<?> functionClass) {
        this.functionClass = functionClass;
    }
}
