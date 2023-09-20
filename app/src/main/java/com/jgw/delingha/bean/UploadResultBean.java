package com.jgw.delingha.bean;

public class UploadResultBean {

    public int success;
    public int error;

    public UploadResultBean(int success, int error) {
        this.success = success;
        this.error = error;
    }
}
