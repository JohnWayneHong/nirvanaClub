package com.jgw.delingha.bean;

import java.util.List;

/**
 * author : Cxz
 * data : 2019/12/17
 * description : 产品仓储信息
 */
public class ProductWareMessage {

    /**
     * productWareHouseId : 443578e3f71247e6819a0e6d81502e1d
     * productId : 91f999b484dd413099ab29027017ee71
     * packageLevel : 2
     * smallUnitCode : 018101
     * smallUnitName : 个
     * costPrice : 12
     * buyPrice : 12
     * priceCode : 019001
     * priceName : 元(￥)
     * sweepCodeIn : Y
     * sweepCodeOut : Y
     * lowerWarnValue : 321
     * warnUnitCode : 018101
     * warnUnitName : 个
     * customInfoViews : []
     * productPackageRatios : [{"productPackageRatioId":"cac1a6f80165430b9976379b0a944aca","firstNumber":1,"firstNumberCode":"018201","firstNumberName":"盒","lastNumber":123,"lastNumberCode":"018101","lastNumberName":"个","farentId":null,"packageSpecificationName":null,"relationTypeName":null,"relationTypeCode":null}]
     * buyPriceCode : 019002
     * buyPriceName : 美元($)
     */

    public String productWareHouseId;
    public String productId;
    public String packageLevel;
    public String smallUnitCode;
    public String smallUnitName;
    public int costPrice;
    public int buyPrice;
    public String priceCode;
    public String priceName;
    public String sweepCodeIn;
    public String sweepCodeOut;
    public int lowerWarnValue;
    public String warnUnitCode;
    public String warnUnitName;
    public String buyPriceCode;
    public String buyPriceName;
    public List<?> customInfoViews;
    public List<ProductPackageRatiosBean> productPackageRatios;


    public static class ProductPackageRatiosBean {
        /**
         * productPackageRatioId : cac1a6f80165430b9976379b0a944aca
         * firstNumber : 1
         * firstNumberCode : 018201
         * firstNumberName : 盒
         * lastNumber : 123
         * lastNumberCode : 018101
         * lastNumberName : 个
         * farentId : null
         * packageSpecificationName : null
         * relationTypeName : null
         * relationTypeCode : null
         */

        public String productPackageRatioId;
        public int firstNumber;
        public String firstNumberCode;
        public String firstNumberName;
        public int lastNumber;
        public String lastNumberCode;
        public String lastNumberName;
        public String farentId;
        public String packageSpecificationName;
        public String relationTypeName;
        public String relationTypeCode;

        public String getLevel() {
            return firstNumberName + "-" + lastNumberName;
        }
    }
}
