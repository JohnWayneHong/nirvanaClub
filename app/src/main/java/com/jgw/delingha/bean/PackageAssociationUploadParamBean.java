package com.jgw.delingha.bean;

import java.util.List;

/**
 * 包装关联上传参数对象
 */
public class PackageAssociationUploadParamBean implements Cloneable {

    public String productId;
    public String productName;
    public String productCode;
    public String productBatchId;
    public String productBatchName;
    public String remark;

    /*与后台对接后这两个字段改为firstNumberCode和lastNumberCode
    public String relationTypeCode;
    public String relationTypeName;
    */
    public String firstNumberCode;
    public String lastNumberCode;

    //箱码
    public String packageCode;
    //设置界面中选择的包装级别
    public int packageLevel;
    public String packageSpecification;
    public List<String> codeList;
    //0为满箱 1为非满箱
    public int notFullBox = 0;
    //CodeTypeUtils.PackageAssociationType
    public int packageSceneType;
    //本地数据库首条插入id 仅用作查询需要上传的code 本身无需上传
    public long firstId;
    public String taskId;

    @Override
    public String toString() {
        return "PackageAssociationUploadParamBean{" +
                "productId='" + productId + '\'' +
                ", productName='" + productName + '\'' +
                ", productCode='" + productCode + '\'' +
                ", productBatchId='" + productBatchId + '\'' +
                ", productBatchName='" + productBatchName + '\'' +
                ", remark='" + remark + '\'' +
                ", firstNumberCode='" + firstNumberCode + '\'' +
                ", lastNumberCode='" + lastNumberCode + '\'' +
                ", packageCode='" + packageCode + '\'' +
                ", packageLevel=" + packageLevel +
                ", packageSpecification='" + packageSpecification + '\'' +
                ", codeList=" + codeList +
                ", notFullBox=" + notFullBox +
                ", packageSceneType=" + packageSceneType +
                ", firstId=" + firstId +
                ", taskId=" + taskId +
                '}';
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        //子码列表每次会重新赋值 此处浅拷贝即可
        return super.clone();
    }
}
