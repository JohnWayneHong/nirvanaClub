package com.jgw.delingha.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : J-T
 * @date : 2022/7/21 15:31
 * description :最近退货码查询
 */
public class CodeStatusQueryStockReturnBean extends BaseCodeStatusQueryBean {

    /**
     * codeTypeId :
     * createTime :
     * currentLevel : 0
     * cusOrgId :
     * houseList :
     * inStoreHouseId :
     * inStoreHouseName :
     * inWareHouseId :
     * inWareHouseName :
     * number : 0
     * operationType :
     * operator :
     * operatorName :
     * organizationId :
     * outWareHouseId :
     * outWareHouseName :
     * outerCodeId :
     * productBatch :
     * productBatchId :
     * productCode :
     * productId :
     * productName :
     * productSpecifications :
     * remark :
     * returnCustomerCode :
     * returnCustomerId :
     * returnCustomerName :
     * returnGoodsCodeId :
     * returnGoodsId :
     * returnGoodsProductId :
     * singleCodeNumber : 0
     * status : 0
     * unitCode :
     * unitName :
     */

    public String createTime;
    public String houseList;
    public String inWareHouseName;
    public String operatorName;
    public String outerCodeId;
    public String productBatch;
    public String productCode;
    public String productName;
    public String returnCustomerCode;
    public String returnCustomerName;
    public String singleCodeNumber;
    public String unitName;

    @Override
    public List<CodeStatusQueryInfoItemBean> getInfoList() {
        ArrayList<CodeStatusQueryInfoItemBean> list = new ArrayList<>();
        CodeStatusQueryInfoItemBean itemBean;
        itemBean = new CodeStatusQueryInfoItemBean("最近操作时间", createTime);
        list.add(itemBean);

        itemBean = new CodeStatusQueryInfoItemBean("单据编号", houseList);
        list.add(itemBean);

        itemBean = new CodeStatusQueryInfoItemBean("身份码", outerCodeId);
        list.add(itemBean);

        itemBean = new CodeStatusQueryInfoItemBean("单位", unitName);
        list.add(itemBean);

        itemBean = new CodeStatusQueryInfoItemBean("子码数量", singleCodeNumber);
        list.add(itemBean);

        itemBean = new CodeStatusQueryInfoItemBean("产品名称", productName);
        list.add(itemBean);

        itemBean = new CodeStatusQueryInfoItemBean("产品编码", productCode);
        list.add(itemBean);

        itemBean = new CodeStatusQueryInfoItemBean("产品批次", productBatch);
        list.add(itemBean);
        itemBean = new CodeStatusQueryInfoItemBean("退货客户", returnCustomerName);
        list.add(itemBean);

        itemBean = new CodeStatusQueryInfoItemBean("客户编码", returnCustomerCode);
        list.add(itemBean);

        itemBean = new CodeStatusQueryInfoItemBean("收货仓库", inWareHouseName);
        list.add(itemBean);

        return list;
    }
}
