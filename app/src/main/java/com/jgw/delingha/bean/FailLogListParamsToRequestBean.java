package com.jgw.delingha.bean;

public class FailLogListParamsToRequestBean {
    public String houseList;
    public String date;
    public String code;
    public int logType;
    public int currentPage;

    public FailLogListParamsToRequestBean(String mhousetList, String mdate, String mcode, int mlogType, int mcurrenPage) {
        this.code = mcode;
        this.currentPage = mcurrenPage;
        this.date = mdate;
        this.houseList = mhousetList;
        this.logType = mlogType;
    }
}
