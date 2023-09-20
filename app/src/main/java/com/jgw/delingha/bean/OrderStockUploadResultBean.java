package com.jgw.delingha.bean;

import java.util.List;

/**
 * 订单出入库上传结果返回对象
 */
public class OrderStockUploadResultBean {

    /**
     * failureMessage : [{"failureMessage":[],"outerCodeId":"","message":"","success":[],"allFailure":true,"allFailureMessage":""}]
     * outerCodeId :
     * message :
     * success : []
     * allFailure : true
     * allFailureMessage :
     */

    public String outerCodeId;
    public String message;
    public boolean allFailure;
    public String allFailureMessage;
    public List<FailureMessageBean> failureMessage;
    public List<?> success;

    public static class FailureMessageBean {
        /**
         * failureMessage : []
         * outerCodeId :
         * message :
         * success : []
         * allFailure : true
         * allFailureMessage :
         */

        public String outerCodeId;
        public String message;
        public boolean allFailure;
        public String allFailureMessage;
        public List<?> failureMessage;
        public List<?> success;
    }
}
