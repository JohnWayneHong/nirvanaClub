package com.jgw.delingha.bean;

/**
 * Created by XiongShaoWu
 * on 2019/12/26
 */
public class PackageCodeInfoBean {


    /**
     * productClassifyId : null
     * productClassifyName :
     * productId : 08b10a54ef40422caa65fb5a03e1e93f
     * productName : 出入库产品1224
     * productCode : CP000280
     * productBatchId : d6208e819aea4bda964448c5ea19a023
     * productBatchName : 出入库产品1224批次
     * productBatchName : 出入库产品1224批次
     * wareHouseId : f884c1350ec34619a0c05b107e882d6f
     * wareHouseName : 13000000006
     * storeHouseId : null
     * storeHouseName : null
     * packageLevel : 2
     * productSpecificationsName : null
     * outerCodeId : 9000000263426152
     * outerCodeTypeId : 14
     * sonCodeNumber : 2
     * wareHouseCode : null
     * relationTypeCode : 018301018201
     * relationTypeName : 箱〈---关联---〉盒
     * lastNumber : 2
     */

    public Object productClassifyId;
    public String productClassifyName;
    public String productId;
    public String productName;
    public String productCode;
    public String productBatchId;
    public String productBatch;
    public String productBatchName;
    public String wareHouseId;
    public String wareHouseName;
    public Object storeHouseId;
    public Object storeHouseName;
    public int packageLevel;
    public Object productSpecificationsName;
    public String outerCodeId;
    public String outerCodeTypeId;
    public int sonCodeNumber;
    public Object wareHouseCode;
    public String relationTypeCode;
    public String relationTypeName;
    public int lastNumber;

    public String getPackageSpecification() {
        if (relationTypeName == null || relationTypeName.length() < 2) {
            return "";
        }
        String start_string = relationTypeName.substring(0, 1);
        String end_string = relationTypeName.substring(relationTypeName.length() - 1);
        //noinspection StringBufferReplaceableByString
        StringBuilder sb = new StringBuilder();
        sb.append(1);
        sb.append(start_string);
        sb.append(lastNumber);
        sb.append(end_string);
        return sb.toString();
    }
}
