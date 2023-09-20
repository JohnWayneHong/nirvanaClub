package com.jgw.delingha.bean;

import android.text.TextUtils;

import java.util.List;

public class WareHouseBean {

    public List<Data> list;

    public static class Data {
        /**
         * wareHouseId : a5f6a1caa6d44f25a62f4128f525d019
         * wareHouseCode : CK-KH-000018
         * wareHouseName : 13000000028
         * areaCode : null
         * detailedAddress : null
         * manageId : null
         * manageName : null
         * managePhoneNumber : null
         * remark : null
         * storeHouseNumber : 0
         * emptyStoreHouseNumber : 0
         * isAllUser : N
         * disableFlag : 1
         * customInfoViews : []
         * wareHouseManages : []
         * wareHouseType : 1
         * customerId : fdcc913d60c947949d133bd9bf2c624c
         */

        public String wareHouseId;
        public String wareHouseCode;
        public String wareHouseName;
        public Object areaCode;
        public Object detailedAddress;
        public Object manageId;
        public Object manageName;
        public Object managePhoneNumber;
        public Object remark;
        public int storeHouseNumber;
        public int emptyStoreHouseNumber;
        public String isAllUser;
        public int disableFlag;
        public int wareHouseType;
        public String customerId;

        public String getWareHouseContentText() {
            if (TextUtils.isEmpty(wareHouseCode) || TextUtils.isEmpty(wareHouseName)) {
                return "无";
            }
            return "(" + wareHouseCode + ")" + wareHouseName;
        }
    }
}
