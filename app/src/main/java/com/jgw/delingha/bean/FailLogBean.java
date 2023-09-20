package com.jgw.delingha.bean;

import android.text.TextUtils;

import com.jgw.delingha.utils.UnitcodeUtil;

import java.util.List;

public class FailLogBean {

    public List<ListBean> list;

    public static class ListBean {
        /**
         * failReason : I/O error on GET request for "http://platform-ms-codetest/outer/info/getChildCodeList": platform-ms-codetest; nested exception is java.net.UnknownHostException: platform-ms-codetest
         * parentOuterCodeId : 9000000266559630
         * outerCodeId : 9000000266559634
         * firstNumberCode : 018301
         * lastNumberCode : 018201
         * productName : 0106产品2
         * productCode : 9000000266559630
         * createTime : 2020-01-07 22:19:13
         * wareHouseName : null
         * wareHouseCode : null
         * receiveOrganizationName : null
         * receiveOrganizationCode : null
         * returnCustomerCode : null
         * returnCustomerName : null
         */

        public String failReason;
        public String parentOuterCodeId;
        public String outerCodeId;
        public String firstNumberCode;
        public String lastNumberCode;
        public String productName;
        public String productCode;
        public String createTime;
        public String wareHouseName;
        public String wareHouseCode;
        public String receiveOrganizationName;
        public String receiveOrganizationCode;
        public String returnCustomerCode;
        public String returnCustomerName;
        public String inCustomerCode; // 入客户编码
        public String inCustomerName; // 入客户名
        public String inWareHouseCode; // 入仓库编码
        public String inWareHouseName; // 入仓库名

        public String getRelatedLevelString() {
            if (TextUtils.isEmpty(firstNumberCode) || TextUtils.isEmpty(lastNumberCode)) {
                return "";
            }
            return UnitcodeUtil.getName(firstNumberCode) + "---" + UnitcodeUtil.getName(lastNumberCode);
        }
    }
}
