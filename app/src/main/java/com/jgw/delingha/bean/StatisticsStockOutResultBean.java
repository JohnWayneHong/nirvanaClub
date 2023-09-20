package com.jgw.delingha.bean;

public class StatisticsStockOutResultBean extends StatisticsResultBean {


    public String productBatch;
    public String productCode;
    public String productName;
    public String singleNumber;
    public String receiveOrganizationName;

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
        return productBatch;
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
        return null;
    }

    @Override
    public String getWareHouseCodeIn() {
        return null;
    }

    @Override
    public String getWareHouseNameOut() {
        return null;
    }

    @Override
    public String getWareHouseCodeOut() {
        return null;
    }

    @Override
    public String getCustomerName() {
        return receiveOrganizationName;
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
