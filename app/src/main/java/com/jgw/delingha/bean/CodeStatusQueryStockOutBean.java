package com.jgw.delingha.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : J-T
 * @date : 2022/7/21 15:25
 * description :最近出库码查询
 */
public class CodeStatusQueryStockOutBean extends BaseCodeStatusQueryBean {

    /**
     * codeTypeId :
     * createTime :
     * cusOrgId :
     * logisticsCompanyCode :
     * logisticsCompanyName :
     * logisticsNumber :
     * operator :
     * operatorName :
     * organizationId :
     * organizationName :
     * outHouseList :
     * outNumberUnitCode :
     * outNumberUnitName :
     * outTypeCode :
     * outTypeName :
     * outerCodeId :
     * productBatch :
     * productBatchId :
     * productClassifyId :
     * productClassifyName :
     * productCode :
     * productId :
     * productName :
     * receiveOrganizationCode :
     * receiveOrganizationId :
     * receiveOrganizationName :
     * remark :
     * singleNumber : 0
     * storeHouseId :
     * storeHouseName :
     * sweepLevel : 0
     * sweepOutCodeId :
     * sweepOutProductId :
     * wareHouseCode :
     * wareHouseId :
     * wareHouseName :
     */

    public String createTime;
    public String logisticsCompanyName;
    public String logisticsNumber;
    public String operatorName;
    public String outHouseList;
    public String outNumberUnitName;
    public String outerCodeId;
    public String productBatch;
    public String productCode;
    public String productName;
    public String receiveOrganizationCode;
    public String receiveOrganizationName;
    public String singleNumber;
    public String wareHouseName;

    @Override
    public List<CodeStatusQueryInfoItemBean> getInfoList() {
        ArrayList<CodeStatusQueryInfoItemBean> list = new ArrayList<>();
        CodeStatusQueryInfoItemBean itemBean;
        itemBean = new CodeStatusQueryInfoItemBean("最近操作时间", createTime);
        list.add(itemBean);

        itemBean = new CodeStatusQueryInfoItemBean("单据编号", outHouseList);
        list.add(itemBean);

        itemBean = new CodeStatusQueryInfoItemBean("身份码", outerCodeId);
        list.add(itemBean);

        itemBean = new CodeStatusQueryInfoItemBean("单位", outNumberUnitName);
        list.add(itemBean);

        itemBean = new CodeStatusQueryInfoItemBean("子码数量", singleNumber);
        list.add(itemBean);

        itemBean = new CodeStatusQueryInfoItemBean("产品名称", productName);
        list.add(itemBean);

        itemBean = new CodeStatusQueryInfoItemBean("产品编码", productCode);
        list.add(itemBean);

        itemBean = new CodeStatusQueryInfoItemBean("产品批次", productBatch);
        list.add(itemBean);

        itemBean = new CodeStatusQueryInfoItemBean("发货仓库", wareHouseName);
        list.add(itemBean);

        itemBean = new CodeStatusQueryInfoItemBean("客户名称", receiveOrganizationName);
        list.add(itemBean);

        itemBean = new CodeStatusQueryInfoItemBean("客户编码", receiveOrganizationCode);
        list.add(itemBean);

        itemBean = new CodeStatusQueryInfoItemBean("物流公司", logisticsCompanyName);
        list.add(itemBean);

        itemBean = new CodeStatusQueryInfoItemBean("物流单号", logisticsNumber);
        list.add(itemBean);

        itemBean = new CodeStatusQueryInfoItemBean("操作人", operatorName);
        list.add(itemBean);
        return list;
    }
}
