package com.jgw.delingha.bean;

public class StatisticsStockInResultBean extends StatisticsResultBean {


    public String productBatch;
    public String productCode;
    public String productName;
    public String singleNumber;
    public String wareHouseName;
    public String wareHouseCode;

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
        return wareHouseName;
    }

    @Override
    public String getWareHouseCode() {
        return wareHouseCode;
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
