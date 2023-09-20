package com.jgw.delingha.sql.converter_bean;

import java.util.List;

public class ProductBean {

    public PaginationBean paginationBean;
    public List<Data> list;

    public static class PaginationBean {

        public int startNumber; // 起始页
        public int current;     // 当前页
        public int totalPage;   // 总页数
        public int pageSize;    // 每页多少条
        public int total;       // 总条数
    }

    public static class Data {

        public String productId;
        public String productSpecificationsCode;
        public String productSpecificationsName;
        public String productName;
        public String producLargeCategory;
        public String productBarcode;
        public String brandCode;
        public String brandName;
        public String producPrice;
        public String organizationId;
        public String producMiddleCategory;
        public String producSmallCategory;
        public int disableFlag;
        public String disableFlagName;
        public String organizationFullName;
        public String producLargeCategoryName;
        public String producMiddleCategoryName;
        public String producSmallCategoryName;
        public String productModel;
        public String productUrl;
        public String productCode;
        public String productSortLink;
        public String productSortId;
        public String qrCode;
        public int serialNumber;
        public String productSortName;
        public String productMarketing;

        public String getProductContentText() {
            return "(" + productCode + ")" + productName;
        }
    }
}
