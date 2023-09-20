package com.jgw.delingha.bean;

import android.text.TextUtils;

import java.util.List;

/**
 * author : Cxz
 * data : 2019/11/29
 * description :
 */
public class FlowQueryBean {
    /**
     * outerCodeId : 44009134931172716038
     * client : android
     * productBatchName : 三级专用
     * list : [{"createTime":"2019-06-20 09:42:53","operationId":"c227b286b54e4b8f986e52a78e75e50c","operationName":"零六","operationType":"030001","typeCode":"032101","typeName":"生产入库","houseList":"RK2019062009425200036","logisticsCompanyName":null,"logisticsNumber":null,"wareHouseName":"兔子仓库","orderTime":1560994973045},{"createTime":"2019-06-20 09:44:18","operationId":"c227b286b54e4b8f986e52a78e75e50c","operationName":"零六","operationType":"030001","typeCode":"032005","typeName":"销售出库","houseList":"CK2019062009441700100","logisticsCompanyName":null,"logisticsNumber":null,"wareHouseName":"兔子仓库","orderTime":1560995058287}]
     * product : {"productModel":"一级","productSortId":"61699a692aa64a85beb9ac4bab235cba","productId":"95990b4430a743e0ac6d9b23e43579cd","producMiddleCategory":"9aa2c3e10e974d989e07278807f183c1","producSmallCategory":"","productName":"三级商品（拖，包，只）","productSortLink":"手工","organizationId":"18355c8780094ca1b786464fb652f471","producLargeCategory":"1b4b400952754d0ea58c24f177c12d13","productSortName":"手工","productCode":"广东发货","qrCode":"125789f49d8448f5b71b95a6512c5747","productBarcode":"1703180001000","disableFlagName":"正常","disableFlag":1,"organizationFullName":"天门天升畜禽有限责任公司","productSpecificationsName":"100斤/袋","productUrl":"74a90c4e2aac4432bd76b01d523fdb50"}
     * productWareHouse : {"buyPrice":11.3,"sweepCodeOut":"N","buyPriceCode":"019001","productId":"95990b4430a743e0ac6d9b23e43579cd","costPrice":11.2,"warnUnitName":"托","productWareHouseId":"6ef2c082d0494179a6d2881189e12ba2","sweepCodeIn":"N","warnUnitCode":"018303","smallUnitName":"只","priceCode":"019001","packageLevel":"3","lowerWarnValue":1,"productPackageRatios":[{"lastNumberCode":"018204","firstNumberCode":"018303","productPackageRatioId":"afd3e4aed41a49c49e500b6d6a82b81c","firstNumber":1,"lastNumberName":"包","firstNumberName":"托","lastNumber":6},{"farentId":"afd3e4aed41a49c49e500b6d6a82b81c","lastNumberCode":"018110","firstNumberCode":"018204","productPackageRatioId":"74c0389f40044ab58eb7a007e8d89ecd","firstNumber":1,"lastNumberName":"只","firstNumberName":"包","lastNumber":5}],"buyPriceName":"元(￥)","customInfoViews":[],"smallUnitCode":"018110","priceName":"元(￥)"}
     * packageSpecification : 1托6包  1包5只
     */

    public String outerCodeId;
    public String productBatch;
    public ProductBean product;
    public String packageSpecification;
    public String organizationName;
    public String customerName;

    public List<FlowBean> list;


    public static class FlowBean {

        /**
         * createTime : 2019-06-20 09:42:53
         * operationId : c227b286b54e4b8f986e52a78e75e50c
         * operationName : 零六
         * operationType : 030001
         * typeCode : 032101
         * typeName : 生产入库
         * houseList : RK2019062009425200036
         * logisticsCompanyName : null
         * logisticsNumber : null
         * wareHouseName : 兔子仓库
         * orderTime : 1560994973045
         */

        public String createTime;
        public String operationId;
        public String operationName;
        public String operationType;
        public String typeCode;
        public String typeName;
        public String houseList;
        public Object logisticsCompanyName;
        public Object logisticsNumber;
        public String wareHouseName;
        public long orderTime;

        public String getIntroduction() {
            if (!TextUtils.isEmpty(wareHouseName)) {
                return operationName + " 将产品" + typeName + "到[" + wareHouseName + "]";
            }
            return "暂无入库记录";
        }

        public String getData() {
            String[] strings = createTime.split(" ");
            return strings[0];
        }

        public String getTime() {
            String[] strings = createTime.split(" ");
            return strings[1];
        }

        public String getHouseListText() {
            return "(" + houseList + ")";
        }
    }

    public static class ProductBean {
        /**
         * productModel : 一级
         * productSortId : 61699a692aa64a85beb9ac4bab235cba
         * productId : 95990b4430a743e0ac6d9b23e43579cd
         * producMiddleCategory : 9aa2c3e10e974d989e07278807f183c1
         * producSmallCategory :
         * productName : 三级商品（拖，包，只）
         * productSortLink : 手工
         * organizationId : 18355c8780094ca1b786464fb652f471
         * producLargeCategory : 1b4b400952754d0ea58c24f177c12d13
         * productSortName : 手工
         * productCode : 广东发货
         * qrCode : 125789f49d8448f5b71b95a6512c5747
         * productBarcode : 1703180001000
         * disableFlagName : 正常
         * disableFlag : 1
         * organizationFullName : 天门天升畜禽有限责任公司
         * productSpecificationsName : 100斤/袋
         * productUrl : 74a90c4e2aac4432bd76b01d523fdb50
         */

        public String productModel;
        public String productSortId;
        public String productId;
        public String producMiddleCategory;
        public String producSmallCategory;
        public String productName;
        public String productSortLink;
        public String organizationId;
        public String producLargeCategory;
        public String productSortName;
        public String productCode;
        public String qrCode;
        public String productBarcode;
        public String disableFlagName;
        public int disableFlag;
        public String organizationFullName;
        public String productSpecificationsName;
        public String productUrl;

    }
}
