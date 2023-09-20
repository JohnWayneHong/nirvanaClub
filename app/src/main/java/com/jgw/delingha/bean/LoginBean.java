package com.jgw.delingha.bean;

public class LoginBean {
    private String mobile; //账号
    private String pwd;  //密码

    public LoginBean(String m, String pwd) {
        this.mobile = m;
        this.pwd = pwd;
    }

    public String getMobile() {
        return mobile;
    }

    public String getPwd() {
        return pwd;
    }
}
