package com.jgw.delingha.bean;

import java.io.Serializable;

public class BatchManagementBean implements Serializable {

    /**
     * batchId :
     * batchName :
     * id : 0
     * marketDate :
     * productId :
     * productName :
     */

    public String batchId;
    public String batchName;
    public Integer id;
    public String marketDate;
    public String produceDate;
    public String productId;
    public String productName;
    public String remark;
    public String getBatchNameStr() {
        return "批次名称：" + batchName;
    }

}
