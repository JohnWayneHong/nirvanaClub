package com.jgw.delingha.bean;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class GBOrderStockInDetailsBean {

    /**
     * auditStatus : 0
     * createTime : 2020-09-22 15:26:43
     * inHouseDate : 2020-09-22 15:26:43
     * inHouseList : RK2020092215264340117
     * inHouseProducts : [{"firstInNumber":1,"firstInNumberUnitCode":"018101","firstInNumberUnitName":"个","harvestType":null,"inHouseProductId":"37d37c7212e1424bbea54d2efc68f516","marketDate":null,"packingSpecification":"","planFirstInNumber":0,"planSecondInNumber":0,"planThirdInNumber":0,"productBatch":"","productBatchId":"","productClassifyId":"","productClassifyName":"","productCode":"CP000028","productId":"f8d29ba835934040a00f4241890a5ff0","productName":"0922123","productSpecificationsName":"","productWareHouse":null,"secondInNumber":0,"secondInNumberUnitCode":"018201","secondInNumberUnitName":"盒","storeHouseId":"8580c1d81a92463993863c876bfbb531","storeHouseName":"123","sweepStatus":"N","thirdInNumber":0,"thirdInNumberUnitCode":"018301","thirdInNumberUnitName":"箱","wareHouseId":"fbe75bb2167e4818ab43feb7faebbbb3","wareHouseName":"0518仓库"}]
     * inTypeCode : 032101
     * inTypeName : 生产入库
     * isSweep : 1
     * operatorName : jinyu
     * supperCode :
     * supperId :
     * supperName :
     * wareHouseId : fbe75bb2167e4818ab43feb7faebbbb3
     * wareHouseInId : a0bd5796882241f0ae7bb53cbd4fcda8
     * wareHouseName : 0518仓库
     */

    public int auditStatus;
    public String createTime;
    public String inHouseDate;
    public String inHouseList;
    public String inTypeCode;
    public String inTypeName;
    public int isSweep;
    public String operatorName;
    public String supperCode;
    public String supperId;
    public String supperName;
    public String wareHouseId;
    public String wareHouseInId;
    public String wareHouseName;
    public String orderId;
    public int updateVersion;
    public List<ListBean> inHouseProducts;

    public static class ListBean {
        /**
         * firstInNumber : 1
         * firstInNumberUnitCode : 018101
         * firstInNumberUnitName : 个
         * harvestType : null
         * inHouseProductId : 37d37c7212e1424bbea54d2efc68f516
         * marketDate : null
         * packingSpecification :
         * planFirstInNumber : 0
         * planSecondInNumber : 0
         * planThirdInNumber : 0
         * productBatch :
         * productBatchId :
         * productClassifyId :
         * productClassifyName :
         * productCode : CP000028
         * productId : f8d29ba835934040a00f4241890a5ff0
         * productName : 0922123
         * productSpecificationsName :
         * productWareHouse : null
         * secondInNumber : 0
         * secondInNumberUnitCode : 018201
         * secondInNumberUnitName : 盒
         * storeHouseId : 8580c1d81a92463993863c876bfbb531
         * storeHouseName : 123
         * sweepStatus : N
         * thirdInNumber : 0
         * thirdInNumberUnitCode : 018301
         * thirdInNumberUnitName : 箱
         * wareHouseId : fbe75bb2167e4818ab43feb7faebbbb3
         * wareHouseName : 0518仓库
         */
        public int actualSingleCodeNumber;
        public int singleCodeNumber;

        public int firstInNumber;
        public String firstInNumberUnitCode;
        public String firstInNumberUnitName;
        public Object harvestType;
        public String inHouseProductId;
        public Object marketDate;
        public String packingSpecification;
        public int planFirstInNumber;
        public int planSecondInNumber;
        public int planThirdInNumber;
        public String productBatch;
        public String productBatchId;
        public String productClassifyId;
        public String productClassifyName;
        public String productCode;
        public String productId;
        public String productName;
        public String productSpecificationsName;
        public Object productWareHouse;
        public int secondInNumber;
        public String secondInNumberUnitCode;
        public String secondInNumberUnitName;
        public String storeHouseId;
        public String storeHouseName;
        public String sweepStatus;
        public int thirdInNumber;
        public String thirdInNumberUnitCode;
        public String thirdInNumberUnitName;
        public String wareHouseId;
        public String wareHouseName;
        public List<OrderStockScanBean> codeList = new ArrayList<>();

        public String getPlanNumber() {
            ArrayList<String> allUnit = new ArrayList<>();
            allUnit.add(planFirstInNumber + firstInNumberUnitName);
            if (!TextUtils.isEmpty(secondInNumberUnitName)) {
                allUnit.add(planSecondInNumber + secondInNumberUnitName);
            }
            if (!TextUtils.isEmpty(thirdInNumberUnitName)) {
                allUnit.add(planThirdInNumber + thirdInNumberUnitName);
            }

            return getFilterString(allUnit);
        }

        public String getCurrentNumber() {
            ArrayList<String> allUnit = new ArrayList<>();

            int first = 0;
            int second = 0;
            int third = 0;
            for (OrderStockScanBean b : codeList) {
                first += b.firstLevel;
                second += b.secondLevel;
                third += b.thirdLevel;
            }
            if (!TextUtils.isEmpty(thirdInNumberUnitName)) {
                allUnit.add(third + thirdInNumber + thirdInNumberUnitName);
            }
            if (!TextUtils.isEmpty(secondInNumberUnitName)) {
                allUnit.add(second + secondInNumber + secondInNumberUnitName);
            }
            allUnit.add(first + firstInNumber + firstInNumberUnitName);

            return getFilterString(allUnit);
        }

        public String getFilterString(List<String> allUnit) {
            StringBuilder sb = new StringBuilder();

            ArrayList<String> temp = new ArrayList<>();
            for (int i = 0; i < allUnit.size(); i++) {
                String s = allUnit.get(i);
                if (!TextUtils.equals("0", s)) {
                    temp.add(s);
                }
            }
            for (int i = 0; i < temp.size(); i++) {
                String s = temp.get(i);
                sb.append(s);
                if (i != temp.size() - 1) {
                    sb.append("、");
                }
            }
            return sb.toString();
        }
    }
}
