package com.jgw.delingha.bean;

/**
 * author : Cxz
 * data : 2020/3/11
 * description : 调货待执行配置展示页面
 */
public class ExchangeGoodsPendingConfigurationBean {

    public long id; //配置Id

    public String dataTime; //创建时间

    public String outCustomer; //调出客户

    public String inCustomer; //调入客户

    public boolean isSelect = false; //是否选择
}
