package com.jgw.delingha.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XiongShaoWu
 * on 2020/7/7
 */
public class OrderExchangeGoodsDetailsBean {


    /**
     * houseList :
     * operationType :
     * products : [{"goodsOutProductId":"","number":0,"productBatch":"","productBatchId":"","productCode":"","productId":"","productName":"","singleCodeNumber":0,"status":0,"unitCode":"","unitName":""}]
     * remark :
     * wareGoodsInCode :
     * wareGoodsInId :
     * wareGoodsInName :
     * wareGoodsOutCode :
     * wareGoodsOutId :
     * wareGoodsOutName :
     */

    public String houseList;
    public String operationType;
    public String remark;
    public String wareGoodsInCode;
    public String wareGoodsInId;
    public String wareGoodsInName;
    public String wareGoodsOutCode;
    public String wareGoodsOutId;
    public String wareGoodsOutName;
    public String createTime;
    public List<ProductsBean> products;

    public static class ProductsBean {
        /**
         * goodsOutProductId :
         * number : 0
         * productBatch :
         * productBatchId :
         * productCode :
         * productId :
         * productName :
         * singleCodeNumber : 0
         * status : 0
         * unitCode :
         * unitName :
         */

        public String goodsOutProductId;
        public int number;
        public String productBatch;
        public String productBatchId;
        public String productCode;
        public String productId;
        public String productName;
        public int singleCodeNumber;
        public int actualSingleCodeNumber;//已发数量
        public int status;
        public String unitCode;
        public String unitName;

        //商品扫码数量
        public List<OrderExchangeGoodsCodeBean> codeList=new ArrayList<>();
        public int scanSingleCodeNumber;

        public String getGroupNumber(){
            return number+unitName;
        }

        public String getSingleNumber(){
            return String.valueOf(singleCodeNumber);
        }

        public String getActualSingleCodeNumber(){
            return String.valueOf(actualSingleCodeNumber);
        }

        public String getScanSingleNumber(){
            return String.valueOf(scanSingleCodeNumber);
        }

    }
}
