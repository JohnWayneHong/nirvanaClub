package com.jgw.delingha.bean;

import java.util.List;

/**
 * 盘点成功码信息
 */
public class InventoryCodeBean {

    /**
     * list : [{"createTime":"","currentLevel":0,"houseList":"","inventoryProductId":"","inventoryReason":"","operatorName":"","outerCodeId":"","productCode":"","productName":"","status":0,"unitName":"","wareHouseCode":"","wareHouseName":""}]
     * pagination : {"current":0,"pageSize":0,"startNumber":0,"total":0,"totalPage":0}
     */

    public PaginationBean pagination;
    public List<ListBean> list;

    public static class PaginationBean {
        /**
         * current : 0
         * pageSize : 0
         * startNumber : 0
         * total : 0
         * totalPage : 0
         */

        public int current;
        public int pageSize;
        public int startNumber;
        public int total;
        public int totalPage;
    }

    public static class ListBean {
        /**
         * createTime :
         * currentLevel : 0
         * houseList :
         * inventoryProductId :
         * inventoryReason :
         * operatorName :
         * outerCodeId :
         * productCode :
         * productName :
         * status : 0
         * unitName :
         * wareHouseCode :
         * wareHouseName :
         */

        public String createTime;
        public int currentLevel;
        public String houseList;
        public String inventoryProductId;
        public String inventoryReason;
        public String operatorName;
        public String outerCodeId;
        public String productCode;
        public String productName;
        public int status;
        public String unitName;
        public String wareHouseCode;
        public String wareHouseName;
    }
}
