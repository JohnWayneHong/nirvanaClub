package com.jgw.delingha.bean;

import java.util.List;

/**
 * Created by XiongShaoWu
 * on 2019/12/26
 */
public class ProducePackageCodeInfoBean {

    public String firstNumberCode;
    public String firstNumberName;
    public String lastNumberCode;
    public String lastNumberName;
    public int codeLevel;
    public String packageSpecification;
    public List<String> sonCodeList;
    public String productId;
    public String productName;
    public String productCode;
    public String productBatchId;
    public String productBatch;
    public String productBatchName;
    public String outerCode;
    public String packageCode;
    public int hasSonQuantity;
    public int packageLevel;

    public String getPackageSpecification() {
        return "1" + firstNumberName + packageSpecification + lastNumberName;
    }

    public int getPackageSpecificationNumber() {
        return Integer.parseInt(packageSpecification);
    }
}
