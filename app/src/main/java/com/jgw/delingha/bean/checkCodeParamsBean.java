package com.jgw.delingha.bean;


public class checkCodeParamsBean {
    private int checkType; // 1退货  2调货  3调仓
    private String outerCodeId;
    private String outCustomerId;
    private String wareHouseId;

    public checkCodeParamsBean(int type, String code, String id, boolean isWarehouse) {
        checkType = type;
        outerCodeId = code;
        if (isWarehouse) {
            wareHouseId = id;
        } else {
            outCustomerId = id;
        }
    }

    public int getCheckType() {
        return checkType;
    }

    public String getOutCustomerId() {
        return outCustomerId;
    }

    public String getOuterCodeId() {
        return outerCodeId;
    }

    public String getWareHouseId() {
        return wareHouseId;
    }
}
