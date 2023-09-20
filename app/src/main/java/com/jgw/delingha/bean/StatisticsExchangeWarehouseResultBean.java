package com.jgw.delingha.bean;

public class StatisticsExchangeWarehouseResultBean extends StatisticsResultBean {


    public String productCode;
    public String productName;
    public String singleNumber;
    public String outWareHouseName;
    public String inWareHouseName;

    @Override
    public String getSingleCodeNumber() {
        return singleNumber;
    }

    @Override
    public String getProductName() {
        return productName;
    }

    @Override
    public String getProductCode() {
        return productCode;
    }

    @Override
    public String getBatchName() {
        return null;
    }

    @Override
    public String getWareHouseName() {
        return null;
    }

    @Override
    public String getWareHouseCode() {
        return null;
    }

    @Override
    public String getWareHouseNameIn() {
        return inWareHouseName;
    }

    @Override
    public String getWareHouseCodeIn() {
        return null;
    }

    @Override
    public String getWareHouseNameOut() {
        return outWareHouseName;
    }

    @Override
    public String getWareHouseCodeOut() {
        return null;
    }

    @Override
    public String getCustomerName() {
        return null;
    }

    @Override
    public String getCustomerNameIn() {
        return null;
    }

    @Override
    public String getCustomerNameOut() {
        return null;
    }
}
