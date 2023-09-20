package com.jgw.delingha.bean;

public class FailLogMenuBean {

    public int titleRes;
    public String icon;
    public int type;

    public FailLogMenuBean(int title, String icon, int typeId) {
        titleRes = title;
        this.icon = icon;
        type = typeId;
    }
}
