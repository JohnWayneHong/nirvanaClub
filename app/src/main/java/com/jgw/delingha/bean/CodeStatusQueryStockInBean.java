package com.jgw.delingha.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : J-T
 * @date : 2022/7/21 15:23
 * description :最近入库码查询
 */
public class CodeStatusQueryStockInBean extends BaseCodeStatusQueryBean {

    /**
     * codeTypeId :
     * createTime :
     * cusOrgId :
     * harvestType : 0
     * inHouseList :
     * inNumberUnitCode :
     * inNumberUnitName :
     * inTypeCode :
     * inTypeName :
     * operationType :
     * operator :
     * operatorName :
     * organizationId :
     * organizationName :
     * outerCodeId :
     * productBatch :
     * productBatchId :
     * productClassifyId :
     * productClassifyName :
     * productCode :
     * productId :
     * productName :
     * productSpecificationsName :
     * receiveOrganizationName :
     * remark :
     * sendOrganizationName :
     * singleNumber : 0
     * storeHouseId :
     * storeHouseName :
     * sweepInCodeId :
     * sweepInProductId :
     * sweepLevel : 0
     * updateTime :
     * wareHouseCode :
     * wareHouseId :
     * wareHouseName :
     */

    public String createTime;
    public String inHouseList;
    public String inNumberUnitName;
    public String operatorName;
    public String outerCodeId;
    public String productBatch;
    public String productCode;
    public String productName;
    public String singleNumber;
    public String wareHouseName;

    @Override
    public List<CodeStatusQueryInfoItemBean> getInfoList() {
        ArrayList<CodeStatusQueryInfoItemBean> list = new ArrayList<>();
        CodeStatusQueryInfoItemBean itemBean;
        itemBean = new CodeStatusQueryInfoItemBean("最近操作时间", createTime);
        list.add(itemBean);

        itemBean = new CodeStatusQueryInfoItemBean("单据编号", inHouseList);
        list.add(itemBean);

        itemBean = new CodeStatusQueryInfoItemBean("身份码", outerCodeId);
        list.add(itemBean);

        itemBean = new CodeStatusQueryInfoItemBean("单位", inNumberUnitName);
        list.add(itemBean);

        itemBean = new CodeStatusQueryInfoItemBean("子码数量", singleNumber);
        list.add(itemBean);

        itemBean = new CodeStatusQueryInfoItemBean("产品名称", productName);
        list.add(itemBean);

        itemBean = new CodeStatusQueryInfoItemBean("产品编码", productCode);
        list.add(itemBean);

        itemBean = new CodeStatusQueryInfoItemBean("产品批次", productBatch);
        list.add(itemBean);

        itemBean = new CodeStatusQueryInfoItemBean("仓库名称", wareHouseName);
        list.add(itemBean);

        itemBean = new CodeStatusQueryInfoItemBean("操作人", operatorName);
        list.add(itemBean);
        return list;
    }
}
