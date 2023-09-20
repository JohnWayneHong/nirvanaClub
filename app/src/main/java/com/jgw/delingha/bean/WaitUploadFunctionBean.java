package com.jgw.delingha.bean;

import com.jgw.delingha.sql.operator.BaseOperator;

/**
 * 本地已支持的待上传功能对象
 */
public class WaitUploadFunctionBean {
    private String appAuthCode;
    private Class<?> functionClass;
    private BaseOperator<?> operator;

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

    public BaseOperator<?> getOperator() {
        return operator;
    }

    public void setOperator(BaseOperator<?> operator) {
        this.operator = operator;
    }
}
